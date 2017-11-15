package org.breakout;

import static org.andork.util.JavaScript.falsy;

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
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.andork.compass.CompassParseError;
import org.andork.compass.plot.BeginSectionCommand;
import org.andork.compass.plot.CompassPlotCommand;
import org.andork.compass.plot.CompassPlotParser;
import org.andork.compass.plot.DrawSurveyCommand;
import org.andork.compass.project.CompassProject;
import org.andork.compass.project.CompassProjectParser;
import org.andork.compass.survey.CompassSurveyParser;
import org.andork.compass.survey.CompassTrip;
import org.andork.swing.OnEDT;
import org.andork.swing.async.SelfReportingTask;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;
import org.breakout.compass.CompassConverter;
import org.breakout.importui.ImportError;
import org.breakout.importui.ImportResultsDialog;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.raw.MutableSurveyRow;
import org.breakout.model.raw.SurveyRow;
import org.breakout.model.raw.SurveyTrip;

class ImportCompassTask extends SelfReportingTask<Void> {
	private static final Logger logger = Logger.getLogger(ImportCompassTask.class.getSimpleName());
	private final BreakoutMainView mainView;
	final List<Path> surveyFiles = new ArrayList<>();
	final List<Path> plotFiles = new ArrayList<>();
	final List<Path> projFiles = new ArrayList<>();
	boolean doImport;

	ImportCompassTask(BreakoutMainView mainView, Iterable<Path> compassFiles) {
		super(mainView.getMainPanel());
		this.mainView = mainView;
		for (Path p : compassFiles) {
			String s = p.toString().toLowerCase();
			if (s.endsWith(".dat")) {
				surveyFiles.add(p);
			} else if (s.endsWith(".plt")) {
				plotFiles.add(p);
			} else if (s.endsWith(".mak")) {
				projFiles.add(p);
			}
		}
		setIndeterminate(false);
		setCompleted(0);

		showDialogLater();
	}

	private String toString(Object o) {
		if (o == null) {
			return null;
		}
		return o.toString();
	}

	static String feetToTripDistanceUnit(String feetMeasurement, SurveyRow row) {
		if (feetMeasurement == null)
			return feetMeasurement;
		double rawValue = Double.valueOf(feetMeasurement);
		if (!Double.isFinite(rawValue))
			return "";
		return String.valueOf(
				new UnitizedDouble<>(rawValue, Length.feet)
						.doubleValue(row.getTrip().getDistanceUnit()));
	}

	@Override
	protected Void workDuringDialog() throws Exception {
		List<SurveyRow> rows = new ArrayList<>();
		final SurveyTableModel newModel;
		final CompassSurveyParser parser = new CompassSurveyParser();
		final CompassPlotParser plotParser = new CompassPlotParser();
		final Map<String, SurveyRow> stationPositionRows = new HashMap<>();

		logger.info("importing compass data...");

		try {
			int progress = 0;

			for (Path file : projFiles) {
				logger.info(() -> "importing compass data from " + file + "...");
				setStatus("Importing data from " + file + "...");
				CompassProject proj = new CompassProjectParser().parseProject(file);
				surveyFiles.addAll(proj.getDataFiles());
				setCompleted(progress++);
			}

			Set<Path> finalSurveyFiles = new HashSet<>();
			for (Path file : surveyFiles) {
				finalSurveyFiles.add(file.toRealPath().normalize());
			}

			Set<Path> finalPlotFiles = new HashSet<>();
			for (Path file : plotFiles) {
				finalPlotFiles.add(file.toRealPath().normalize());
			}

			setTotal(projFiles.size() + finalSurveyFiles.size() + finalPlotFiles.size());

			for (Path file : finalSurveyFiles) {
				logger.info(() -> "importing compass data from " + file + "...");
				setStatus("Importing data from " + file + "...");
				runSubtask(1, fileSubtask -> {
					fileSubtask.setTotal(2);
					List<CompassTrip> trips = parser.parseCompassSurveyData(file);
					fileSubtask.increment();
					fileSubtask.runSubtask(1,
							subtask -> rows.addAll(CompassConverter.convertFromCompass(trips, subtask)));
				});
			}
			newModel = new SurveyTableModel(rows);
			newModel.setEditable(false);

			for (Path file : finalPlotFiles) {
				logger.info(() -> "importing compass data from " + file + "...");
				setStatus("Importing data from " + file + "...");
				runSubtask(1, fileSubtask -> {
					fileSubtask.setTotal(2);
					List<CompassPlotCommand> commands = plotParser.parsePlot(file);
					fileSubtask.increment();

					fileSubtask.runSubtask(1, convertSubtask -> {
						SurveyTrip trip = null;
						convertSubtask.setTotal(commands.size());
						for (CompassPlotCommand command : commands) {
							if (command instanceof BeginSectionCommand) {
								trip = new SurveyTrip();
								trip.setCave(((BeginSectionCommand) command).getSectionName());
							} else if (command instanceof DrawSurveyCommand) {
								DrawSurveyCommand c = (DrawSurveyCommand) command;
								if (falsy(c.getStationName())) {
									continue;
								}
								SurveyRow row = new MutableSurveyRow()
										.setTrip(trip)
										.setFromStation(c.getStationName())
										.setNorthing(toString(c.getLocation().getNorthing()))
										.setEasting(toString(c.getLocation().getEasting()))
										.setElevation(toString(c.getLocation().getVertical()))
										.toImmutable();
								stationPositionRows.put(c.getStationName(), row);
							}
							convertSubtask.increment();
						}
					});
				});
			}

			if (!stationPositionRows.isEmpty()) {
				newModel.updateRows(row -> {
					SurveyRow posRow = stationPositionRows.get(row.getFromStation());
					if (posRow == null) {
						return row;
					}
					return row.withMutations(r -> {
						r.setNorthing(feetToTripDistanceUnit(posRow.getNorthing(), row));
						r.setEasting(feetToTripDistanceUnit(posRow.getEasting(), row));
						r.setElevation(feetToTripDistanceUnit(posRow.getElevation(), row));
					});
				});
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Failed to import compass data", ex);
			new OnEDT() {
				@Override
				public void run() throws Throwable {
					JOptionPane.showMessageDialog(ImportCompassTask.this.mainView.getMainPanel(),
							ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage(),
							"Failed to import compass data", JOptionPane.ERROR_MESSAGE);
				}
			};

			return null;
		}

		new OnEDT() {

			@Override
			public void run() throws Throwable {
				ImportResultsDialog dialog = new ImportResultsDialog(ImportCompassTask.this.mainView.i18n,
						"title.compass");
				List<ImportError> errors = new ArrayList<>();
				for (CompassParseError error : parser.getErrors()) {
					errors.add(new ImportError(error));
				}
				for (CompassParseError error : plotParser.getErrors()) {
					errors.add(new ImportError(error));
				}
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

				if (newModel == null || newModel.getRowCount() == 0) {
					logger.info("no shots found in imported compass data");
					return;
				}

				if (doImport) {
					mainView.setSurveyRowsFrom(newModel);
					logger.info(() -> "imported " + newModel.getRowCount() + " shots from compass data");
				} else {
					logger.info("user canceled compass import");
				}
			}
		};
		return null;
	}
}
