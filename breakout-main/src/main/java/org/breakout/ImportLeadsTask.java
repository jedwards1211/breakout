package org.breakout;

import static org.andork.swing.async.SelfReportingTask.callSelfReportingSubtask;
import static org.andork.util.StringUtils.isNullOrEmpty;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableColumnModel;

import org.andork.awt.GridBagWizard;
import org.andork.awt.I18n.Localizer;
import org.andork.collect.HashSetMultiMap;
import org.andork.collect.MultiMap;
import org.andork.io.CSVFormat;
import org.andork.swing.FromEDT;
import org.andork.swing.JOptionPaneBuilder;
import org.andork.swing.OnEDT;
import org.andork.swing.WizardPanel;
import org.andork.swing.table.BetterJTable;
import org.andork.swing.table.ListTableColumn;
import org.andork.swing.table.ListTableModel;
import org.andork.task.Task;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.util.StringUtils;
import org.breakout.leadimport.LeadListParser;
import org.breakout.leadimport.LeadListParser.Context;
import org.breakout.leadimport.LeadListParser.ListTable;
import org.breakout.leadimport.LeadListParser.Table;
import org.breakout.model.ProjectModel;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.raw.SurveyLead;
import org.breakout.model.raw.SurveyRow;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

import com.github.krukow.clj_lang.PersistentVector;

public class ImportLeadsTask extends Task<Void> {
	private static final Logger logger = Logger.getLogger(ImportCompassTask.class.getSimpleName());
	private BreakoutMainView mainView;

	public ImportLeadsTask(BreakoutMainView mainView) {
		this.mainView = mainView;
		localizer = mainView.getI18n().forClass(ImportLeadsTask.class);
		setStatus(localizer.getString("status.root"));
		setIndeterminate(false);
		setTotal(3);
	}

	private final Localizer localizer;
	private String cave = null;
	private File[] csvFiles;

	class Info {
		final Set<String> caveSet = new HashSet<>();
		final MultiMap<String, String> stations = new HashSetMultiMap<>();
		int i;
		int total;

		List<String> caves;

		void clear() {
			caveSet.clear();
			stations.clear();
			i = 0;
		}

		void addCave(String cave) {
			if (cave != null)
				caveSet.add(cave);
		}

		void add(SurveyRow row) {
			if (!isNullOrEmpty(row.getFromStation())) {
				String cave = row.getFromCave();
				if (cave == null)
					cave = "";
				stations.put(cave, row.getFromStation());
				caveSet.add(cave);
			}
			if (!isNullOrEmpty(row.getToStation())) {
				String cave = row.getToCave();
				if (cave == null)
					cave = "";
				stations.put(cave, row.getToStation());
				caveSet.add(cave);
			}
		}

		void finish() {
			caves = new ArrayList<>(caveSet);
			Collections.sort(caves);
		}
	}

	private Info getInfo() throws Exception {
		Info info = new Info();

		SurveyTableModel model = FromEDT.fromEDT(() -> {
			SurveyTableModel m = mainView.getSurveyTable().getModel();
			info.total = m.getRowCount();
			return m;
		});

		TableModelListener changeListener = new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				info.clear();
				info.total = model.getRowCount();
			}
		};

		OnEDT.onEDT(() -> {
			model.addTableModelListener(changeListener);
		});

		try {
			callSelfReportingSubtask(this, 1, mainView.getMainPanel(), task -> {
				task.setStatus(localizer.getString("status.analyzing"));
				while (info.i < info.total) {
					task.setCompleted(info.i);
					int end = Math.min(info.total, info.i + 1000);
					OnEDT.onEDT(() -> {
						while (info.i < end) {
							SurveyRow row = model.getRow(info.i++);
							info.add(row);
						}
					});
					if (task.isCanceled())
						return;
					task.setCompleted(end);
				}
			});

			if (isCanceled())
				return info;

			info.finish();

			return info;
		} finally {
			OnEDT.onEDT(() -> {
				model.removeTableModelListener(changeListener);
			});
		}
	}

	@SuppressWarnings("serial")
	private void selectOptions(Info info) {
		WizardPanel wizardPanel = new WizardPanel(mainView.getI18n());
		wizardPanel.setUseNextButton(false);

		GridBagWizard w;

		List<String> caves = info.caves;
		if (caves.size() > 1) {
			w = GridBagWizard.quickPanel();
			JLabel caveLabel = new JLabel();
			localizer.setText(caveLabel, "optionsDialog.caveLabel.text");

			@SuppressWarnings("unchecked")
			JList<String> caveList = new JList<>(new ListComboBoxModel<>(caves));
			caveList.setCellRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(
					JList<?> list,
					Object value,
					int index,
					boolean isSelected,
					boolean cellHasFocus) {
					return super.getListCellRendererComponent(
						list,
						StringUtils.isNullOrEmpty(value) ? "(no cave)" : value,
						index,
						isSelected,
						cellHasFocus);
				}
			});
			ListSelectionModel selModel = caveList.getSelectionModel();
			selModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			selModel.addListSelectionListener(e -> {
				if (e.getValueIsAdjusting())
					return;
				cave = caveList.getSelectedValue();
			});
			caveList.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (caveList.getSelectedValue() != null) {
						wizardPanel.next();
					}
				}
			});
			w.put(caveLabel).xy(0, 0).fillx(1);
			w.put(caveList).below(caveLabel).fillboth(1, 1).insets(10, 0, 0, 0);
			wizardPanel.addCard(w.getTarget());
		}
		else {
			cave = "";
		}

		JFileChooser fileChooser = mainView.fileChooser(ProjectModel.importLeadsDirectory, null);
		fileChooser.setControlButtonsAreShown(false);
		fileChooser.setMultiSelectionEnabled(true);
		FileFilter csvFilter = new FileNameExtensionFilter("Comma-separated values (*.csv)", "csv");
		fileChooser.addChoosableFileFilter(csvFilter);
		fileChooser.setFileFilter(csvFilter);

		wizardPanel.addCard(fileChooser);

		int choice =
			wizardPanel
				.showDialog(
					mainView.getMainPanel(),
					localizer.getString("optionsDialog.title"),
					WizardPanel.linkFileChooser(fileChooser));
		if (choice != JOptionPane.OK_OPTION)
			return;

		mainView.saveFileChooserDirectory(fileChooser, ProjectModel.importLeadsDirectory, null);

		csvFiles = fileChooser.getSelectedFiles();
	}

	private List<SurveyLead> parseCsvFiles(File[] files, String cave, Info info) throws Exception {
		return callSelfReportingSubtask(this, 1, mainView.getMainPanel(), subtask -> {
			subtask.setStatus(localizer.getString("status.importing"));
			List<SurveyLead> leads = new ArrayList<>();
			subtask.setTotal(files.length);
			for (File file : files) {
				subtask.setStatus(localizer.getFormattedString("status.importingFile", file.toString()));
				try {
					parseCsvFile(file, cave, info, leads);
				}
				catch (Exception ex) {
					logger.log(Level.SEVERE, "Failed to import leads", ex);
					OnEDT.onEDT(() -> {
						JOptionPane
							.showMessageDialog(
								this.mainView.getMainPanel(),
								ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage(),
								"Failed to import leads",
								JOptionPane.ERROR_MESSAGE);
					});
				}
				subtask.increment();
			}
			return leads;
		});
	}

	private void parseCsvFile(File file, String cave, Info info, List<SurveyLead> leads) throws IOException {
		CSVFormat csv = new CSVFormat();
		List<List<String>> rows = csv.parse(Files.newBufferedReader(file.toPath()));
		Table table = new ListTable(rows);
		Context context = new Context() {
			@Override
			public boolean isStationName(String s) {
				return info.stations.contains(cave, s);
			}

			@Override
			public boolean widthComesFirst() {
				return false;
			}

			@Override
			public Unit<Length> defaultLengthUnit() {
				return Objects.requireNonNull(mainView.getProjectModel().get(ProjectModel.displayLengthUnit));
			}
		};

		for (SurveyLead lead : LeadListParser.parse(table, context)) {
			leads.add(lead.withMutations(l -> l.setCave(cave).set("file", file)));
		}
	}

	private static final ListTableModel.Column<SurveyLead, ?> doneColumn =
		ListTableModel.column(SurveyLead.Properties.done);
	private static final ListTableModel.Column<SurveyLead, ?> stationColumn =
		ListTableModel.column(SurveyLead.Properties.station);
	private static final ListTableModel.Column<SurveyLead, ?> heightColumn =
		ListTableModel.column(SurveyLead.Properties.height);
	private static final ListTableModel.Column<SurveyLead, ?> widthColumn =
		ListTableModel.column(SurveyLead.Properties.width);
	private static final ListTableModel.Column<SurveyLead, ?> descriptionColumn =
		ListTableModel.column(SurveyLead.Properties.description);

	private static final List<ListTableModel.Column<SurveyLead, ?>> leadColumns =
		Arrays.asList(doneColumn, stationColumn, heightColumn, widthColumn, descriptionColumn);

	private List<SurveyLead> confirmResults(List<SurveyLead> leads, Info info) {
		return FromEDT.fromEDT(() -> {
			ListTableModel<SurveyLead> dataModel = new ListTableModel<>(leadColumns, leads);
			dataModel.setEditable(false);
			BetterJTable dataTable = new BetterJTable(dataModel);
			DefaultTableColumnModel colModel = new DefaultTableColumnModel();
			colModel
				.addColumn(
					new ListTableColumn<>(doneColumn)
						.headerValue(localizer.getString("dataTable.doneColumn.headerValue"))
						.preferredWidth(75));
			colModel
				.addColumn(
					new ListTableColumn<>(stationColumn)
						.headerValue(localizer.getString("dataTable.stationColumn.headerValue"))
						.preferredWidth(125));
			colModel
				.addColumn(
					new ListTableColumn<>(heightColumn)
						.headerValue(localizer.getString("dataTable.heightColumn.headerValue"))
						.alignRight()
						.preferredWidth(75));
			colModel
				.addColumn(
					new ListTableColumn<>(widthColumn)
						.headerValue(localizer.getString("dataTable.widthColumn.headerValue"))
						.alignRight()
						.preferredWidth(75));
			colModel
				.addColumn(
					new ListTableColumn<>(descriptionColumn)
						.headerValue(localizer.getString("dataTable.descriptionColumn.headerValue"))
						.preferredWidth(600));
			dataTable.setColumnModel(colModel);
			ListTableColumn.updateModelIndices(dataTable);

			JScrollPane dataTableScroller = new JScrollPane(dataTable);

			dataTableScroller.setPreferredSize(new Dimension(800, 600));

			final String accept = localizer.getString("confirmDialog.acceptButton.text");
			final String cancel = localizer.getString("confirmDialog.cancelButton.text");
			int choice =
				new JOptionPaneBuilder()
					.message(dataTableScroller)
					.defaultOption()
					.options(accept, cancel)
					.initialValue(accept)
					.showDialog(mainView.getMainPanel(), localizer.getString("confirmDialog.title"));

			if (choice != 0)
				return null;

			return leads;
		});
	}

	private void mergeIntoProject(List<SurveyLead> leads) throws Exception {
		OnEDT.onEDT(() -> {
			mainView.getProjectModel().set(ProjectModel.leads, PersistentVector.create(leads));
		});
	}

	@Override
	protected Void work() throws Exception {
		try {
			Info info = getInfo();
			if (isCanceled())
				return null;

			OnEDT.onEDT(() -> selectOptions(info));
			if (csvFiles == null || isCanceled())
				return null;

			List<SurveyLead> leads = parseCsvFiles(csvFiles, cave, info);
			if (isCanceled())
				return null;
			if (leads.isEmpty()) {
				OnEDT.onEDT(() -> {
					JOptionPane.showMessageDialog(mainView.getMainPanel(), localizer.getString("message.nothingFound"));
				});
				return null;
			}

			leads = confirmResults(leads, info);
			if (leads == null)
				return null;

			mergeIntoProject(leads);

			return null;
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Failed to import leads", ex);
			OnEDT.onEDT(() -> {
				JOptionPane
					.showMessageDialog(
						this.mainView.getMainPanel(),
						ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage(),
						"Failed to import leads",
						JOptionPane.ERROR_MESSAGE);
			});
		}

		return null;
	}
}
