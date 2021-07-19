package org.breakout.model.parsed;

import static org.andork.util.StringUtils.isNullOrEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import org.andork.datescraper.DateMatcher;
import org.andork.datescraper.DatePatterns;
import org.andork.model.Property;
import org.andork.unit.Angle;
import org.andork.unit.Unit;
import org.andork.unit.UnitType;
import org.andork.unit.UnitizedDouble;
import org.breakout.model.CrossSectionType;
import org.breakout.model.ShotKey;
import org.breakout.model.StationKey;
import org.breakout.model.parsed.ParseMessage.Severity;
import org.breakout.model.raw.MutableSurveyLead;
import org.breakout.model.raw.SurveyLead;
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

	private static final DateMatcher dateMatcher = DatePatterns.en_US.matcher("");

	public static Date parseDate(String s) {
		return s != null && dateMatcher.reset(s).find() ? dateMatcher.match() : null;
	}

	private static <T, U extends UnitType<U>> ParsedField<UnitizedDouble<U>> parse(
		T object,
		Property<T, String> property,
		BiFunction<String, Unit<U>, ParsedField<UnitizedDouble<U>>> parser,
		Unit<U> defaultUnit) {
		String text = property.get(object);
		return parser.apply(text, defaultUnit);
	}

	private <T, U extends UnitType<U>> ParsedField<UnitizedDouble<U>> parse(
		T object,
		Property<T, String> property,
		BiFunction<String, Unit<U>, ParsedField<UnitizedDouble<U>>> parser,
		Unit<U> defaultUnit,
		Severity missingSeverity) {
		ParsedField<UnitizedDouble<U>> result = parse(object, property, parser, defaultUnit);
		if ((result == null || result.value == null) && missingSeverity != null) {
			result.message = new ParseMessage(missingSeverity, "missing " + property.name());
		}
		return result;
	}

	private <T, U extends UnitType<U>> ParsedField<UnitizedDouble<U>> parse(
		T object,
		Property<T, String> property,
		BiFunction<String, Unit<U>, ParsedField<UnitizedDouble<U>>> parser,
		Unit<U> defaultUnit,
		Severity missingSeverity,
		double defaultValue) {
		ParsedField<UnitizedDouble<U>> result = parse(object, property, parser, defaultUnit, missingSeverity);
		return result != null ? result : new ParsedField<>(new UnitizedDouble<>(defaultValue, defaultUnit));
	}

	private ParsedCave ensureCave(String caveName) {
		if (caveName == null)
			caveName = "";
		ParsedCave cave = project.caves.get(caveName);
		if (cave == null) {
			cave = new ParsedCave();
			cave.name = new ParsedField<>(caveName);
			project.caves.put(caveName, cave);
		}
		return cave;
	}

	public ParsedTrip parse(SurveyTrip raw) {
		ParsedTrip parsed = trips.get(raw);
		if (parsed != null) {
			return parsed;
		}
		parsed = new ParsedTrip();
		trips.put(raw, parsed);
		String caveName = raw.getCave();
		ParsedCave cave = ensureCave(caveName);
		cave.trips.add(parsed);

		parsed.name = raw.getName();
		parsed.distanceCorrection =
			parse(
				raw,
				SurveyTrip.Properties.distanceCorrection,
				MetacaveLengthParser::parse,
				raw.getDistanceUnit(),
				null,
				0);
		parsed.declination =
			parse(raw, SurveyTrip.Properties.declination, MetacaveAngleParser::parse, raw.getAngleUnit(), null, 0);
		parsed.frontAzimuthCorrection =
			parse(
				raw,
				SurveyTrip.Properties.frontAzimuthCorrection,
				MetacaveAngleParser::parse,
				raw.getFrontAzimuthUnit(),
				null,
				0);
		parsed.backAzimuthCorrection =
			parse(
				raw,
				SurveyTrip.Properties.backAzimuthCorrection,
				MetacaveAngleParser::parse,
				raw.getBackAzimuthUnit(),
				null,
				0);
		parsed.frontInclinationCorrection =
			parse(
				raw,
				SurveyTrip.Properties.frontInclinationCorrection,
				MetacaveAngleParser::parse,
				raw.getFrontInclinationUnit(),
				null,
				0);
		parsed.backInclinationCorrection =
			parse(
				raw,
				SurveyTrip.Properties.backInclinationCorrection,
				MetacaveAngleParser::parse,
				raw.getBackInclinationUnit(),
				null,
				0);
		parsed.areBackAzimuthsCorrected = raw.areBackAzimuthsCorrected();
		parsed.areBackInclinationsCorrected = raw.areBackInclinationsCorrected();
		String dateText = raw.getDate();
		if (dateText != null) {
			dateText = dateText.trim();
		}
		if (!isNullOrEmpty(raw.getDate())) {
			Date parsedDate = parseDate(dateText);
			parsed.date =
				parsedDate != null ? new ParsedField<>(parsedDate) : new ParsedField<>(Severity.ERROR, "invalid date");
		}
		if (!isNullOrEmpty(raw.getDatum())) {
			parsed.datum = new ParsedField<>(raw.getDatum());
		}
		if (!isNullOrEmpty(raw.getEllipsoid())) {
			parsed.ellipsoid = new ParsedField<>(raw.getEllipsoid());
		}
		if (!isNullOrEmpty(raw.getUtmZone())) {
			try {
				parsed.utmZone = new ParsedField<>(Integer.valueOf(raw.getUtmZone()));
				if (parsed.utmZone.value < 1 || parsed.utmZone.value > 60) {
					parsed.utmZone = new ParsedField<>(Severity.ERROR, "UTM zone out of range");
				}
			}
			catch (Exception ex) {
				parsed.utmZone = new ParsedField<>(Severity.ERROR, "invalid UTM zone");
			}
		}

		parsed.attachedFiles = raw.getAttachedFiles();

		return parsed;
	}

	private ParsedStation parseFromStation(SurveyRow raw, SurveyTrip trip) {
		if (isNullOrEmpty(raw.getFromStation())) {
			return null;
		}
		ParsedStation result = new ParsedStation();
		result.cave = new ParsedField<>(raw.getFromCave());
		result.name = new ParsedField<>(raw.getFromStation());

		result.crossSection = parseCrossSection(raw, trip);

		return result;
	}

	private ParsedStation parseToStation(SurveyRow raw, SurveyTrip trip) {
		if (isNullOrEmpty(raw.getToStation())) {
			return null;
		}
		ParsedStation result = new ParsedStation();
		result.cave = new ParsedField<>(raw.getToCave());
		result.name = new ParsedField<>(raw.getToStation());

		return result;
	}

	@SuppressWarnings("unchecked")
	private ParsedCrossSection parseCrossSection(SurveyRow raw, SurveyTrip trip) {
		if (isNullOrEmpty(raw.getLeft())
			&& isNullOrEmpty(raw.getRight())
			&& isNullOrEmpty(raw.getUp())
			&& isNullOrEmpty(raw.getDown())) {
			return null;
		}
		ParsedCrossSection result = new ParsedCrossSection();
		result.measurements =
			new ParsedField[]
			{
				parse(raw, SurveyRow.Properties.left, MetacaveLengthParser::parse, trip.getDistanceUnit()),
				parse(raw, SurveyRow.Properties.right, MetacaveLengthParser::parse, trip.getDistanceUnit()),
				parse(raw, SurveyRow.Properties.up, MetacaveLengthParser::parse, trip.getDistanceUnit()),
				parse(raw, SurveyRow.Properties.down, MetacaveLengthParser::parse, trip.getDistanceUnit()) };
		result.type = CrossSectionType.LRUD;
		return result;
	}

	private ParsedShotMeasurement parseFrontsights(SurveyRow row, SurveyTrip trip) {
		if (isNullOrEmpty(row.getDistance())
			&& isNullOrEmpty(row.getFrontAzimuth())
			&& isNullOrEmpty(row.getFrontInclination())) {
			return null;
		}
		ParsedShotMeasurement measurement = new ParsedShotMeasurement();
		measurement.isBacksight = false;
		measurement.distance =
			parse(row, SurveyRow.Properties.distance, MetacaveLengthParser::parse, trip.getDistanceUnit());
		measurement.azimuth =
			parse(row, SurveyRow.Properties.frontAzimuth, MetacaveAzimuthParser::parse, trip.getFrontAzimuthUnit());
		measurement.inclination =
			parse(row, SurveyRow.Properties.frontInclination, MetacaveAzimuthParser::parse, trip.getBackAzimuthUnit());
		return measurement;
	}

	private ParsedShotMeasurement parseBacksights(SurveyRow row, SurveyTrip trip) {
		if (isNullOrEmpty(row.getDistance())
			&& isNullOrEmpty(row.getBackAzimuth())
			&& isNullOrEmpty(row.getBackInclination())) {
			return null;
		}
		ParsedShotMeasurement measurement = new ParsedShotMeasurement();
		measurement.isBacksight = true;
		measurement.distance =
			parse(row, SurveyRow.Properties.distance, MetacaveLengthParser::parse, trip.getDistanceUnit());
		measurement.azimuth =
			parse(row, SurveyRow.Properties.backAzimuth, MetacaveAzimuthParser::parse, trip.getBackAzimuthUnit());
		measurement.inclination =
			parse(row, SurveyRow.Properties.backInclination, MetacaveAzimuthParser::parse, trip.getBackAzimuthUnit());
		return measurement;
	}

	private ParsedLatLonLocation parseLatLonLocation(SurveyRow raw, SurveyTrip trip) {
		if (isNullOrEmpty(raw.getLatitude())
			&& isNullOrEmpty(raw.getLongitude())
			&& isNullOrEmpty(raw.getElevation())) {
			return null;
		}
		ParsedLatLonLocation location = new ParsedLatLonLocation();
		location.latitude = parse(raw, SurveyRow.Properties.latitude, MetacaveAngleParser::parse, Angle.degrees);
		location.longitude = parse(raw, SurveyRow.Properties.longitude, MetacaveAngleParser::parse, Angle.degrees);
		location.elevation =
			parse(raw, SurveyRow.Properties.elevation, MetacaveLengthParser::parse, trip.getDistanceUnit());
		return location;
	}

	private ParsedNEVLocation parseNEVLocation(SurveyRow raw, SurveyTrip trip) {
		if (isNullOrEmpty(raw.getNorthing()) && isNullOrEmpty(raw.getEasting()) && isNullOrEmpty(raw.getElevation())) {
			return null;
		}
		ParsedNEVLocation location = new ParsedNEVLocation();
		location.northing =
			parse(raw, SurveyRow.Properties.northing, MetacaveLengthParser::parse, trip.getDistanceUnit());
		location.easting =
			parse(raw, SurveyRow.Properties.easting, MetacaveLengthParser::parse, trip.getDistanceUnit());
		location.elevation =
			parse(raw, SurveyRow.Properties.elevation, MetacaveLengthParser::parse, trip.getDistanceUnit());
		return location;
	}

	public String getString(String field) {
		return !isNullOrEmpty(field) ? field : null;
	}

	public String getString(ParsedField<String> field) {
		return field != null && !isNullOrEmpty(field.value) ? field.value : null;
	}

	private static Pattern leadPattern =
		Pattern.compile("\\b(leads?|go(es|ing)?|continues?|dig)\\b", Pattern.CASE_INSENSITIVE);

	public ShotKey parse(SurveyRow raw) {
		SurveyTrip trip = raw.getTrip();
		ParsedTrip parsedTrip = parse(trip);
		ParsedCave cave = ensureCave(trip.getCave());
		String comment = raw.getComment();

		ParsedStation fromStation =
			parsedTrip.stations.isEmpty() ? null : parsedTrip.stations.get(parsedTrip.stations.size() - 1);
		if (fromStation == null
			|| !Objects.equals(getString(fromStation.cave), getString(raw.getFromCave()))
			|| !Objects.equals(getString(fromStation.name), getString(raw.getFromStation()))) {
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
			ParsedLatLonLocation latLonLocation = parseLatLonLocation(raw, trip);
			if ((location != null || latLonLocation != null) && cave != null) {
				ParsedFixedStation station = new ParsedFixedStation();
				station.cave = fromStation.cave;
				station.name = fromStation.name;
				station.location = location != null ? location : latLonLocation;
				parsedTrip.fixedStations.put(station.name.value, station);
			}

			if (fromStation.crossSection == null) {
				fromStation.crossSection = parseCrossSection(raw, trip);
			}

			if (toStation == null) {
				fromStation.comment = comment;
			}
		}

		if (comment != null && leadPattern.matcher(comment).find()) {
			ParsedStation leadStation = fromStation != null ? fromStation : toStation;
			if (leadStation != null) {
				SurveyLead lead =
					new MutableSurveyLead()
						.setCave(leadStation.key().cave)
						.setStation(leadStation.key().station)
						.setDescription(comment)
						.toImmutable();
				project.leads.add(lead);
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
		}
		else if (fromStation != null && toStation != null) {
			toStation.comment = comment;
			ParsedShot shot = new ParsedShot();
			shot.fromStation = fromStation;
			shot.toStation = toStation;
			shot.trip = parsedTrip;
			shot.setHasAttachedFiles(raw.getAttachedFiles() != null && !raw.getAttachedFiles().isEmpty());
			shot.measurements = new ArrayList<>(2);
			if (frontsights != null) {
				shot.measurements.add(frontsights);
			}
			if (backsights != null) {
				shot.measurements.add(backsights);
			}
			parsedTrip.shots.add(shot);
			parsedTrip.stations.add(toStation);
			parsedTrip.surveyors = trip.getSurveyors();

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
