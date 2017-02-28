package org.breakout.model;

import java.util.IdentityHashMap;

import org.andork.model.Property;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitType;
import org.andork.unit.UnitizedDouble;
import org.breakout.model.ParseMessages.Severity;

public class CalcProjectParser {
	public final CalcProject project;
	public final ParseMessages messages = new ParseMessages();

	public CalcProjectParser() {
		this(new CalcProject());
	}

	public CalcProjectParser(CalcProject project) {
		super();
		this.project = project;
	}

	private final MetacaveLengthParser lengthParser = new MetacaveLengthParser();
	private final MetacaveAngleParser angleParser = new MetacaveAngleParser();
	private final MetacaveAzimuthParser azimuthParser = new MetacaveAzimuthParser();
	private final MetacaveInclinationParser inclinationParser = new MetacaveInclinationParser();

	private final IdentityHashMap<SurveyTrip, CalcTrip> trips = new IdentityHashMap<>();

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
			messages.add(object, property, severity, parser.message);
		}
		return result;
	}

	private <T, U extends UnitType<U>> UnitizedDouble<U> parse(T object, Property<T, String> property,
			MetacaveMeasurementParser<U> parser, Unit<U> defaultUnit,
			Severity missingSeverity) {
		String text = property.get(object);
		UnitizedDouble<U> result = text == null || text.isEmpty()
				? null : parse(object, property, parser, defaultUnit);
		if (result == null && missingSeverity != null) {
			messages.add(object, property, missingSeverity, "missing " + property.name());
		}
		return result;
	}

	private <T, U extends UnitType<U>> UnitizedDouble<U> parse(T object, Property<T, String> property,
			MetacaveMeasurementParser<U> parser, Unit<U> defaultUnit,
			Severity missingSeverity, double defaultValue) {
		UnitizedDouble<U> result = parse(object, property, parser, defaultUnit, missingSeverity);
		return result != null ? result : new UnitizedDouble<>(defaultValue, defaultUnit);
	}

	public CalcTrip parse(SurveyTrip trip) {
		CalcTrip calcTrip = trips.get(trip);
		if (calcTrip != null) {
			return calcTrip;
		}
		calcTrip = new CalcTrip();
		trips.put(trip, calcTrip);

		calcTrip.distanceCorrection = parse(trip, SurveyTrip.Properties.distanceCorrection,
				lengthParser, trip.getDistanceUnit(), null, 0);
		calcTrip.declination = parse(trip, SurveyTrip.Properties.declination,
				angleParser, trip.getAngleUnit(), null, 0);
		calcTrip.frontAzimuthCorrection = parse(trip, SurveyTrip.Properties.frontAzimuthCorrection,
				angleParser, trip.getFrontAzimuthUnit(), null, 0);
		calcTrip.backAzimuthCorrection = parse(trip, SurveyTrip.Properties.backAzimuthCorrection,
				angleParser, trip.getBackAzimuthUnit(), null, 0);
		calcTrip.frontInclinationCorrection = parse(trip, SurveyTrip.Properties.frontInclinationCorrection,
				angleParser, trip.getFrontInclinationUnit(), null, 0);
		calcTrip.backInclinationCorrection = parse(trip, SurveyTrip.Properties.backInclinationCorrection,
				angleParser, trip.getBackInclinationUnit(), null, 0);

		return calcTrip;
	}

	public CalcShot parse(SurveyRow row) {
		CalcShot shot = new CalcShot();
		shot.trip = parse(row.getTrip());

		parseDistance(row, shot);
		parseAzimuth(row, shot);
		parseInclination(row, shot);

		linkRow(row, shot);
		parseLruds(row, shot);
		parseNev(row, shot);

		return shot;
	}

	public void parseDistance(SurveyRow row, CalcShot shot) {
		UnitizedDouble<Length> distance = parse(row, SurveyRow.Properties.distance,
				lengthParser, row.getTrip().getDistanceUnit(), null);
		if (distance != null) {
			shot.distance = distance.add(shot.trip.distanceCorrection);
		}
	}

	public void parseAzimuth(SurveyRow row, CalcShot shot) {
		SurveyTrip trip = row.getTrip();

		UnitizedDouble<Angle> frontAzimuth = parse(row, SurveyRow.Properties.frontAzimuth,
				azimuthParser, trip.getFrontAzimuthUnit(), null);
		UnitizedDouble<Angle> backAzimuth = parse(row, SurveyRow.Properties.backAzimuth,
				azimuthParser, trip.getBackAzimuthUnit(), null);

		if (frontAzimuth != null) {
			frontAzimuth = Angle.normalize(frontAzimuth.add(shot.trip.frontAzimuthCorrection));
		}
		if (backAzimuth != null) {
			backAzimuth = Angle.normalize(backAzimuth.add(shot.trip.backAzimuthCorrection));
			if (!trip.areBackAzimuthsCorrected()) {
				backAzimuth = Angle.opposite(backAzimuth);
			}
		}
		if (frontAzimuth != null) {
			if (backAzimuth != null) {
				shot.azimuth = Angle.bisect(frontAzimuth, backAzimuth);
			} else {
				shot.azimuth = frontAzimuth;
			}
		} else if (backAzimuth != null) {
			shot.azimuth = backAzimuth;
		}
		if (shot.azimuth != null) {
			shot.azimuth = shot.azimuth.add(shot.trip.declination);
		}
	}

	public void parseInclination(SurveyRow row, CalcShot shot) {
		SurveyTrip trip = row.getTrip();
		UnitizedDouble<Angle> frontInclination = parse(row, SurveyRow.Properties.frontInclination,
				inclinationParser, trip.getFrontInclinationUnit(), null);
		UnitizedDouble<Angle> backInclination = parse(row, SurveyRow.Properties.backInclination,
				inclinationParser, trip.getBackInclinationUnit(), null);

		if (frontInclination != null) {
			frontInclination = frontInclination.add(shot.trip.frontInclinationCorrection);
		}
		if (backInclination != null) {
			backInclination = backInclination.add(shot.trip.backInclinationCorrection);
			if (!trip.areBackInclinationsCorrected()) {
				backInclination = backInclination.negate();
			}
		}
		if (frontInclination != null) {
			shot.inclination = frontInclination;
			if (backInclination != null) {
				shot.inclination = shot.inclination.add(backInclination).mul(0.5);
			}
		} else if (backInclination != null) {
			shot.inclination = backInclination;
		}
	}

	public void parseLruds(SurveyRow row, CalcShot shot) {
		SurveyTrip trip = row.getTrip();

		@SuppressWarnings("unchecked")
		UnitizedDouble<Length>[] lruds = new UnitizedDouble[4];
		lruds[0] = parse(row, SurveyRow.Properties.left,
				lengthParser, trip.getDistanceUnit(), Severity.WARNING, 0);
		lruds[1] = parse(row, SurveyRow.Properties.right,
				lengthParser, trip.getDistanceUnit(), Severity.WARNING, 0);
		lruds[2] = parse(row, SurveyRow.Properties.up,
				lengthParser, trip.getDistanceUnit(), Severity.WARNING, 0);
		lruds[3] = parse(row, SurveyRow.Properties.down,
				lengthParser, trip.getDistanceUnit(), Severity.WARNING, 0);

		if (shot.fromStation == null) {
			return;
		}

		StationKey toKey = null;
		if (shot.toStation != null) {
			toKey = shot.toStation.key();
		} else {
			for (CalcShot other : shot.fromStation.shots.values()) {
				StationKey key = other.fromStation.key();
				if (other.toStation == shot.fromStation &&
						!shot.fromStation.crossSections.containsKey(key)) {
					toKey = key;
					break;
				}
			}
		}
		if (toKey != null) {
			CalcCrossSection crossSection = new CalcCrossSection();
			crossSection.measurements = lruds;
			shot.fromStation.crossSections.put(toKey, crossSection);
		}
	}

	public void parseNev(SurveyRow row, CalcShot shot) {
		SurveyTrip trip = row.getTrip();
		UnitizedDouble<Length> northing = parse(row, SurveyRow.Properties.northing,
				lengthParser, trip.getDistanceUnit(), null);
		UnitizedDouble<Length> easting = parse(row, SurveyRow.Properties.easting,
				lengthParser, trip.getDistanceUnit(), null);
		UnitizedDouble<Length> elevation = parse(row, SurveyRow.Properties.elevation,
				lengthParser, trip.getDistanceUnit(), null);

		if (shot.fromStation != null) {
			if (northing != null) {
				shot.fromStation.northing = northing;
			}
			if (easting != null) {
				shot.fromStation.easting = easting;
			}
			if (elevation != null) {
				shot.fromStation.elevation = elevation;
			}
		}
	}

	public void linkRow(SurveyRow row, CalcShot shot) {
		project.rows.add(shot);

		StationKey fromKey = row.getFromStation() != null
				? new StationKey(row.getFromCave(), row.getFromStation()) : null;
		StationKey toKey = row.getToStation() != null
				? new StationKey(row.getToCave(), row.getToStation()) : null;

		if (fromKey != null) {
			if (fromKey.equals(toKey)) {
				messages.error(row, SurveyRow.Properties.toStation, "to station is the same as from station");
				return;
			}
			CalcStation fromStation = project.stations.get(fromKey);
			if (fromStation == null) {
				fromStation = new CalcStation();
				fromStation.name = row.getFromStation();
				fromStation.cave = row.getFromCave();
				project.stations.put(fromKey, fromStation);
			}
			shot.fromStation = fromStation;
		}

		if (toKey != null) {
			CalcStation toStation = project.stations.get(toKey);
			if (toStation == null) {
				toStation = new CalcStation();
				toStation.name = row.getToStation();
				toStation.cave = row.getToCave();
				project.stations.put(toKey, toStation);
			}
			shot.toStation = toStation;
		}

		if (fromKey != null && toKey != null) {
			shot.fromStation.shots.put(toKey, shot);
			shot.toStation.shots.put(fromKey, shot);

			ShotKey shotKey = new ShotKey(fromKey, toKey);

			shot.overrides = project.shots.get(shotKey);
			if (shot.overrides != null) {
				shot.overrides.overriddenBy = shot;
			}

			project.shots.put(shotKey, shot);
		}
	}
}
