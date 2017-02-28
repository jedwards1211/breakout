package org.breakout.model;

import java.util.IdentityHashMap;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public class CalcProjectParser {
	public CalcProject project;

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

	public CalcTrip parse(SurveyTrip trip) {
		CalcTrip calcTrip = trips.get(trip);
		if (calcTrip != null) {
			return calcTrip;
		}
		calcTrip = new CalcTrip();
		trips.put(trip, calcTrip);

		calcTrip.distanceCorrection = lengthParser.parse(trip.getDistanceCorrection(),
				trip.getDistanceUnit(), 0);
		if (lengthParser.message != null) {
			calcTrip.addMessage(SurveyTrip.Properties.distanceCorrection, lengthParser.message);
		}
		calcTrip.declination = angleParser.parse(trip.getDeclination(), trip.getAngleUnit(), 0);
		if (angleParser.message != null) {
			calcTrip.addMessage(SurveyTrip.Properties.declination, angleParser.message);
		}
		calcTrip.frontAzimuthCorrection = angleParser.parse(trip.getFrontAzimuthCorrection(),
				trip.getFrontAzimuthUnit(), 0);
		if (angleParser.message != null) {
			calcTrip.addMessage(SurveyTrip.Properties.frontAzimuthCorrection, angleParser.message);
		}
		calcTrip.backAzimuthCorrection = angleParser.parse(trip.getBackAzimuthCorrection(),
				trip.getBackAzimuthUnit(), 0);
		if (angleParser.message != null) {
			calcTrip.addMessage(SurveyTrip.Properties.backAzimuthCorrection, angleParser.message);
		}
		calcTrip.frontInclinationCorrection = angleParser.parse(trip.getFrontInclinationCorrection(),
				trip.getFrontInclinationUnit(), 0);
		if (angleParser.message != null) {
			calcTrip.addMessage(SurveyTrip.Properties.frontInclinationCorrection, angleParser.message);
		}
		calcTrip.backInclinationCorrection = angleParser.parse(trip.getBackInclinationCorrection(),
				trip.getBackInclinationUnit(), 0);
		if (angleParser.message != null) {
			calcTrip.addMessage(SurveyTrip.Properties.backInclinationCorrection, angleParser.message);
		}

		return calcTrip;
	}

	public CalcRow parse(SurveyRow row) {
		CalcRow shot = new CalcRow();
		shot.trip = parse(row.getTrip());

		parseDistance(row, shot);
		parseAzimuth(row, shot);
		parseInclination(row, shot);
		parseLruds(row, shot);

		linkRow(row, shot);
		parseNev(row, shot);

		return shot;
	}

	public void parseDistance(SurveyRow row, CalcRow shot) {
		UnitizedDouble<Length> distance = lengthParser.parse(row.getDistance(),
				row.getTrip().getDistanceUnit(), null);
		if (lengthParser.message != null) {
			shot.addMessage(SurveyRow.Properties.distance, lengthParser.message);
		}
		if (distance != null) {
			shot.distance = distance.add(shot.trip.distanceCorrection);
		}
	}

	public void parseAzimuth(SurveyRow row, CalcRow shot) {
		SurveyTrip trip = row.getTrip();
		UnitizedDouble<Angle> frontAzimuth = azimuthParser.parse(row.getFrontAzimuth(),
				trip.getFrontAzimuthUnit(), null);
		if (azimuthParser.message != null) {
			shot.addMessage(SurveyRow.Properties.frontAzimuth, azimuthParser.message);
		}

		UnitizedDouble<Angle> backAzimuth = azimuthParser.parse(row.getBackAzimuth(),
				trip.getBackAzimuthUnit(), null);
		if (azimuthParser.message != null) {
			shot.addMessage(SurveyRow.Properties.backAzimuth, azimuthParser.message);
		}
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

	public void parseInclination(SurveyRow row, CalcRow shot) {
		SurveyTrip trip = row.getTrip();
		UnitizedDouble<Angle> frontInclination = inclinationParser.parse(row.getFrontInclination(),
				trip.getFrontInclinationUnit(), null);
		if (inclinationParser.message != null) {
			shot.addMessage(SurveyRow.Properties.frontInclination, inclinationParser.message);
		}

		UnitizedDouble<Angle> backInclination = inclinationParser.parse(row.getBackInclination(),
				trip.getBackInclinationUnit(), null);
		if (inclinationParser.message != null) {
			shot.addMessage(SurveyRow.Properties.backInclination, inclinationParser.message);
		}

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

	public void parseLruds(SurveyRow row, CalcRow shot) {
		SurveyTrip trip = row.getTrip();
		shot.left = lengthParser.parse(row.getLeft(), trip.getDistanceUnit(), null);
		if (lengthParser.message != null) {
			shot.addMessage(SurveyRow.Properties.left, lengthParser.message);
		}
		shot.right = lengthParser.parse(row.getRight(), trip.getDistanceUnit(), null);
		if (lengthParser.message != null) {
			shot.addMessage(SurveyRow.Properties.right, lengthParser.message);
		}
		shot.up = lengthParser.parse(row.getUp(), trip.getDistanceUnit(), null);
		if (lengthParser.message != null) {
			shot.addMessage(SurveyRow.Properties.up, lengthParser.message);
		}
		shot.down = lengthParser.parse(row.getDown(), trip.getDistanceUnit(), null);
		if (lengthParser.message != null) {
			shot.addMessage(SurveyRow.Properties.down, lengthParser.message);
		}
	}

	public void parseNev(SurveyRow row, CalcRow shot) {
		SurveyTrip trip = row.getTrip();
		UnitizedDouble<Length> northing = lengthParser.parse(row.getNorthing(), trip.getDistanceUnit(), null);
		if (lengthParser.message != null) {
			shot.addMessage(SurveyRow.Properties.northing, lengthParser.message);
		}
		UnitizedDouble<Length> easting = lengthParser.parse(row.getEasting(), trip.getDistanceUnit(), null);
		if (lengthParser.message != null) {
			shot.addMessage(SurveyRow.Properties.easting, lengthParser.message);
		}
		UnitizedDouble<Length> elevation = lengthParser.parse(row.getElevation(), trip.getDistanceUnit(), null);
		if (lengthParser.message != null) {
			shot.addMessage(SurveyRow.Properties.elevation, lengthParser.message);
		}

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

	public void linkRow(SurveyRow row, CalcRow shot) {
		project.rows.add(shot);

		StationKey fromKey = row.getFromStation() != null
				? new StationKey(row.getFromCave(), row.getFromStation()) : null;
		StationKey toKey = row.getToStation() != null
				? new StationKey(row.getToCave(), row.getToStation()) : null;

		if (fromKey != null) {
			if (fromKey.equals(toKey)) {
				shot.addMessage(SurveyRow.Properties.toStation,
						ParseMessage.error("to station is the same as from station"));
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
