package org.breakout;

import static org.andork.swing.async.SelfReportingTask.callSelfReportingSubtask;
import static org.andork.util.StringUtils.isNullOrEmpty;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
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
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.andork.awt.GridBagWizard;
import org.andork.awt.I18n.I18nUpdater;
import org.andork.awt.I18n.Localizer;
import org.andork.collect.HashSetMultiMap;
import org.andork.collect.MultiMap;
import org.andork.immutable.MutableArrayList;
import org.andork.q.QArrayList;
import org.andork.swing.FromEDT;
import org.andork.swing.JOptionPaneBuilder;
import org.andork.swing.OnEDT;
import org.andork.swing.WizardPanel;
import org.andork.swing.filechooser.DirectoryFileFilter;
import org.andork.swing.table.ListTableModel;
import org.andork.swing.table.ListTableModel.Column;
import org.andork.swing.table.ListTableModel.ColumnBuilder;
import org.andork.task.Task;
import org.andork.util.Comparables;
import org.andork.util.StringUtils;
import org.breakout.model.ProjectModel;
import org.breakout.model.ShotKey;
import org.breakout.model.StationKey;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.parsed.ProjectParser;
import org.breakout.model.raw.SurveyRow;
import org.breakout.model.raw.SurveyTrip;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

public class LinkSurveyNotesTask extends Task<Void> {
	private static final Logger logger = Logger.getLogger(ImportCompassTask.class.getSimpleName());
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

	private boolean caseSensitive = true;
	private boolean linkDirectories = false;
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
		public Set<Path> unlinkedFilesSet = new HashSet<>();

		public void addCave(String cave) {
			if (cave != null)
				caveSet.add(cave);
		}

		public void addStationRow(StationKey key, SurveyRow row) {
			SurveyRow existing = stationRows.get(key);
			if (existing != null) {
				if (existing.getTrip() == row.getTrip())
					return;
				if (Comparables
					.compareNullsLast(
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

		public void buildRowIndex() {
			for (SurveyRow row : allShots) {
				String fromStation = row.getFromStation();
				String toStation = row.getToStation();
				if (!caseSensitive) {
					fromStation = fromStation.toUpperCase();
					toStation = toStation.toUpperCase();
				}
				addStationRow(new StationKey(row.getFromCave(), fromStation), row);
				addStationRow(new StationKey(row.getToCave(), toStation), row);
			}
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
							if (isShot(row))
								info.allShots.add(row);
							if (row.getAttachedFiles() == null || row.getAttachedFiles().isEmpty()) {
								info.addCave(row.getFromCave());
								info.addCave(row.getToCave());
							}
						}
					});
					if (task.isCanceled())
						return;
					task.setCompleted(end);
				}
			});

			if (isCanceled())
				return info;

			info.caves = new ArrayList<>(info.caveSet);
			Collections.sort(info.caves);

			return info;
		} finally {
			OnEDT.onEDT(() -> {
				model.removeTableModelListener(changeListener);
			});
		}
	}

	private boolean isShot(SurveyRow row) {
		return !isNullOrEmpty(row.getFromStation())
			&& !isNullOrEmpty(row.getToStation())
			&& !isNullOrEmpty(row.getDistance());
	}

	@SuppressWarnings("serial")
	private void selectOptions(Info info) {
		searchDirectory = null;

		WizardPanel wizardPanel = new WizardPanel(mainView.getI18n());
		wizardPanel.setUseNextButton(false);

		GridBagWizard w;

		List<String> caves = info.caves;
		if (caves.size() > 1) {
			w = GridBagWizard.quickPanel();
			JLabel caveLabel = new JLabel();
			localizer.setText(caveLabel, "optionsDialog.caveLabel.text");

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
			cave = null;
		}

		Box namingSchemeBox = Box.createVerticalBox();
		ButtonGroup namingSchemeGroup = new ButtonGroup();
		JLabel namingSchemeLabel = new JLabel();
		localizer.setText(namingSchemeLabel, "namingSchemeLabel.text");
		namingSchemeBox.add(namingSchemeLabel);
		JToggleButton lechSchemeButton = new JToggleButton();
		localizer.setText(lechSchemeButton, "lechSchemeButton.text");
		namingSchemeBox.add(lechSchemeButton);
		namingSchemeGroup.add(lechSchemeButton);

		lechSchemeButton.setSelected(false);
		lechSchemeButton.addActionListener(e -> {
			wizardPanel.next();
		});

		wizardPanel.addCard(GridBagWizard.wrap(namingSchemeBox));

		Box linkDirectoriesBox = Box.createVerticalBox();
		JLabel linkDirectoriesLabel = new JLabel();
		localizer.setText(linkDirectoriesLabel, "optionsDialog.linkDirectoriesLabel.text");
		linkDirectoriesBox.add(linkDirectoriesLabel);
		ButtonGroup linkDirectoriesGroup = new ButtonGroup();
		JToggleButton linkDirectoriesButton = new JToggleButton();
		JToggleButton dontLinkDirectoriesButton = new JToggleButton();
		localizer.setText(dontLinkDirectoriesButton, "optionsDialog.dontLinkDirectoriesButton.text");
		localizer.setText(linkDirectoriesButton, "optionsDialog.linkDirectoriesButton.text");
		linkDirectoriesBox.add(dontLinkDirectoriesButton);
		linkDirectoriesBox.add(linkDirectoriesButton);
		linkDirectoriesGroup.add(linkDirectoriesButton);
		linkDirectoriesGroup.add(dontLinkDirectoriesButton);
		linkDirectoriesButton.addActionListener(e -> {
			linkDirectories = true;
			wizardPanel.next();
		});
		dontLinkDirectoriesButton.addActionListener(e -> {
			linkDirectories = false;
			wizardPanel.next();
		});
		wizardPanel.addCard(GridBagWizard.wrap(linkDirectoriesBox));

		Box caseSensitiveBox = Box.createVerticalBox();
		JLabel caseSensitiveLabel = new JLabel();
		localizer.setText(caseSensitiveLabel, "optionsDialog.caseSensitiveLabel.text");
		caseSensitiveBox.add(caseSensitiveLabel);
		ButtonGroup caseSensitivityGroup = new ButtonGroup();
		JToggleButton caseSensitiveButton = new JToggleButton();
		JToggleButton caseInsensitiveButton = new JToggleButton();
		localizer.setText(caseSensitiveButton, "optionsDialog.caseSensitiveButton.text");
		localizer.setText(caseInsensitiveButton, "optionsDialog.caseInsensitiveButton.text");
		caseSensitiveBox.add(caseInsensitiveButton);
		caseSensitiveBox.add(caseSensitiveButton);
		caseSensitivityGroup.add(caseSensitiveButton);
		caseSensitivityGroup.add(caseInsensitiveButton);
		caseSensitiveButton.addActionListener(e -> {
			caseSensitive = true;
			wizardPanel.next();
		});
		caseInsensitiveButton.addActionListener(e -> {
			caseSensitive = false;
			wizardPanel.next();
		});
		wizardPanel.addCard(GridBagWizard.wrap(caseSensitiveBox));

		JLabel fileChooserLabel = new JLabel();
		localizer.setText(fileChooserLabel, "optionsDialog.fileChooserLabel.text");

		JFileChooser fileChooser = mainView.fileChooser(ProjectModel.linkSurveyNotesDirectory, null);
		fileChooser.setControlButtonsAreShown(false);
		fileChooser.setMultiSelectionEnabled(false);
		DirectoryFileFilter.install(fileChooser);

		w = GridBagWizard.quickPanel();
		w.put(fileChooserLabel).xy(0, 0).fillx(1).insets(0, 20, 0, 0);
		w.put(fileChooser).below(fileChooserLabel).height(2).fillboth(1, 1).sameInsets(fileChooserLabel);

		wizardPanel.addCard(w.getTarget());

		int choice =
			wizardPanel
				.showDialog(
					mainView.getMainPanel(),
					localizer.getString("optionsDialog.title"),
					WizardPanel.linkFileChooser(fileChooser));
		if (choice != JOptionPane.OK_OPTION)
			return;

		searchDirectory = DirectoryFileFilter.getSelectedDirectory(fileChooser);
		mainView.saveFileChooserDirectory(fileChooser, ProjectModel.linkSurveyNotesDirectory, null);
	}

	private List<SurveyRow> findSurveyNotes(Path directory, String caveName, Info info) throws Exception {
		return callSelfReportingSubtask(this, 1, mainView.getMainPanel(), subtask -> {
			subtask.setStatus(localizer.getString("status.scanning"));
			subtask.setIndeterminate(true);

			final Matcher leadsMatcher = Pattern.compile("\\blead", Pattern.CASE_INSENSITIVE).matcher("");

			Files
				.walkFileTree(
					directory,
					Collections.singleton(FileVisitOption.FOLLOW_LINKS),
					10,
					new FileVisitor<Path>() {
						Stack<Boolean> hasChildDirs = new Stack<>();
						Stack<Boolean> hasFiles = new Stack<>();

						@Override
						public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
							throws IOException {
							if (!hasChildDirs.isEmpty() && !hasChildDirs.peek()) {
								hasChildDirs.pop();
								hasChildDirs.push(true);
							}
							hasChildDirs.push(false);
							hasFiles.push(false);
							return FileVisitResult.CONTINUE;
						}

						public boolean fileMatches(Path path) {
							String fileName = path.getFileName().toString();
							if (fileName.startsWith(".") || leadsMatcher.reset(fileName).find()) {
								return false;
							}
							return extensionMatcher.reset(fileName).find();
						}

						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							if (fileMatches(file)) {
								if (!hasFiles.isEmpty() && !hasFiles.peek()) {
									hasFiles.pop();
									hasFiles.push(true);
								}
								addPath(file);
							}
							return FileVisitResult.CONTINUE;
						}

						public void addPath(Path file) {
							subtask.setStatus(localizer.getFormattedString("status.scanningFile", file.toString()));
							String fileName = file.getFileName().toString();
							if (!caseSensitive)
								fileName = fileName.toUpperCase();
							Set<String> stations;
							try {
								stations =
									LechuguillaStationSets
										.parse(
											fileName
												.replaceAll(
													"^[^A-Z]+|\\.[^.]*|\\b\\d{2}(\\d{2})?([-/_])\\d\\d?\\2\\d\\d?\\b$",
													""));
							}
							catch (Exception e) {
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
							}
							info.unlinkedFilesSet.add(file);
						}

						@Override
						public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
							boolean hadChildDirs = hasChildDirs.pop();
							boolean hadFiles = hasFiles.pop();
							if (linkDirectories && !hadChildDirs && hadFiles) {
								addPath(dir);
							}
							return FileVisitResult.CONTINUE;
						}
					});

			IdentityHashMap<SurveyTrip, SurveyTrip> newTrips = new IdentityHashMap<>();

			info.potentialTripLinks.forEach((trip, paths) -> {
				Path notesFile = getHighestCountKey(paths, p -> !paths.containsKey(p.getParent()));
				if (notesFile != null) {
					newTrips
						.put(
							trip,
							trip
								.setAttachedFiles(
									new MutableArrayList<String>()
										.add(notesFile.getFileName().toString())
										.toImmutable()));
					info.linkedFiles.add(notesFile);
					info.unlinkedFilesSet.remove(notesFile);
				}
			});

			info.unlinkedFiles.addAll(info.unlinkedFilesSet);
			Collections.sort(info.linkedFiles);
			Collections.sort(info.unlinkedFiles);

			List<SurveyRow> result = new ArrayList<>();

			info.allShots.forEach(row -> {
				if (row.getTrip() == null)
					return;
				SurveyTrip newTrip = newTrips.get(row.getTrip());
				if (newTrip != null) {
					result.add(row.setTrip(newTrip));
				}
			});

			return result;
		});
	}

	private static <K> K getHighestCountKey(Map<K, Integer> m) {
		return getHighestCountKey(m, k -> true);
	}

	private static <K> K getHighestCountKey(Map<K, Integer> m, Predicate<K> p) {
		K result = null;
		int bestCount = -1;

		for (Map.Entry<K, Integer> e : m.entrySet()) {
			if (!p.test(e.getKey()))
				continue;
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
		if (value == null)
			value = 0;
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
			int choice =
				new JOptionPaneBuilder()
					.message(tabs)
					.defaultOption()
					.options(accept, cancel)
					.initialValue(accept)
					.showDialog(mainView.getMainPanel(), localizer.getString("confirmDialog.title"));

			if (choice != 0)
				return null;

			ListSelectionModel selModel = dataTable.getModelSelectionModel();
			if (dataTable.getSelectedRowCount() == 0)
				return rows;
			List<SurveyRow> result = new ArrayList<>(rows);
			Iterator<SurveyRow> iter = result.iterator();
			for (int i = 0; iter.hasNext(); iter.next(), i++) {
				if (!selModel.isSelectedIndex(i))
					iter.remove();
			}
			return result;
		});
	}

	private Component createFileTable(List<Path> files) {
		List<Column<Path, Path>> columns =
			Arrays
				.asList(
					new ColumnBuilder<Path, Path>()
						.columnClass(Path.class)
						.columnName(localizer.getString("fileTable.columns.directory.name"))
						.getter(p -> p.getParent())
						.create(),
					new ColumnBuilder<Path, Path>()
						.columnClass(Path.class)
						.columnName(localizer.getString("fileTable.columns.file.name"))
						.getter(p -> p.getFileName())
						.create());
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
						if (index == null)
							return;
						model
							.setRow(
								index,
								model.getRow(index).setOverrideAttachedFiles(row.getOverrideAttachedFiles()));
					}

				});
				if (subtask.isCanceled())
					return;
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
		Pattern.compile("\\.(bmp|jpe?g|png|pdf|e?ps|xps|gif|tiff?|[we]mf)$", Pattern.CASE_INSENSITIVE).matcher("");

	@Override
	protected Void work() throws Exception {
		try {
			Info info = getInfo();
			if (isCanceled())
				return null;

			OnEDT.onEDT(() -> selectOptions(info));
			if (searchDirectory == null)
				return null;

			info.buildRowIndex();

			List<SurveyRow> rows = findSurveyNotes(searchDirectory.toPath(), cave, info);
			if (isCanceled())
				return null;
			if (rows.isEmpty()) {
				OnEDT.onEDT(() -> {
					JOptionPane.showMessageDialog(mainView.getMainPanel(), localizer.getString("message.nothingFound"));
				});
				return null;
			}

			rows = confirmResults(rows, info);
			if (rows == null)
				return null;

			addSearchDirectoryToProject(searchDirectory);

			mergeIntoProject(rows);
			mainView.rebuild3dModel.run();

			return null;
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Failed to link survey notes", ex);
			OnEDT.onEDT(() -> {
				JOptionPane
					.showMessageDialog(
						this.mainView.getMainPanel(),
						ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage(),
						"Failed to link survey notes",
						JOptionPane.ERROR_MESSAGE);
			});
		}

		return null;
	}
}
