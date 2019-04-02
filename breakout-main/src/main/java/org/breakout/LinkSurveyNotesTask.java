package org.breakout;

import static org.andork.swing.async.SelfReportingTask.callSelfReportingSubtask;
import static org.andork.util.StringUtils.isNullOrEmpty;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultRowSorter;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.andork.awt.GridBagWizard;
import org.andork.awt.I18n.I18nUpdater;
import org.andork.awt.I18n.Localizer;
import org.andork.collect.HashSetMultiMap;
import org.andork.collect.MultiMap;
import org.andork.q.QArrayList;
import org.andork.swing.FromEDT;
import org.andork.swing.JOptionPaneBuilder;
import org.andork.swing.OnEDT;
import org.andork.swing.table.ListTableModel;
import org.andork.swing.table.ListTableModel.Column;
import org.andork.swing.table.ListTableModel.ColumnBuilder;
import org.andork.task.Task;
import org.breakout.model.ProjectModel;
import org.breakout.model.RootModel;
import org.breakout.model.ShotKey;
import org.breakout.model.StationKey;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.parsed.ProjectParser;
import org.breakout.model.raw.SurveyRow;
import org.breakout.model.raw.SurveyTrip;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

public class LinkSurveyNotesTask extends Task<Void> {
	private BreakoutMainView mainView;

	public LinkSurveyNotesTask(BreakoutMainView mainView) {
		this.mainView = mainView;
		localizer = mainView.getI18n().forClass(LinkSurveyNotesTask.class);
		setStatus(localizer.getString("status.root"));
		setIndeterminate(false);
		setTotal(3);
	}
	
	private static final Pattern lettersNumbersPattern = Pattern.compile("^([\\p{L}]+)(\\d+)$");
	private final Localizer localizer;

	private String cave = null;
	private File searchDirectory = null;
		
	class Info {
		private final Set<String> caveSet = new HashSet<>();
		private int i;
		private int total;

		public List<String> caves;
		public final List<SurveyRow> allShots = new ArrayList<>();
		public final Map<StationKey, SurveyRow> stationRows = new HashMap<>();
		public final MultiMap<StationKey, SurveyRow> stationRowsByDesignation = new HashSetMultiMap<>();

		public final Map<SurveyTrip, Map<Path, Integer>> potentialTripLinks = new HashMap<>();
		public List<Path> linkedFiles = new ArrayList<>();
		public List<Path> unlinkedFiles = new ArrayList<>();
		
		public void addCave(String cave) {
			if (cave != null) caveSet.add(cave);
		}
		
		public void addStationRow(StationKey key, SurveyRow row) {
			SurveyRow existing = stationRows.get(key);
			if (existing != null) {
				if (existing.getTrip() == row.getTrip()) return;
				if (Objects.compare(
					ProjectParser.parseDate(row.getTrip().getDate()),
					ProjectParser.parseDate(existing.getTrip().getDate()),
					Date::compareTo) >= 0) {
					return;
				}
			}
			stationRows.put(key, row);
			
			Matcher m = lettersNumbersPattern.matcher(key.station);
			if (m.find()) {
				stationRowsByDesignation.put(new StationKey(key.cave, m.group(1)), row);
			}
		}

		public void addPotentialTripLink(SurveyTrip trip, Path file, Integer stationCount) {
			Map<Path, Integer> counts = potentialTripLinks.get(trip);
			if (counts == null) {
				counts = new HashMap<Path, Integer>();
				potentialTripLinks.put(trip, counts);
			}
			counts.put(file, stationCount);
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
				info.caveSet.clear();
				info.stationRows.clear();
				info.i = 0;
				info.total = model.getRowCount();
			}
		};
		
		OnEDT.onEDT(() -> { model.addTableModelListener(changeListener); });

		try {
			callSelfReportingSubtask(this, 1, mainView.getMainPanel(), task -> {
				task.setStatus(localizer.getString("status.analyzing"));
				while (info.i < info.total) {
					task.setCompleted(info.i);
					int end = Math.min(info.total, info.i + 1000);
					OnEDT.onEDT(() -> {
						while (info.i < end) {
							SurveyRow row = model.getRow(info.i++);
							if (isShot(row)) info.allShots.add(row);
							if (isNullOrEmpty(row.getSurveyNotes())) {
								info.addCave(row.getFromCave());
								info.addCave(row.getToCave());
								if (isShot(row)) {
									info.addStationRow(new StationKey(row.getFromCave(), row.getFromStation()), row);
									info.addStationRow(new StationKey(row.getToCave(), row.getToStation()), row);
								}
							}
						}
					});
					if (task.isCanceled()) return;
					task.setCompleted(end);
				}
			});
			
			if (isCanceled()) return info;
			
			info.caves = new ArrayList<>(info.caveSet);
			Collections.sort(info.caves);

			return info;
		} finally {
			OnEDT.onEDT(() -> { model.removeTableModelListener(changeListener); });
		}
	}
	
	private boolean isShot(SurveyRow row) {
		return !isNullOrEmpty(row.getFromStation()) &&
			!isNullOrEmpty(row.getToStation()) &&
			!isNullOrEmpty(row.getDistance());
	}
	
	private void selectOptions(Info info) {
		searchDirectory = null;

		JPanel panel = new JPanel();
		GridBagWizard w = GridBagWizard.create(panel);
		
		JLabel fileChooserLabel = new JLabel();
		localizer.setText(fileChooserLabel, "optionsDialog.fileChooserLabel.text");

		JFileChooser fileChooser = new JFileChooser(mainView.getRootModel().get(RootModel.currentWallsImportDirectory));
		fileChooser.setControlButtonsAreShown(false);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		JLabel caveLabel = new JLabel();
		localizer.setText(caveLabel, "optionsDialog.caveLabel.text");
		
		List<String> caves = info.caves;
		@SuppressWarnings("unchecked")
		JComboBox<String> caveComboBox = new JComboBox<>(
			new ListComboBoxModel<>(caves)
		);
		if (!caves.isEmpty()) caveComboBox.setSelectedIndex(0);
		caveComboBox.setEnabled(caves.size() > 1);
		
		Box namingSchemeBox = Box.createVerticalBox();
		ButtonGroup namingSchemeGroup = new ButtonGroup();
		JLabel namingSchemeLabel = new JLabel();
		localizer.setText(namingSchemeLabel, "namingSchemeLabel.text");
		namingSchemeBox.add(namingSchemeLabel);
		JToggleButton lechSchemeButton = new JToggleButton();
		localizer.setText(lechSchemeButton, "lechSchemeButton.text");
		namingSchemeBox.add(lechSchemeButton);
		namingSchemeGroup.add(lechSchemeButton);
		
		lechSchemeButton.setSelected(true);
		
		w.put(caveLabel).xy(0, 0).fillx(0);
		w.put(caveComboBox).below(caveLabel).fillx(0);
		w.put(namingSchemeBox).below(caveComboBox).fillboth(0, 0);
		w.put(fileChooserLabel).rightOf(caveLabel).fillx(1).insets(0, 20, 0, 0);
		w.put(fileChooser).below(fileChooserLabel).height(2).fillboth(1, 1).sameInsets(fileChooserLabel);
		
		int choice = new JOptionPaneBuilder()
			.message(panel)
			.okCancel()
			.showDialog(mainView.getMainPanel(), localizer.getString("optionsDialog.title"));
	
		if (choice != JOptionPane.OK_OPTION) return;
		
		cave = caveComboBox.getSelectedItem().toString();
		searchDirectory = fileChooser.getCurrentDirectory();
	}
	
	private List<SurveyRow> findSurveyNotes(Path directory, String caveName, Info info) throws Exception {
		return callSelfReportingSubtask(this, 1, mainView.getMainPanel(), subtask -> {
			subtask.setStatus(localizer.getString("status.scanning"));
			subtask.setIndeterminate(true);
			
			final Matcher leadsMatcher = Pattern.compile("\\blead", Pattern.CASE_INSENSITIVE).matcher("");

			try (Stream<Path> files = Files.find(
				directory, 10,
				(Path path, BasicFileAttributes attrs) -> {
					String fileName = path.getFileName().toString();
					if (fileName.startsWith(".") || leadsMatcher.reset(fileName).find()) {
						return false;
					}
					return extensionMatcher.reset(fileName).find();
				},
				FileVisitOption.FOLLOW_LINKS)) {
				
				subtask.onceCanceled(files::close);

				files.forEach(file -> {
					subtask.setStatus(localizer.getFormattedString("status.scanningFile", file.toString()));
					String fileName = file.getFileName().toString();
					Set<String> stations;
					try {
						 stations = LechuguillaStationSets.parse(fileName.replaceAll("^[^A-Z]+|\\.[^.]*$", ""));
					} catch (Exception e) {
						return;
					}
					
					Map<SurveyTrip, Integer> trips = new HashMap<>();
					
					stations.forEach(station -> {
						StationKey key = new StationKey(caveName, station);
						SurveyRow existing = info.stationRows.get(key);
						if (existing != null && existing.getTrip() != null) {
							increment(trips, existing.getTrip());
							return;
						}
						Collection<SurveyRow> rows = info.stationRowsByDesignation.get(key);
						if (rows != null) {
							for (SurveyRow row : rows) {
								if (row != null && row.getTrip() != null) {
									increment(trips, row.getTrip());
								}
							}
						}
					});
					
					SurveyTrip bestMatch = getHighestCountKey(trips);
					if (bestMatch != null) {
						info.addPotentialTripLink(bestMatch, file, trips.get(bestMatch));
						info.linkedFiles.add(file);
					} else {
						info.unlinkedFiles.add(file);
					}
				});
			}
			
			IdentityHashMap<SurveyTrip, SurveyTrip> newTrips = new IdentityHashMap<>();

			info.potentialTripLinks.forEach((trip, paths) -> {
				Path notesFile = getHighestCountKey(paths);
				if (notesFile != null) newTrips.put(trip, trip.setSurveyNotes(notesFile.getFileName().toString()));
			});
			
			List<SurveyRow> result = new ArrayList<>();
			
			info.allShots.forEach(row -> {
				if (row.getTrip() == null) return;
				SurveyTrip newTrip = newTrips.get(row.getTrip());
				if (newTrip != null) {
					result.add(row.setTrip(newTrip));
				}
			});

			return result;
		});
	}
	
	private static <K> K getHighestCountKey(Map<K, Integer> m) {
		K result = null;
		int bestCount = -1;
		
		for (Map.Entry<K, Integer> e : m.entrySet()) {
			if (result == null || e.getValue() > bestCount) {
				result = e.getKey();
				if (e.getValue() == null) {
					Thread.dumpStack();
				}
				bestCount = e.getValue();
			}
		}
		return result;
	}
	
	private static <K> void increment(Map<K, Integer> m, K key) {
		Integer value = m.get(key);
		if (value == null) value = 0;
		m.put(key, value + 1);
	}
	
	private List<SurveyRow> confirmResults(List<SurveyRow> rows, Info info) {
		return FromEDT.fromEDT(() -> {
			JTabbedPane tabs = new JTabbedPane();

			final I18nUpdater<LinkSurveyNotesTask> i18nUpdater = new I18nUpdater<LinkSurveyNotesTask>() {
				@Override
				public void updateI18n(Localizer localizer, LinkSurveyNotesTask localizedObject) {
					tabs.setTitleAt(0, localizer.getString("resultsDialog.tabs.data.title"));
					tabs.setTitleAt(1, localizer.getString("resultsDialog.tabs.linkedFiles.title"));
					tabs.setTitleAt(2, localizer.getString("resultsDialog.tabs.unlinkedFiles.title"));
				}
			};


			SurveyTableModel dataModel = new SurveyTableModel(rows);
			SurveyTable dataTable = new SurveyTable();
			dataTable.setAspect(SurveyTable.Aspect.LINK_SURVEY_NOTES);
			dataTable.setModel(dataModel);
			
			JScrollPane dataTableScroller = new JScrollPane(dataTable);
			
			tabs.addTab("Data", dataTableScroller);
			tabs.addTab("Linked Files", createFileTable(info.linkedFiles));
			tabs.addTab("Unlinked Files", createFileTable(info.unlinkedFiles));

			tabs.setPreferredSize(new Dimension(800, 600));
			
			localizer.register(LinkSurveyNotesTask.this, i18nUpdater);
			
			final String accept = localizer.getString("confirmDialog.acceptButton.text");
			final String cancel = localizer.getString("confirmDialog.cancelButton.text");
			int choice = new JOptionPaneBuilder()
				.message(tabs)
				.defaultOption()
				.options(accept, cancel)
				.initialValue(accept)
				.showDialog(mainView.getMainPanel(), localizer.getString("confirmDialog.title"));
		
			if (choice != 0) return null;
			
			ListSelectionModel selModel = dataTable.getModelSelectionModel();
			if (dataTable.getSelectedRowCount() == 0) return rows;
			List<SurveyRow> result = new ArrayList<>(rows);
			Iterator<SurveyRow> iter = result.iterator();
			for (int i = 0; iter.hasNext(); iter.next(), i++) {
				if (!selModel.isSelectedIndex(i)) iter.remove();
			}
			return result;
		});
	}
	
	private Component createFileTable(List<Path> files) {
		List<Column<Path, Path>> columns = Arrays.asList(
			new ColumnBuilder<Path, Path>()
				.columnClass(Path.class)
				.columnName(localizer.getString("fileTable.columns.directory.name"))
				.getter(p -> p.getParent())
				.create(),
			new ColumnBuilder<Path, Path>()
				.columnClass(Path.class)
				.columnName(localizer.getString("fileTable.columns.file.name"))
				.getter(p -> p.getFileName())
				.create()
		);
		TableModel tableModel = new ListTableModel<>(columns, files);
		JTable table = new JTable();
		table.setAutoCreateColumnsFromModel(true);
		table.setAutoCreateRowSorter(true);
		table.setModel(tableModel);
		return new JScrollPane(table);
	}
	
	private void mergeIntoProject(List<SurveyRow> rows) throws Exception {
		callSelfReportingSubtask(this, 1, mainView.getMainPanel(), (Task<?> subtask) -> {
			subtask.setStatus(localizer.getString("status.merging"));
			subtask.setTotal(rows.size());
			
			while (subtask.getCompleted() < rows.size()) {
				int end = (int) Math.min(rows.size(), subtask.getCompleted() + 1000);
				OnEDT.onEDT(() -> {
					SurveyTableModel model = mainView.getSurveyTable().getModel();
					for (int i = (int) subtask.getCompleted(); i < end; i++) {
						SurveyRow row = rows.get(i);
						Integer index = mainView.shotKeyToModelIndex.get(new ShotKey(row));
						if (index == null) return;
						model.setRow(index,
							model.getRow(index)
								.setOverrideSurveyNotes(row.getSurveyNotes()));
					}

				});
				if (subtask.isCanceled()) return;
				subtask.setCompleted(end);
			}
		});
	}
	
	private void addSearchDirectoryToProject(File directory) {
		OnEDT.onEDT(() -> {
			QArrayList<File> surveyScanPaths = mainView.getProjectModel().get(ProjectModel.surveyScanPaths);
			if (surveyScanPaths == null) {
				surveyScanPaths = new QArrayList<File>();
			}
			if (!surveyScanPaths.contains(directory)) {
				surveyScanPaths.add(directory);
			}
		});
	}

	private final Matcher extensionMatcher =
		Pattern.compile("\\.(bmp|jpe?g|png|pdf|e?ps|xps|gif|tiff?|[we]mf)$", Pattern.CASE_INSENSITIVE)
		.matcher("");

	@Override
	protected Void work() throws Exception {
		Info info = getInfo();
		if (isCanceled()) return null;

		OnEDT.onEDT(() -> selectOptions(info));
		if (searchDirectory == null) return null;

		List<SurveyRow> rows = findSurveyNotes(searchDirectory.toPath(), cave, info);
		if (isCanceled()) return null;
		if (rows.isEmpty()) {
			OnEDT.onEDT(() -> {
				JOptionPane.showMessageDialog(
					mainView.getMainPanel(),
					localizer.getString("message.nothingFound"));
			});
			return null;
		}
		
		Collections.sort(info.linkedFiles);
		Collections.sort(info.unlinkedFiles);
		
		rows = confirmResults(rows, info);
		if (rows == null) return null;
					
		addSearchDirectoryToProject(searchDirectory);
		
		mergeIntoProject(rows);
		mainView.rebuild3dModel.run();
		
		return null;
	}
}
