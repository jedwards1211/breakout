package org.breakout;

import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.andork.segment.SegmentParseException;
import org.andork.swing.OnEDT;
import org.andork.swing.async.SelfReportingTask;
import org.andork.walls.WallsMessage;
import org.andork.walls.srv.AbstractWallsVisitor;
import org.andork.walls.srv.WallsSurveyParser;
import org.andork.walls.wpj.WallsProjectBook;
import org.andork.walls.wpj.WallsProjectEntry;
import org.andork.walls.wpj.WallsProjectParser;
import org.breakout.importui.ImportError;
import org.breakout.importui.ImportResultsDialog;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.raw.SurveyRow;

class ImportWallsTask extends SelfReportingTask<Void> {
	private static final Logger logger = Logger.getLogger(ImportWallsTask.class.getSimpleName());
	private final BreakoutMainView mainView;
	final Map<Path, WallsProjectEntry> surveyFiles = new HashMap<>();
	final List<Path> plotFiles = new ArrayList<>();
	final List<Path> projFiles = new ArrayList<>();
	boolean doImport;
	final List<ImportError> errors = new ArrayList<>();
	SurveyTableModel newModel;

	ImportWallsTask(BreakoutMainView mainView, Iterable<Path> wallsFiles) {
		super(mainView.getMainPanel());
		this.mainView = mainView;
		for (Path p : wallsFiles) {
			String s = p.toString().toLowerCase();
			if (s.endsWith(".srv")) {
				surveyFiles.put(p, null);
			} else if (s.endsWith(".plt")) {
				plotFiles.add(p);
			} else if (s.endsWith(".wpj")) {
				projFiles.add(p);
			}
		}
		setIndeterminate(false);
		setCompleted(0);

		showDialogLater();
	}

	private void putSurveyFiles(WallsProjectEntry entry) {
		if (entry.isSurvey()) {
			surveyFiles.put(entry.absolutePath(), entry);
		} else if (entry instanceof WallsProjectBook) {
			((WallsProjectBook) entry).children().stream().forEach(this::putSurveyFiles);
		}
	}

	@Override
	protected Void workDuringDialog() throws Exception {
		List<SurveyRow> rows = new ArrayList<>();

		logger.info("importing walls data...");

		try {
			int progress = 0;

			for (Path file : projFiles) {
				logger.info(() -> "importing walls data from " + file + "...");
				setStatus("Importing data from " + file);
				WallsProjectParser parser = new WallsProjectParser();
				try {
					WallsProjectBook rootBook = parser.parseFile(file.toString());
					if (rootBook == null) {
						throw new RuntimeException("Failed to parse " + file);
					}
					putSurveyFiles(rootBook);
				} finally {
					for (WallsMessage message : parser.getMessages()) {
						errors.add(new ImportError(message));
					}
				}
				setCompleted(progress++);
			}

			setTotal(projFiles.size() + surveyFiles.size());

			for (Map.Entry<Path, WallsProjectEntry> entry : surveyFiles.entrySet()) {
				Path file = entry.getKey();
				WallsProjectEntry surveyEntry = entry.getValue();
				logger.info(() -> "importing walls data from " + file + "...");
				setStatus("Importing data from " + file);
				runSubtask(1, fileSubtask -> {
					fileSubtask.setTotal(2);
					WallsSurveyParser parser = new WallsSurveyParser();
					parser.setVisitor(new AbstractWallsVisitor() {
						@Override
						public void message(WallsMessage message) {
							errors.add(new ImportError(message));
						}
					});
					try {
						if (surveyEntry != null) {
							parser.parseSurveyEntry(surveyEntry);
						} else {
							parser.parseFile(file.toFile());
						}
					} catch (SegmentParseException ex) {
						errors.add(new ImportError(ex));
						throw ex;
					}
					fileSubtask.increment();
					//					fileSubtask.runSubtask(1,
					//							subtask -> rows.addAll(WallsConverter.convertFromWalls(trips, subtask)));
				});
			}
			newModel = new SurveyTableModel(rows);
			newModel.setEditable(false);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Failed to import walls data", ex);
		}
		
		if (newModel == null) {
			newModel = new SurveyTableModel();
		}

		new OnEDT() {

			@Override
			public void run() throws Throwable {
				ImportResultsDialog dialog = new ImportResultsDialog(ImportWallsTask.this.mainView.i18n, "title.walls");
				dialog.setErrors(errors);
				dialog.setSurveyTableModel(newModel);
				dialog.setSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));
				doImport = false;
				dialog.onImport(e -> {
					doImport = true;
					dialog.dispose();
				});
				dialog.setModalityType(ModalityType.APPLICATION_MODAL);
				dialog.setVisible(true);

				if (newModel.getRowCount() == 0) {
					logger.info("no shots found in imported walls data");
					return;
				}

				if (doImport) {
					mainView.setSurveyRowsFrom(newModel);
					logger.info(() -> "imported " + newModel.getRowCount() + " shots from walls data");
				} else {
					logger.info("user canceled walls import");
				}
			}
		};
		return null;
	}
}
