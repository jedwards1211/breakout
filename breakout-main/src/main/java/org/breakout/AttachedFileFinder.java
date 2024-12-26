package org.breakout;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import org.andork.q.QArrayList;
import org.andork.ref.Ref;
import org.andork.swing.FromEDT;
import org.andork.swing.OnEDT;
import org.andork.swing.async.SelfReportingTask;
import org.andork.task.Task;
import org.andork.util.StringUtils;
import org.breakout.FileFinder.MatchSet;
import org.breakout.model.ProjectModel;

public class AttachedFileFinder {
	static QArrayList<File> getSurveyScanPaths(BreakoutMainView mainView) {
		QArrayList<File> dirs = mainView.getProjectModel().get(ProjectModel.surveyScanPaths);
		if (dirs == null || dirs.isEmpty()) {
			OnEDT.onEDT(() -> {
				int option =
					JOptionPane
						.showConfirmDialog(
							mainView.getMainPanel(),
							"There are no directories configured to search for files.  Would you like to configure them now?",
							"Can't find file",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);
				if (option == JOptionPane.YES_OPTION) {
					mainView.editSurveyScanPathsAction
						.actionPerformed(new ActionEvent(mainView, ActionEvent.ACTION_PERFORMED, ""));
				}
			});
		}
		dirs = mainView.getProjectModel().get(ProjectModel.surveyScanPaths);
		if (dirs.isEmpty()) dirs = new QArrayList<>();
		return dirs;
	}
	
	private static final int SCANNED_NOTES_SEARCH_DEPTH = 10;

	private static final Logger logger = Logger.getLogger(AttachedFileFinder.class.getName());

	public static List<URI> findAttachedFiles(BreakoutMainView mainView, Set<String> attachedFiles, Task<?> task) throws IOException {
		if (SwingUtilities.isEventDispatchThread()) {
			throw new IllegalThreadStateException("must not be called on EDT, should be called on ioTaskService");
		}
		
		Map<String, FileFinder.MatchSet> matches;
		try {
			matches = FileFinder.findFiles(attachedFiles, (Function<Path, Boolean> iteratee) -> {
				QArrayList<File> dirs = getSurveyScanPaths(mainView);
				if (dirs != null && !dirs.isEmpty()) {
					if (task instanceof SelfReportingTask) {
						((SelfReportingTask<?>) task).showDialogLater();
					}

					final QArrayList<File> finalDirs = dirs;
					task
						.setStatus(
							attachedFiles.size() > 1
								? "Searching for files..."
								: "Searching for file: " + attachedFiles.iterator().next() + "...");
					task.setIndeterminate(true);

					Ref<Boolean> done = new Ref<>(false);
					for (File dir : finalDirs) {
						Files
							.walkFileTree(
								dir.toPath(),
								Collections.singleton(FileVisitOption.FOLLOW_LINKS),
								SCANNED_NOTES_SEARCH_DEPTH,
								new SimpleFileVisitor<Path>() {
									@Override
									public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
										throws IOException {
										if (iteratee.apply(path) && !task.isCanceled()) {
											return FileVisitResult.CONTINUE;
										} else {
											done.value = true;
											return FileVisitResult.TERMINATE;
										}
									}
								});
						if (done.value || task.isCanceled()) break;
					}
				}
			});
		} catch (Exception ex) {
			if (task.isCanceled()) {
				return Collections.emptyList();
			}
			logger.log(Level.SEVERE, "Failed to find files", ex);
			OnEDT.onEDT(() -> {
				JOptionPane.showMessageDialog(mainView.getMainPanel(), 
					"Failed to find files: " + ex.getLocalizedMessage(), "Error finding files", JOptionPane.ERROR_MESSAGE);
			});
			throw ex;			
		}
		if (task.isCanceled()) {
			return Collections.emptyList();
		}

		List<URI> result = new ArrayList<>();
		for (Map.Entry<String, MatchSet> entry : matches.entrySet()) {
			URI exactMatch = entry.getValue().exactMatch();
			if (exactMatch != null) {
				result.add(exactMatch);
				continue;
			}
			URI selected = FromEDT.fromEDT(() -> selectMatch(mainView, entry.getKey(), entry.getValue()));
			if (selected == null) continue;
			result.add(selected);
		}
		
		Set<String> notFound = new HashSet<>(attachedFiles);
		notFound.removeAll(matches.keySet());
		if (!notFound.isEmpty()) {
			final String title = "Failed to find file" + (notFound.size() == 1 ? "" : "s");
			OnEDT.onEDT(() -> {
				JOptionPane.showMessageDialog(mainView.getMainPanel(), 
					title + ": " + StringUtils.join(", ", notFound), title, JOptionPane.ERROR_MESSAGE);
			});
		}
		
		return result;
	}
	
	private static URI selectMatch(BreakoutMainView mainView, String inputFile, MatchSet matchset) {
		DefaultListModel<URI> model = new DefaultListModel<>();
		for (URI match : matchset.bestMatches(0.7f, 5)) {
			model.addElement(match);
		}
		JList<URI> list = new JList<>(model);
		ListCellRenderer<Object> defaultRenderer = new DefaultListCellRenderer();
		list.setCellRenderer((l, value, index, isSelected, cellHasFocus) -> {
			return defaultRenderer.getListCellRendererComponent(
				list,
				value.getScheme().equals("file") ? Paths.get(value).toString() : value.toString(),
				index, isSelected, cellHasFocus
			);
		});
		
		int option = JOptionPane.showConfirmDialog(mainView.getMainPanel(), new Object[] {
			new JLabel("No file named " + inputFile + " exists, do you want to choose one of the following?"),
			list
		}, "Select file", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		return option == JOptionPane.OK_OPTION ? list.getSelectedValue() : null;
	}
}
