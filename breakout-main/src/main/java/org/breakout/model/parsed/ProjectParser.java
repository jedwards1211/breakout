package org.breakout.model.parsed;

import static org.andork.util.JavaScript.falsy;
import static org.andork.util.JavaScript.truthy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.function.BiFunction;

import org.andork.model.Property;
import org.andork.unit.Unit;
import org.andork.unit.UnitType;
import org.andork.unit.UnitizedDouble;
import org.andork.util.StringUtils;
import org.breakout.model.CrossSectionType;
import org.breakout.model.ShotKey;
import org.breakout.model.StationKey;
import org.breakout.model.parsed.ParseMessage.Severity;
import org.breakout.model.raw.SurveyRow;
import org.breakout.model.raw.SurveyTrip;

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

	private final IdentityHashMap<SurveyTrip, ParsedTrip> trips = new IdentityHashMap<>();
	private final DateFormat[] dateFormats = {
			new SimpleDateFormat("yyyy/MM/dd"),
			new SimpleDateFormat("yyyy-MM-dd"),
			new SimpleDateFormat("MM/dd/yyyy"),
			new SimpleDateFormat("MM-dd-yyyy")
	};

	private static <T, U extends UnitType<U>> ParsedField<UnitizedDouble<U>> parse(
			T object, Property<T, String> property,
			BiFunction<String, Unit<U>, ParsedField<UnitizedDouble<U>>> parser, Unit<U> defaultUnit) {
		String text = property.get(object);
		return parser.apply(text, defaultUnit);
	}

	private <T, U extends UnitType<U>> ParsedField<UnitizedDouble<U>> parse(
			T object, Property<T, String> property,
			BiFunction<String, Unit<U>, ParsedField<UnitizedDouble<U>>> parser, Unit<U> defaultUnit,
			Severity missingSeverity) {
		ParsedField<UnitizedDouble<U>> result = parse(object, property, parser, defaultUnit);
		if ((result == null || result.value == null) && missingSeverity != null) {
			result.message = new ParseMessage(missingSeverity, "missing " + property.name());
		}
		return result;
	}

	private <T, U extends UnitType<U>> ParsedField<UnitizedDouble<U>> parse(
			T object, Property<T, String> property,
			BiFunction<String, Unit<U>, ParsedField<UnitizedDouble<U>>> parser, Unit<U> defaultUnit,
			Severity missingSeverity, double defaultValue) {
		ParsedField<UnitizedDouble<U>> result = parse(object, property, parser, defaultUnit, missingSeverity);
		return result != null ? result : new ParsedField<>(new UnitizedDouble<>(defaultValue, defaultUnit));
	}

	public ParsedTrip parse(SurveyTrip raw) {
		ParsedTrip parsed = trips.get(raw);
		if (parsed != null) {
			return parsed;
		}
		parsed = new ParsedTrip();
		trips.put(raw, parsed);
		String caveName = raw.getCave();
		if (caveName == null) {
			caveName = "";
		}
		ParsedCave cave = project.caves.get(caveName);
		if (cave == null) {
			cave = new ParsedCave();
			cave.name = new ParsedField<>(caveName);
			project.caves.put(raw.getCave(), cave);
		}
		cave.trips.add(parsed);

		parsed.distanceCorrection = parse(raw, SurveyTrip.Properties.distanceCorrection,
				MetacaveLengthParser::parse, raw.getDistanceUnit(), null, 0);
		parsed.declination = parse(raw, SurveyTrip.Properties.declination,
				MetacaveAngleParser::parse, raw.getAngleUnit(), null, 0);
		parsed.frontAzimuthCorrection = parse(raw, SurveyTrip.Properties.frontAzimuthCorrection,
				MetacaveAngleParser::parse, raw.getFrontAzimuthUnit(), null, 0);
		parsed.backAzimuthCorrection = parse(raw, SurveyTrip.Properties.backAzimuthCorrection,
				MetacaveAngleParser::parse, raw.getBackAzimuthUnit(), null, 0);
		parsed.frontInclinationCorrection = parse(raw, SurveyTrip.Properties.frontInclinationCorrection,
				MetacaveAngleParser::parse, raw.getFrontInclinationUnit(), null, 0);
		parsed.backInclinationCorrection = parse(raw, SurveyTrip.Properties.backInclinationCorrection,
				MetacaveAngleParser::parse, raw.getBackInclinationUnit(), null, 0);
		parsed.areBackAzimuthsCorrected = raw.areBackAzimuthsCorrected();
		parsed.areBackInclinationsCorrected = raw.areBackInclinationsCorrected();
		String dateText = raw.getDate();
		if (dateText != null) {
			dateText = dateText.trim();
		}
		if (raw.getDate() != null && !raw.getDate().isEmpty()) {
			parsed.date = new ParsedField<>(Severity.ERROR, "invalid date");
			for (DateFormat format : dateFormats) {
				try {
					parsed.date = new ParsedField<>(format.parse(dateText));
					break;
				} catch (ParseException ex) {
					// ignore
				}
			}
		}

		return parsed;
	}

	private ParsedStation parseFromStation(SurveyRow raw, SurveyTrip trip) {
		if (falsy(raw.getFromStation())) {
			return null;
		}
		ParsedStation result = new ParsedStation();
		result.cave = new ParsedField<>(raw.getFromCave());
		result.name = new ParsedField<>(raw.getFromStation());

		result.crossSection = parseCrossSection(raw, trip);

		return result;
	}

	private ParsedStation parseToStation(SurveyRow raw, SurveyTrip trip) {
		if (falsy(raw.getToStation())) {
			return null;
		}
		ParsedStation result = new ParsedStation();
		result.cave = new ParsedField<>(raw.getToCave());
		result.name = new ParsedField<>(raw.getToStation());

		return result;
	}

	@SuppressWarnings("unchecked")
	private ParsedCrossSection parseCrossSection(SurveyRow raw, SurveyTrip trip) {
		if (falsy(raw.getLeft()) && falsy(raw.getRight()) &&
				falsy(raw.getUp()) && falsy(raw.getDown())) {
			return null;
		}
		ParsedCrossSection result = new ParsedCrossSection();
		result.measurements = new ParsedField[] {
				parse(raw, SurveyRow.Properties.left, MetacaveLengthParser::parse, trip.getDistanceUnit()),
				parse(raw, SurveyRow.Properties.right, MetacaveLengthParser::parse, trip.getDistanceUnit()),
				parse(raw, SurveyRow.Properties.up, MetacaveLengthParser::parse, trip.getDistanceUnit()),
				parse(raw, SurveyRow.Properties.down, MetacaveLengthParser::parse, trip.getDistanceUnit())
		};
		result.type = CrossSectionType.LRUD;
		return result;
	}

	private ParsedShotMeasurement parseFrontsights(SurveyRow row, SurveyTrip trip) {
		if (falsy(row.getDistance()) &&
				falsy(row.getFrontAzimuth()) && falsy(row.getFrontInclination())) {
			return null;
		}
		ParsedShotMeasurement measurement = new ParsedShotMeasurement();
		measurement.isBacksight = false;
		measurement.distance = parse(row, SurveyRow.Properties.distance,
				MetacaveLengthParser::parse, trip.getDistanceUnit());
		measurement.azimuth = parse(row, SurveyRow.Properties.frontAzimuth,
				MetacaveAzimuthParser::parse, trip.getFrontAzimuthUnit());
		measurement.inclination = parse(row, SurveyRow.Properties.frontInclination,
				MetacaveAzimuthParser::parse, trip.getBackAzimuthUnit());
		return measurement;
	}

	private ParsedShotMeasurement parseBacksights(SurveyRow row, SurveyTrip trip) {
		if (falsy(row.getDistance()) &&
				falsy(row.getBackAzimuth()) && falsy(row.getBackInclination())) {
			return null;
		}
		ParsedShotMeasurement measurement = new ParsedShotMeasurement();
		measurement.isBacksight = true;
		measurement.distance = parse(row, SurveyRow.Properties.distance,
				MetacaveLengthParser::parse, trip.getDistanceUnit());
		measurement.azimuth = parse(row, SurveyRow.Properties.backAzimuth,
				MetacaveAzimuthParser::parse, trip.getBackAzimuthUnit());
		measurement.inclination = parse(row, SurveyRow.Properties.backInclination,
				MetacaveAzimuthParser::parse, trip.getBackAzimuthUnit());
		return measurement;
	}

	private ParsedNEVLocation parseNEVLocation(SurveyRow raw, SurveyTrip trip) {
		ParsedNEVLocation location = new ParsedNEVLocation();
		location.northing = parse(raw, SurveyRow.Properties.northing,
				MetacaveLengthParser::parse, trip.getDistanceUnit());
		location.easting = parse(raw, SurveyRow.Properties.easting,
				MetacaveLengthParser::parse, trip.getDistanceUnit());
		location.elevation = parse(raw, SurveyRow.Properties.elevation,
				MetacaveLengthParser::parse, trip.getDistanceUnit());
		return location;
	}

	public String getString(String field) {
		return truthy(field) ? field : null;
	}

	public String getString(ParsedField<String> field) {
		return field != null && truthy(field.value) ? field.value : null;
	}

	public ShotKey parse(SurveyRow raw) {
		SurveyTrip trip = raw.getTrip();
		ParsedTrip parsedTrip = parse(trip);
		ParsedCave cave = project.caves.get(trip.getCave());

		ParsedStation fromStation = parsedTrip.stations.isEmpty()
				? null
				: parsedTrip.stations.get(parsedTrip.stations.size() - 1);
		if (fromStation == null ||
				!Objects.equals(getString(fromStation.cave), getString(raw.getFromCave())) ||
				!Objects.equals(getString(fromStation.name), getString(raw.getFromStation()))) {
			fromStation = parseFromStation(raw, trip);
			if (fromStation != null) {
				if (!parsedTrip.shots.isEmpty()) {
					parsedTrip.shots.add(null);
				}
				parsedTrip.stations.add(fromStation);
			}
		}
		ParsedStation toStation = parseToStation(raw, trip);

		if (fromStation != null) {
			ParsedNEVLocation location = parseNEVLocation(raw, trip);
			if (location != null) {
				ParsedFixedStation station = new ParsedFixedStation();
				station.cave = fromStation.cave;
				station.name = fromStation.name;
				station.location = location;
				cave.fixedStations.put(station.name.value, station);
			}

			if (fromStation.crossSection == null) {
				fromStation.crossSection = parseCrossSection(raw, trip);
			}
		}

		ParsedShotMeasurement frontsights = parseFrontsights(raw, trip);
		ParsedShotMeasurement backsights = parseBacksights(raw, trip);
		if (frontsights == null && backsights == null) {
			return null;
		}

		if (fromStation != null && toStation == null) {
			if (fromStation.splays == null) {
				fromStation.splays = new ArrayList<>();
			}
			ParsedSplayShot splay = new ParsedSplayShot();
			splay.measurements = new ArrayList<>(1);
			if (frontsights != null) {
				splay.measurements.add(frontsights);
			}
			if (backsights != null) {
				splay.measurements.add(backsights);
			}
			fromStation.splays.add(splay);
		} else if (fromStation != null && toStation != null) {
			ParsedShot shot = new ParsedShot();
			shot.measurements = new ArrayList<>(2);
			if (frontsights != null) {
				shot.measurements.add(frontsights);
			}
			if (backsights != null) {
				shot.measurements.add(backsights);
			}
			parsedTrip.shots.add(shot);
			parsedTrip.stations.add(toStation);

			StationKey fromKey = fromStation.key();
			StationKey toKey = toStation.key();
			if (fromKey == null || toKey == null) {
				return null;
			}
			ShotKey shotKey = new ShotKey(fromKey, toKey);
			project.shots.put(shotKey, shot);
			return shotKey;
		}

		return null;
	}
}
