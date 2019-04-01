package org.breakout;

import static org.andork.swing.async.SelfReportingTask.callSelfReportingSubtask;
import static org.andork.util.StringUtils.isNullOrEmpty;

import java.awt.Dimension;
import java.io.File;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
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

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.andork.collect.HashSetMultiMap;
import org.andork.collect.MultiMap;
import org.andork.q.QArrayList;
import org.andork.swing.FromEDT;
import org.andork.swing.JOptionPaneBuilder;
import org.andork.swing.OnEDT;
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
		setStatus("Link Survey Notes");
		setIndeterminate(false);
		setTotal(3);
	}
	
	private static final Pattern lettersNumbersPattern = Pattern.compile("^([\\p{L}]+)(\\d+)$");
		
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
				task.setStatus("analyzing data");
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
	
	private String selectCave(List<String> caves) {
		return FromEDT.fromEDT(() -> {
			JComboBox<String> comboBox = new JComboBox<>(
				new ListComboBoxModel<>(caves)
			);
			int choice = new JOptionPaneBuilder()
				.message(comboBox)
				.okCancel()
				.question()
				.showDialog(mainView.getMainPanel(), "Select Cave");
			if (choice != JOptionPane.OK_OPTION) return null;
			return (String) comboBox.getSelectedItem();
		}); 
	}
	
	private File selectSearchDirectory() {
		return FromEDT.fromEDT(() -> {
			JFileChooser fileChooser = new JFileChooser(mainView.getRootModel().get(RootModel.currentWallsImportDirectory));
			fileChooser.setDialogTitle("Select Search Directory");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = fileChooser.showDialog(mainView.getMainPanel(), "Select");
			return result == JFileChooser.APPROVE_OPTION
				? fileChooser.getSelectedFile()
				: null;
		});
	}
	
	private List<SurveyRow> findSurveyNotes(Path directory, String caveName, Info info) throws Exception {
		return callSelfReportingSubtask(this, 1, mainView.getMainPanel(), subtask -> {
			subtask.setStatus("scanning files");
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
					subtask.setStatus("scanning " + file);
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
	
	private List<SurveyRow> confirmResults(List<SurveyRow> rows) {
		return FromEDT.fromEDT(() -> {
			SurveyTableModel resultsModel = new SurveyTableModel(rows);
			SurveyTable resultsTable = new SurveyTable();
			resultsTable.setAspect(SurveyTable.Aspect.LINK_SURVEY_NOTES);
			resultsTable.setModel(resultsModel);
			
			JScrollPane tableScroller = new JScrollPane(resultsTable);
			tableScroller.setPreferredSize(new Dimension(800, 600));
			
			final String accept = "Add To Project";
			final String cancel = "Cancel";
			int choice = new JOptionPaneBuilder()
				.message(tableScroller)
				.defaultOption()
				.options(accept, cancel)
				.initialValue(accept)
				.showDialog(mainView.getMainPanel(), "Link Results");
		
			if (choice == JOptionPane.CANCEL_OPTION) return null;
			
			ListSelectionModel selModel = resultsTable.getModelSelectionModel();
			if (resultsTable.getSelectedRowCount() == 0) return rows;
			List<SurveyRow> result = new ArrayList<>(rows);
			Iterator<SurveyRow> iter = result.iterator();
			for (int i = 0; iter.hasNext(); iter.next(), i++) {
				if (!selModel.isSelectedIndex(i)) iter.remove();
			}
			return result;
		});
	}
	
	private void mergeIntoProject(List<SurveyRow> rows) throws Exception {
		callSelfReportingSubtask(this, 1, mainView.getMainPanel(), (Task<?> subtask) -> {
			subtask.setStatus("adding linked survey notes to project");
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
		List<String> caves = info.caves;

		String cave;
		if (caves.size() < 2) {
			cave = caves.size() > 0
				? caves.iterator().next()
				: null;
		} else {
			cave = selectCave(caves);
			if (cave == null) return null;
		}
		
		File directory = selectSearchDirectory();
		if (directory == null) return null;

		List<SurveyRow> rows = findSurveyNotes(directory.toPath(), cave, info);
		if (isCanceled()) return null;
		if (rows.isEmpty()) {
			OnEDT.onEDT(() -> {
				JOptionPane.showMessageDialog(
					mainView.getMainPanel(),
					"The search was unable to automatically link any files.");
			});
			return null;
		}
		
		Collections.sort(info.linkedFiles);
		Collections.sort(info.unlinkedFiles);
		
		System.out.println("Linked Files:");
		info.linkedFiles.forEach(file -> System.out.println("  " + file));

		System.out.println("\nUnlinked Files:");
		info.unlinkedFiles.forEach(file -> System.out.println("  " + file));
		
		rows = confirmResults(rows);
		if (rows == null) return null;
					
		addSearchDirectoryToProject(directory);
		
		mergeIntoProject(rows);
		mainView.rebuild3dModel.run();
		
		return null;
	}
}
