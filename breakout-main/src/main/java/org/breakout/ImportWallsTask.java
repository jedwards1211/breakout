package org.breakout;

import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.andork.collect.ArrayLists;
import org.andork.segment.Segment;
import org.andork.segment.SegmentParseException;
import org.andork.swing.OnEDT;
import org.andork.swing.async.SelfReportingTask;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.util.StringUtils;
import org.andork.walls.WallsMessage;
import org.andork.walls.lst.StationPosition;
import org.andork.walls.lst.WallsStationReport;
import org.andork.walls.lst.WallsStationReportParser;
import org.andork.walls.srv.AbstractWallsVisitor;
import org.andork.walls.srv.FixedStation;
import org.andork.walls.srv.Vector;
import org.andork.walls.srv.VectorType;
import org.andork.walls.srv.WallsSurveyParser;
import org.andork.walls.srv.WallsUnits;
import org.andork.walls.srv.WallsVisitor;
import org.andork.walls.wpj.WallsProjectBook;
import org.andork.walls.wpj.WallsProjectEntry;
import org.andork.walls.wpj.WallsProjectParser;
import org.breakout.importui.ImportError;
import org.breakout.importui.ImportError.Severity;
import org.breakout.importui.ImportResultsDialog;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.raw.MutableSurveyRow;
import org.breakout.model.raw.MutableSurveyTrip;
import org.breakout.model.raw.SurveyTrip;

class ImportWallsTask extends SelfReportingTask<Void> {
	private static final Logger logger = Logger.getLogger(ImportWallsTask.class.getSimpleName());
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private final BreakoutMainView mainView;
	private final Map<Path, WallsProjectEntry> surveyFiles = new HashMap<>();
	private final List<Path> stationReportFiles = new ArrayList<>();
	private final List<Path> projFiles = new ArrayList<>();
	private boolean doImport;
	private final List<ImportError> errors = new ArrayList<>();
	private SurveyTableModel newModel;

	private WallsUnits currentUnits;
	private Date currentDate;
	private final List<MutableSurveyRow> rows = new ArrayList<>();
	private boolean awaitingTripNameComment = true;
	private Path currentFile;
	private String currentTripName;
	private MutableSurveyTrip currentTrip;
	private final List<MutableSurveyRow> rowsInCurrentTrip = new ArrayList<>();
	private final List<FixedStation> fixedStations = new ArrayList<>();

	private void endCurrentTrip() {
		if (!rowsInCurrentTrip.isEmpty()) {
			ensureCurrentTrip();
		}
		if (currentTrip != null) {
			SurveyTrip immutableTrip = currentTrip.toImmutable();
			for (MutableSurveyRow row : rowsInCurrentTrip) {
				row.setTrip(immutableTrip);
			}
			rowsInCurrentTrip.clear();
			currentTrip = null;
		}
	}

	private MutableSurveyTrip ensureCurrentTrip() {
		if (currentTrip == null) {
			if (currentUnits == null) {
				throw new IllegalStateException("missing currentUnits");
			}
			currentTrip =
				new MutableSurveyTrip()
					.setName(currentTripName)
					.setDistanceUnit(currentUnits.getDUnit())
					.setAngleUnit(currentUnits.getAUnit())
					.setOverrideFrontAzimuthUnit(currentUnits.getAUnit())
					.setOverrideBackAzimuthUnit(currentUnits.getAbUnit())
					.setOverrideFrontInclinationUnit(currentUnits.getVUnit())
					.setOverrideBackInclinationUnit(currentUnits.getVbUnit())
					.setBackAzimuthsCorrected(currentUnits.isTypeabCorrected())
					.setBackInclinationsCorrected(currentUnits.isTypevbCorrected())
					.setDeclination(Objects.toString(currentUnits.getDecl(), null))
					.setDistanceCorrection(Objects.toString(currentUnits.getIncd(), null))
					.setFrontAzimuthCorrection(Objects.toString(currentUnits.getInca(), null))
					.setBackAzimuthCorrection(Objects.toString(currentUnits.getIncab(), null))
					.setFrontInclinationCorrection(Objects.toString(currentUnits.getIncv(), null))
					.setBackInclinationCorrection(Objects.toString(currentUnits.getIncvb(), null))
					.setDate(currentDate != null ? dateFormat.format(currentDate) : null);
		}
		return currentTrip;
	}

	private static final List<Function<WallsUnits, ?>> tripDependentFields =
		Arrays
			.asList(
				WallsUnits::getDUnit,
				WallsUnits::getDecl,
				WallsUnits::getGrid,
				WallsUnits::getIncd,
				WallsUnits::getInca,
				WallsUnits::getIncab,
				WallsUnits::getIncv,
				WallsUnits::getIncvb,
				WallsUnits::getAUnit,
				WallsUnits::getAbUnit,
				WallsUnits::getVUnit,
				WallsUnits::getVbUnit,
				WallsUnits::isTypeabCorrected,
				WallsUnits::isTypevbCorrected);

	private void setCurrentUnits(WallsUnits units) {
		if (currentUnits != units) {
			if (units == null || currentUnits == null) {
				endCurrentTrip();
			}
			else {
				for (Function<WallsUnits, ?> field : tripDependentFields) {
					if (!Objects.equals(field.apply(units), field.apply(currentUnits))) {
						endCurrentTrip();
						break;
					}
				}
			}
			currentUnits = units;
		}
	}

	private void setCurrentDate(Date date) {
		if (!Objects.equals(currentDate, date)) {
			endCurrentTrip();
			currentDate = date;
		}
	}

	private void setCurrentTripName(String name) {
		if (!Objects.equals(name, currentTripName)) {
			if (currentTrip != null) {
				currentTrip.setName(name);
			}
			currentTripName = name;
		}
	}

	private final WallsVisitor wallsVisitor = new AbstractWallsVisitor() {
		@Override
		public void parsedComment(String parsedComment) {
			if (awaitingTripNameComment) {
				awaitingTripNameComment = false;
				setCurrentTripName(parsedComment);
			}
		}

		@Override
		public void message(WallsMessage message) {
			errors.add(new ImportError(message));
		}

		@Override
		public void parsedFixStation(FixedStation station) {
			awaitingTripNameComment = false;
			setCurrentUnits(station.units);
			fixedStations.add(station);
		}

		@Override
		public void parsedVector(Vector vector) {
			awaitingTripNameComment = false;
			setCurrentUnits(vector.units);
			try {
				if (vector.units.getVectorType() == VectorType.RECTANGULAR) {
					vector.deriveCtFromRect();
				}
				else {
					vector.applyHeightCorrections(this);
				}
			}
			catch (SegmentParseException e) {
				errors.add(new ImportError(e));
				throw new RuntimeException(e);
			}

			String fromStationName = vector.units.processStationName(vector.from);
			MutableSurveyRow row =
				new MutableSurveyRow()
					.setFromStation(fromStationName)
					.setToStation(vector.units.processStationName(vector.to))
					.setDistance(Objects.toString(vector.distance, null))
					.setFrontAzimuth(Objects.toString(vector.frontsightAzimuth, null))
					.setBackAzimuth(Objects.toString(vector.backsightAzimuth, null))
					.setFrontInclination(Objects.toString(vector.frontsightInclination, null))
					.setBackInclination(Objects.toString(vector.backsightInclination, null))
					.setLeft(vector.left == null ? null : vector.left.add(vector.units.getIncs()).toString())
					.setRight(vector.right == null ? null : vector.right.add(vector.units.getIncs()).toString())
					.setUp(vector.up == null ? null : vector.up.add(vector.units.getIncs()).toString())
					.setDown(vector.down == null ? null : vector.down.add(vector.units.getIncs()).toString())
					.setComment(vector.comment);
			rowsInCurrentTrip.add(row);
			rows.add(row);
		}

		@Override
		public void parsedDate(Date date) {
			awaitingTripNameComment = false;
			setCurrentDate(date);
		}

		@Override
		public void parsedNote(String station, String parsedNote) {
			MutableSurveyRow row = new MutableSurveyRow().setFromStation(station).setComment(parsedNote);
			rowsInCurrentTrip.add(row);
			rows.add(row);
			awaitingTripNameComment = false;
		}

		@Override
		public void parsedFlag(List<String> stations, String flag) {
			awaitingTripNameComment = false;
		}

		@Override
		public void parsedUnits() {
			awaitingTripNameComment = false;
		}

		@Override
		public void parsedSegment(String segment) {
			awaitingTripNameComment = false;
		}
	};

	public ImportWallsTask(BreakoutMainView mainView, Iterable<Path> wallsFiles) {
		super(mainView.getMainPanel());
		this.mainView = mainView;
		for (Path p : wallsFiles) {
			String s = p.toString().toLowerCase();
			if (s.endsWith(".srv")) {
				surveyFiles.put(p, null);
			}
			else if (s.endsWith(".lst")) {
				stationReportFiles.add(p);
			}
			else if (s.endsWith(".wpj")) {
				projFiles.add(p);
			}
		}
		setIndeterminate(false);
		setCompleted(0);

		showDialogLater();
	}

	private void putSurveyFiles(WallsProjectEntry entry) {
		if (entry.isDetatched()) {
			wallsVisitor
				.message(
					new WallsMessage(
						"warning",
						"Entry is detached and won't be imported: " + StringUtils.join(" -> ", entry.titlePath()),
						entry.statusSegment()));
			return;
		}
		if (entry.isSurvey()) {
			surveyFiles.put(entry.absolutePath(), entry);
		}
		else if (entry instanceof WallsProjectBook) {
			((WallsProjectBook) entry).children().stream().forEach(this::putSurveyFiles);
		}
	}

	@Override
	protected Void workDuringDialog() throws Exception {
		logger.info("importing walls data...");

		try {
			parseProjectFiles();
			setTotal(projFiles.size() + surveyFiles.size() + stationReportFiles.size());
			parseSurveyFiles();
			parseStationReportFiles();

			// applyFixedStationPositions();

		}
		catch (Exception ex) {
			if (ex instanceof SegmentParseException) {
				errors.add(new ImportError((SegmentParseException) ex));
			}
			else {
				errors
					.add(
						new ImportError(
							Severity.ERROR,
							"Unexpected error: " + ex.getLocalizedMessage(),
							new Segment("", currentFile, 0, 0)));
			}
			logger.log(Level.SEVERE, "Failed to import walls data", ex);
		}

		try {
			newModel = new SurveyTableModel(ArrayLists.map(rows, row -> row.toImmutable()));
		}
		catch (Exception ex) {
			newModel = new SurveyTableModel();
			errors
				.add(
					new ImportError(
						Severity.ERROR,
						"Unexpected error: " + ex.getLocalizedMessage(),
						new Segment("", currentFile, 0, 0)));
			logger.log(Level.SEVERE, "Failed to import walls data", ex);
		}
		OnEDT.onEDT(() -> {
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
				mainView.addSurveyRowsFrom(newModel);
				logger.info(() -> "imported " + newModel.getRowCount() + " shots from walls data");
			}
			else {
				logger.info("user canceled walls import");
			}
		});

		return null;
	}

	private void parseProjectFiles() throws IOException {
		for (Path file : projFiles) {
			currentFile = file;
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
			increment();
		}
	}

	private void parseSurveyFiles() throws SegmentParseException, IOException {
		for (Map.Entry<Path, WallsProjectEntry> entry : surveyFiles.entrySet()) {
			Path file = entry.getKey();
			currentFile = file;
			WallsProjectEntry surveyEntry = entry.getValue();

			logger.info(() -> "importing walls data from " + file + "...");
			setStatus("Importing data from " + file);

			awaitingTripNameComment = true;
			try {
				WallsSurveyParser parser = new WallsSurveyParser();
				parser.setVisitor(wallsVisitor);
				if (surveyEntry != null) {
					setCurrentTripName(surveyEntry.absolutePath().getFileName().toString());
					parser.parseSurveyEntry(surveyEntry);
				}
				else {
					setCurrentTripName(file.getFileName().toString());
					parser.parseFile(file.toFile());
				}
			} finally {
				endCurrentTrip();
			}
			increment();
		}
	}

	private void parseStationReportFiles() throws IOException {
		WallsStationReportParser parser = new WallsStationReportParser();

		for (Path file : stationReportFiles) {
			logger.info(() -> "importing walls data from " + file + "...");
			setStatus("Importing data from " + file);

			parser.parseFile(file);

			increment();
		}

		WallsStationReport report = parser.getReport();
		SurveyTrip trip =
			new MutableSurveyTrip()
				.setDatum(report.datum)
				.setUtmZone(String.valueOf(report.utmZone))
				.setName(null)
				.setDistanceUnit(Length.meters)
				.setAngleUnit(Angle.degrees)
				.setOverrideFrontAzimuthUnit(Angle.degrees)
				.setOverrideBackAzimuthUnit(Angle.degrees)
				.setOverrideFrontInclinationUnit(Angle.degrees)
				.setOverrideBackInclinationUnit(Angle.degrees)
				.setBackAzimuthsCorrected(true)
				.setBackInclinationsCorrected(true)
				.toImmutable();

		for (StationPosition station : report.stationPositions) {
			MutableSurveyRow row = new MutableSurveyRow();
			row.setTrip(trip);
			row.setFromStation(station.getNameWithPrefix());
			if (Double.isFinite(station.north))
				row.setNorthing((report.utmSouth ? 1000000 - station.north : station.north) + " m");
			if (Double.isFinite(station.east))
				row.setEasting(station.east + " m");
			if (Double.isFinite(station.up))
				row.setElevation(station.up + " m");
			rows.add(row);
		}
	}
}
