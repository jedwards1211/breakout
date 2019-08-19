package org.breakout;

import static org.andork.util.JavaScript.falsy;

import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.andork.compass.CompassParseError;
import org.andork.compass.LrudAssociation;
import org.andork.compass.plot.CompassPlotCommand;
import org.andork.compass.plot.CompassPlotParser;
import org.andork.compass.plot.DatumCommand;
import org.andork.compass.plot.DrawOperation;
import org.andork.compass.plot.DrawSurveyCommand;
import org.andork.compass.plot.UtmZoneCommand;
import org.andork.compass.project.CommentDirective;
import org.andork.compass.project.CompassProjectParser;
import org.andork.compass.project.CompassProjectVisitor;
import org.andork.compass.project.DatumDirective;
import org.andork.compass.project.FileDirective;
import org.andork.compass.project.FlagsDirective;
import org.andork.compass.project.LinkStation;
import org.andork.compass.project.LocationDirective;
import org.andork.compass.project.UTMConvergenceDirective;
import org.andork.compass.project.UTMZoneDirective;
import org.andork.compass.survey.CompassShot;
import org.andork.compass.survey.CompassSurveyParser;
import org.andork.compass.survey.CompassTrip;
import org.andork.segment.Segment;
import org.andork.swing.OnEDT;
import org.andork.swing.async.SelfReportingTask;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;
import org.andork.util.StringUtils;
import org.breakout.compass.CompassConverter;
import org.breakout.importui.ImportError;
import org.breakout.importui.ImportError.Severity;
import org.breakout.importui.ImportResultsDialog;
import org.breakout.model.ShotKey;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.raw.MutableSurveyRow;
import org.breakout.model.raw.MutableSurveyTrip;
import org.breakout.model.raw.SurveyRow;
import org.breakout.model.raw.SurveyTrip;

class ImportCompassTask extends SelfReportingTask<Void> {
	private static final Logger logger = Logger.getLogger(ImportCompassTask.class.getSimpleName());
	private final BreakoutMainView mainView;
	final List<Path> surveyFiles = new ArrayList<>();
	final List<Path> plotFiles = new ArrayList<>();
	final List<Path> projFiles = new ArrayList<>();
	final List<ImportError> errors = new ArrayList<>();
	boolean doImport;

	ImportCompassTask(BreakoutMainView mainView, Iterable<Path> compassFiles) {
		super(mainView.getMainPanel());
		this.mainView = mainView;
		for (Path p : compassFiles) {
			String s = p.toString().toLowerCase();
			if (s.endsWith(".dat")) {
				surveyFiles.add(p);
			}
			else if (s.endsWith(".plt")) {
				plotFiles.add(p);
			}
			else if (s.endsWith(".mak")) {
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
		return String.valueOf(new UnitizedDouble<>(rawValue, Length.feet).doubleValue(row.getTrip().getDistanceUnit()));
	}

	class SurveyLocation {
		final String datum;
		final LocationDirective location;

		public SurveyLocation(String datum, LocationDirective location) {
			super();
			this.datum = datum;
			this.location = location;
		}

		public SurveyLocation setDatum(String datum) {
			if (Objects.equals(datum, this.datum))
				return this;
			return new SurveyLocation(datum, location);
		}

		public SurveyLocation setLocation(LocationDirective location) {
			if (Objects.equals(location, this.location))
				return this;
			return new SurveyLocation(datum, location);
		}
	}

	private static Map<String, String> datumMap = new HashMap<>();
	private static Map<String, String> ellipsoidMap = new HashMap<>();

	static {
		datumMap.put("North American 1983", "NAD83");
		datumMap.put("North American 1927", "NAD27");
		datumMap.put("WGS 1984", "WGS84");
		ellipsoidMap.put("North American 1983", "NAD83");
		ellipsoidMap.put("North American 1927", "NAD27");
		ellipsoidMap.put("WGS 1984", "WGS84");
	}

	@Override
	protected Void workDuringDialog() throws Exception {
		List<SurveyRow> rows = new ArrayList<>();
		final SurveyTableModel newModel;
		final CompassSurveyParser parser = new CompassSurveyParser();
		final CompassPlotParser plotParser = new CompassPlotParser();
		final Map<Path, SurveyLocation> surveyLocations = new HashMap<>();
		final Map<Path, SurveyTrip> surveyLocationTrips = new HashMap<>();
		final Map<String, Path> stationToSurvey = new HashMap<>();
		final Set<String> importedLrudStations = new HashSet<>();
		final Set<ShotKey> importedShots = new HashSet<>();

		logger.info("importing compass data...");

		try {
			int progress = 0;

			for (Path file : projFiles) {
				logger.info(() -> "importing compass data from " + file + "...");
				setStatus("Importing data from " + file + "...");
				new CompassProjectParser(new CompassProjectVisitor() {
					SurveyLocation currentLocation = new SurveyLocation(null, null);

					@Override
					public void utmZone(UTMZoneDirective utmZone) {
					}

					@Override
					public void utmConvergence(UTMConvergenceDirective utmConvergence) {
					}

					@Override
					public void location(LocationDirective location) {
						currentLocation = currentLocation.setLocation(location);
					}

					@Override
					public void flags(FlagsDirective flags) {
						// TODO handle this
					}

					@Override
					public void file(Segment name, FileDirective surveyFile) {
						Path surveyPath = file.getParent().resolve(Paths.get(surveyFile.file));
						if (Files.notExists(surveyPath)) {
							errors
								.add(
									new ImportError(
										Severity.ERROR,
										"Survey file doesn't exist: " + surveyFile.file,
										name));
							return;
						}
						surveyFiles.add(surveyPath);
						surveyLocations.put(surveyPath, currentLocation);
						SurveyTrip locationTrip =
							new MutableSurveyTrip()
								.setDatum(currentLocation.datum != null ? datumMap.get(currentLocation.datum) : null)
								.setEllipsoid(
									currentLocation.datum != null ? ellipsoidMap.get(currentLocation.datum) : null)
								.setUtmZone(
									currentLocation.location != null
										? String.valueOf(currentLocation.location.utmZone)
										: null)
								.setName(null)
								.setDistanceUnit(Length.feet)
								.setAngleUnit(Angle.degrees)
								.setOverrideFrontAzimuthUnit(Angle.degrees)
								.setOverrideBackAzimuthUnit(Angle.degrees)
								.setOverrideFrontInclinationUnit(Angle.degrees)
								.setOverrideBackInclinationUnit(Angle.degrees)
								.setBackAzimuthsCorrected(true)
								.setBackInclinationsCorrected(true)
								.toImmutable();
						surveyLocationTrips.put(surveyPath, locationTrip);
						if (surveyFile.linkStations != null) {
							for (LinkStation station : surveyFile.linkStations) {
								if (station.location == null)
									continue;
								MutableSurveyRow stationPositionRow = new MutableSurveyRow();
								stationPositionRow.setTrip(locationTrip);
								stationPositionRow.setFromStation(station.name);
								stationPositionRow.setEasting(station.location.easting.toString());
								stationPositionRow.setNorthing(station.location.northing.toString());
								stationPositionRow.setElevation(station.location.elevation.toString());
							}
						}
					}

					@Override
					public void datum(DatumDirective datum) {
						currentLocation = currentLocation.setDatum(datum.datum);
						if (datum.datum != null
							&& (!datumMap.containsKey(datum.datum) || !ellipsoidMap.containsKey(datum.datum))) {
							errors.add(new ImportError(Severity.ERROR, "datum not supported: " + datum.datum, null));
						}
					}

					@Override
					public void comment(CommentDirective comment) {
					}
				}).parse(file);
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
					for (CompassTrip trip : trips) {
						boolean lrudAtFrom = trip.getHeader().getLrudAssociation() == LrudAssociation.FROM;
						for (CompassShot shot : trip.getShots()) {
							importedShots
								.add(new ShotKey(null, shot.getFromStationName(), null, shot.getToStationName()));
							importedLrudStations.add(lrudAtFrom ? shot.getFromStationName() : shot.getToStationName());
							stationToSurvey.put(shot.getFromStationName(), file);
							stationToSurvey.put(shot.getToStationName(), file);
						}
					}
					fileSubtask.increment();
					fileSubtask
						.runSubtask(1, subtask -> rows.addAll(CompassConverter.convertFromCompass(trips, subtask)));
				});
			}

			for (Path file : finalPlotFiles) {
				logger.info(() -> "importing compass data from " + file + "...");
				setStatus("Importing data from " + file + "...");
				runSubtask(1, fileSubtask -> {
					fileSubtask.setTotal(2);
					List<CompassPlotCommand> commands = plotParser.parsePlot(file);
					fileSubtask.increment();

					fileSubtask.runSubtask(1, convertSubtask -> {
						convertSubtask.setTotal(commands.size());

						String datum = null;
						Integer utmZone = null;
						SurveyTrip locationTrip = null;

						DrawSurveyCommand prevDrawCommand = null;

						for (CompassPlotCommand command : commands) {
							if (command instanceof DatumCommand) {
								datum = ((DatumCommand) command).getDatum();
								locationTrip = null;
							}
							else if (command instanceof UtmZoneCommand) {
								utmZone = Integer.valueOf(((UtmZoneCommand) command).getUtmZone());
								locationTrip = null;
							}
							else if (command instanceof DrawSurveyCommand) {
								DrawSurveyCommand c = (DrawSurveyCommand) command;
								if (falsy(c.getStationName())) {
									continue;
								}
								SurveyTrip trip = null;
								SurveyLocation location = null;
								Path surveyFile = stationToSurvey.get(c.getStationName());
								if (surveyFile != null) {
									trip = surveyLocationTrips.get(surveyFile);
									location = surveyLocations.get(surveyFile);
								}
								if (trip == null) {
									if (locationTrip == null) {
										locationTrip =
											new MutableSurveyTrip()
												.setDatum(datum != null ? datumMap.get(datum) : null)
												.setEllipsoid(datum != null ? ellipsoidMap.get(datum) : null)
												.setUtmZone(utmZone != null ? String.valueOf(utmZone) : null)
												.setName(null)
												.setDistanceUnit(Length.feet)
												.setAngleUnit(Angle.degrees)
												.setOverrideFrontAzimuthUnit(Angle.degrees)
												.setOverrideBackAzimuthUnit(Angle.degrees)
												.setOverrideFrontInclinationUnit(Angle.degrees)
												.setOverrideBackInclinationUnit(Angle.degrees)
												.setBackAzimuthsCorrected(true)
												.setBackInclinationsCorrected(true)
												.toImmutable();
									}
									trip = locationTrip;
								}

								if (prevDrawCommand != null) {
									if (c.getOperation() == DrawOperation.MOVE_TO) {
										if (importedLrudStations.add(prevDrawCommand.getStationName())) {
											rows
												.add(
													new MutableSurveyRow()
														.setTrip(trip)
														.setFromStation(prevDrawCommand.getStationName())
														.setLeft(StringUtils.valueOfOrNull(prevDrawCommand.getLeft()))
														.setRight(StringUtils.valueOfOrNull(prevDrawCommand.getRight()))
														.setUp(StringUtils.valueOfOrNull(prevDrawCommand.getUp()))
														.setDown(StringUtils.valueOfOrNull(prevDrawCommand.getDown()))
														.toImmutable());
										}
									}
									else if (importedShots
										.add(
											new ShotKey(
												null,
												prevDrawCommand.getStationName(),
												null,
												c.getStationName()))) {

										double dn =
											c
												.getLocation()
												.getNorthing()
												.subtract(prevDrawCommand.getLocation().getNorthing())
												.doubleValue();
										double de =
											c
												.getLocation()
												.getEasting()
												.subtract(prevDrawCommand.getLocation().getEasting())
												.doubleValue();
										double dv =
											c
												.getLocation()
												.getVertical()
												.subtract(prevDrawCommand.getLocation().getVertical())
												.doubleValue();

										double distance = Math.sqrt(dn * dn + de * de + dv * dv);
										double dne = Math.sqrt(dn * dn + de * de);
										double azimuth = Math.toDegrees(Math.atan2(de, dn));
										if (azimuth < 0)
											azimuth += 360;
										double inclination = Math.toDegrees(Math.atan2(dv, dne));

										importedLrudStations.add(prevDrawCommand.getStationName());
										rows
											.add(
												new MutableSurveyRow()
													.setTrip(trip)
													.setFromStation(prevDrawCommand.getStationName())
													.setToStation(c.getStationName())
													.setDistance(String.valueOf(distance))
													.setFrontAzimuth(String.valueOf(azimuth))
													.setFrontInclination(String.valueOf(inclination))
													.setLeft(StringUtils.valueOfOrNull(prevDrawCommand.getLeft()))
													.setRight(StringUtils.valueOfOrNull(prevDrawCommand.getRight()))
													.setUp(StringUtils.valueOfOrNull(prevDrawCommand.getUp()))
													.setDown(StringUtils.valueOfOrNull(prevDrawCommand.getDown()))
													.toImmutable());
									}
								}

								prevDrawCommand = c;

								BigDecimal northing = c.getLocation().getNorthing();
								BigDecimal easting = c.getLocation().getEasting();
								BigDecimal elevation = c.getLocation().getVertical();
								if (location != null) {
									Unit<Length> distUnit = trip.getDistanceUnit();
									northing =
										northing.add(new BigDecimal(location.location.northing.doubleValue(distUnit)));
									easting =
										easting.add(new BigDecimal(location.location.easting.doubleValue(distUnit)));
									elevation =
										elevation
											.add(new BigDecimal(location.location.elevation.doubleValue(distUnit)));
								}
								SurveyRow row =
									new MutableSurveyRow()
										.setTrip(trip)
										.setFromStation(c.getStationName())
										.setNorthing(toString(northing))
										.setEasting(toString(easting))
										.setElevation(toString(elevation))
										.toImmutable();
								rows.add(row);

							}
							convertSubtask.increment();
						}
					});
				});
			}
			newModel = new SurveyTableModel(rows);
			newModel.setEditable(false);

			OnEDT.onEDT(() -> {
				ImportResultsDialog dialog =
					new ImportResultsDialog(ImportCompassTask.this.mainView.i18n, "title.compass");
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
					mainView.addSurveyRowsFrom(newModel);
					logger.info(() -> "imported " + newModel.getRowCount() + " shots from compass data");
				}
				else {
					logger.info("user canceled compass import");
				}
			});
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Failed to import compass data", ex);
			OnEDT.onEDT(() -> {
				JOptionPane
					.showMessageDialog(
						ImportCompassTask.this.mainView.getMainPanel(),
						ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage(),
						"Failed to import compass data",
						JOptionPane.ERROR_MESSAGE);
			});
		}

		return null;
	}
}
