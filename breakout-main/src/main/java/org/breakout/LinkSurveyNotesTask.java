package org.breakout;

import static org.andork.swing.async.SelfReportingTask.callSelfReportingSubtask;

import java.awt.Dimension;
import java.io.File;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.andork.swing.FromEDT;
import org.andork.swing.JOptionPaneBuilder;
import org.andork.swing.OnEDT;
import org.andork.task.Task;
import org.breakout.model.RootModel;
import org.breakout.model.ShotKey;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.raw.SurveyRow;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

public class LinkSurveyNotesTask extends Task<Void> {
	private BreakoutMainView mainView;

	public LinkSurveyNotesTask(BreakoutMainView mainView) {
		this.mainView = mainView;
	}
	
	private List<String> getCaves() {
		setStatus("Building cave list");
		setIndeterminate(false);
		SurveyTableModel model = mainView.getSurveyTable().getModel();
		setCompleted(0);
		setTotal(model.getRowCount());		
		Set<String> caves = new HashSet<>();
		for (int i = 0; i < model.getRowCount(); i++) {
			SurveyRow row = model.getRow(i);
			String fromCave = row.getFromCave();
			if (fromCave != null) caves.add(fromCave);
			String toCave = row.getToCave();
			if (toCave != null) caves.add(toCave);
			increment();
		}
		List<String> result = new ArrayList<>(caves);
		Collections.sort(result);
		return result;
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
	
	private List<SurveyRow> findSurveyNotes(Path directory, String caveName) throws Exception {
		return callSelfReportingSubtask(this, 1, mainView.getMainPanel(), subtask -> {
			setStatus("Scanning files");
			setIndeterminate(true);

			try (Stream<Path> files = Files.find(
				directory, 10,
				(Path path, BasicFileAttributes attrs) -> {
					String fileName = path.getFileName().toString();
					if (fileName.startsWith(".")) return false;
					return extensionMatcher.reset(fileName).find();
				},
				FileVisitOption.FOLLOW_LINKS)) {

				List<SurveyRow> result = new ArrayList<>();
				
				files.forEach(file -> {
					setStatus("Scanning " + file);
					String fileName = file.getFileName().toString();
					StationSet stations = new StationSet(fileName.replace("\\.[^.]*$", ""));
					
					stations.forEachShot(caveName, key -> {
						SurveyRow existing = mainView.sourceRows.get(key);
						if (existing == null || existing.getSurveyNotes() != null) return;
						result.add(existing.setOverrideSurveyNotes(fileName));
					});
				});

				return result;
			}
		});
	}
	
	private List<SurveyRow> confirmResults(List<SurveyRow> rows) {
		setStatus("");
		setIndeterminate(true);

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
		setStatus("Adding linked survey notes to project");
		setIndeterminate(false);
		setCompleted(0);
		setTotal(1);
		callSelfReportingSubtask(this, 1, mainView.getMainPanel(), subtask -> {
			SurveyTableModel model = mainView.getSurveyTable().getModel();
			
			subtask.forEach(rows, (SurveyRow row) -> {
				Integer index = mainView.shotKeyToModelIndex.get(new ShotKey(row));
				if (index == null) return;
				model.setRow(index,
					model.getRow(index)
						.setOverrideSurveyNotes(row.getSurveyNotes()));
			});
		});
	}
	
	private final Matcher extensionMatcher =
		Pattern.compile("\\.(bmp|jpe?g|png|pdf|e?ps|xps|gif|tiff?|[we]mf)$", Pattern.CASE_INSENSITIVE)
		.matcher("");

	@Override
	protected Void work() throws Exception {
		List<String> caves = getCaves();
		
		setStatus(null);
		setIndeterminate(true);

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

		List<SurveyRow> rows = findSurveyNotes(directory.toPath(), cave);
		if (rows.isEmpty()) {
			OnEDT.onEDT(() -> {
				JOptionPane.showMessageDialog(
					mainView.getMainPanel(),
					"The search was unable to automatically link any files.");
			});
			return null;
		}
		
		rows = confirmResults(rows);
		if (rows == null) return null;
		
		mergeIntoProject(rows);
		mainView.rebuild3dModel.run();
		
		return null;
	}
}
