package org.breakout.model;

import java.util.IdentityHashMap;

import org.andork.model.Property;
import org.andork.unit.Unit;
import org.andork.unit.UnitType;
import org.andork.unit.UnitizedDouble;
import org.breakout.model.ParseMessages.Severity;

/**
 * Parses SurveyRows and SurveyTrips into graph of CalcStations, CalcShots, and
 * CalcTrips. Station positions and passage walls can then be calculated on the
 * graph.
 */
public class ProjectParser {
	public final ParsedProject project;

	public ProjectParser() {
		this(new ParsedProject());
	}

	public ProjectParser(ParsedProject project) {
		super();
		this.project = project;
	}

	private final MetacaveLengthParser lengthParser = new MetacaveLengthParser();
	private final MetacaveAngleParser angleParser = new MetacaveAngleParser();
	private final MetacaveAzimuthParser azimuthParser = new MetacaveAzimuthParser();
	private final MetacaveInclinationParser inclinationParser = new MetacaveInclinationParser();

	private final IdentityHashMap<SurveyTrip, ParsedTrip> trips = new IdentityHashMap<>();

	private <T, U extends UnitType<U>> UnitizedDouble<U> parse(T object, Property<T, String> property,
			MetacaveMeasurementParser<U> parser, Unit<U> defaultUnit) {
		String text = property.get(object);

		UnitizedDouble<U> result = parser.parse(text, defaultUnit);
		if (parser.message != null && parser.severity != null) {
			Severity severity = null;
			switch (parser.severity) {
			case INFO:
				severity = Severity.INFO;
			case WARNING:
				severity = Severity.WARNING;
			case ERROR:
				severity = Severity.ERROR;
			}
			project.messages.add(object, property, severity, parser.message);
		}
		return result;
	}

	private <T, U extends UnitType<U>> UnitizedDouble<U> parse(T object, Property<T, String> property,
			MetacaveMeasurementParser<U> parser, Unit<U> defaultUnit,
			Severity missingSeverity) {
		UnitizedDouble<U> result = parse(object, property, parser, defaultUnit);
		if (result == null && missingSeverity != null) {
			project.messages.add(object, property, missingSeverity, "missing " + property.name());
		}
		return result;
	}

	private <T, U extends UnitType<U>> UnitizedDouble<U> parse(T object, Property<T, String> property,
			MetacaveMeasurementParser<U> parser, Unit<U> defaultUnit,
			Severity missingSeverity, double defaultValue) {
		UnitizedDouble<U> result = parse(object, property, parser, defaultUnit, missingSeverity);
		return result != null ? result : new UnitizedDouble<>(defaultValue, defaultUnit);
	}

	public ParsedTrip parse(SurveyTrip raw) {
		ParsedTrip parsed = trips.get(raw);
		if (parsed != null) {
			return parsed;
		}
		parsed = new ParsedTrip();
		trips.put(raw, parsed);

		// TODO Date!
		parsed.distanceCorrection = parse(raw, SurveyTrip.Properties.distanceCorrection,
				lengthParser, raw.getDistanceUnit(), null, 0);
		parsed.declination = parse(raw, SurveyTrip.Properties.declination,
				angleParser, raw.getAngleUnit(), null, 0);
		parsed.frontAzimuthCorrection = parse(raw, SurveyTrip.Properties.frontAzimuthCorrection,
				angleParser, raw.getFrontAzimuthUnit(), null, 0);
		parsed.backAzimuthCorrection = parse(raw, SurveyTrip.Properties.backAzimuthCorrection,
				angleParser, raw.getBackAzimuthUnit(), null, 0);
		parsed.frontInclinationCorrection = parse(raw, SurveyTrip.Properties.frontInclinationCorrection,
				angleParser, raw.getFrontInclinationUnit(), null, 0);
		parsed.backInclinationCorrection = parse(raw, SurveyTrip.Properties.backInclinationCorrection,
				angleParser, raw.getBackInclinationUnit(), null, 0);
		parsed.areBackAzimuthsCorrected = raw.areBackAzimuthsCorrected();
		parsed.areBackInclinationsCorrected = raw.areBackInclinationsCorrected();

		return parsed;
	}

	public ParsedRow parse(SurveyRow raw) {
		SurveyTrip trip = raw.getTrip();
		ParsedRow parsed = new ParsedRow();
		parsed.trip = parse(trip);
		parsed.fromCave = raw.getFromCave();
		parsed.fromStation = raw.getFromStation();
		parsed.toCave = raw.getToCave();
		parsed.toStation = raw.getToStation();

		if (parsed.fromStation != null && parsed.toStation != null &&
				new StationKey(parsed.fromCave, parsed.fromStation).equals(
						new StationKey(parsed.toCave, parsed.toStation))) {
			project.messages.error(raw, SurveyRow.Properties.toStation, "to station is the same as from station");
		}

		parsed.distance = parse(raw, SurveyRow.Properties.distance,
				lengthParser, raw.getTrip().getDistanceUnit());
		parsed.frontAzimuth = parse(raw, SurveyRow.Properties.frontAzimuth,
				azimuthParser, trip.getFrontAzimuthUnit());
		parsed.backAzimuth = parse(raw, SurveyRow.Properties.backAzimuth,
				azimuthParser, trip.getBackAzimuthUnit());
		parsed.frontInclination = parse(raw, SurveyRow.Properties.frontInclination,
				inclinationParser, trip.getFrontInclinationUnit());
		parsed.backInclination = parse(raw, SurveyRow.Properties.backInclination,
				inclinationParser, trip.getBackInclinationUnit());
		parsed.crossSectionType = CrossSectionType.LRUD;
		parsed.left = parse(raw, SurveyRow.Properties.left,
				lengthParser, trip.getDistanceUnit(), Severity.WARNING);
		parsed.right = parse(raw, SurveyRow.Properties.right,
				lengthParser, trip.getDistanceUnit(), Severity.WARNING);
		parsed.up = parse(raw, SurveyRow.Properties.up,
				lengthParser, trip.getDistanceUnit(), Severity.WARNING);
		parsed.down = parse(raw, SurveyRow.Properties.down,
				lengthParser, trip.getDistanceUnit(), Severity.WARNING);
		parsed.northing = parse(raw, SurveyRow.Properties.northing,
				lengthParser, trip.getDistanceUnit());
		parsed.easting = parse(raw, SurveyRow.Properties.easting,
				lengthParser, trip.getDistanceUnit());
		parsed.elevation = parse(raw, SurveyRow.Properties.elevation,
				lengthParser, trip.getDistanceUnit());
		project.rows.add(parsed);
		ShotKey key = parsed.key();
		if (key != null) {
			project.shots.put(key, parsed);
		}
		return parsed;
	}
}
