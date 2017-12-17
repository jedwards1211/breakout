package org.breakout.model.calc;

import static org.breakout.util.StationNames.getSurveyDesignation;

import java.util.IdentityHashMap;
import java.util.Map;

import org.andork.task.Task;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;
import org.breakout.model.ShotKey;
import org.breakout.model.StationKey;
import org.breakout.model.parsed.ParsedCave;
import org.breakout.model.parsed.ParsedCrossSection;
import org.breakout.model.parsed.ParsedField;
import org.breakout.model.parsed.ParsedFixedStation;
import org.breakout.model.parsed.ParsedNEVLocation;
import org.breakout.model.parsed.ParsedProject;
import org.breakout.model.parsed.ParsedShot;
import org.breakout.model.parsed.ParsedShotMeasurement;
import org.breakout.model.parsed.ParsedStation;
import org.breakout.model.parsed.ParsedTrip;

/**
 * Parses ParsedRows and SurveyTrips into graph of CalcStations, CalcShots, and
 * CalcTrips. Station positions and passage walls can then be calculated on the
 * graph.
 */
public class Parsed2Calc {
	public final CalcProject project;

	final Map<ParsedTrip, CalcTrip> trips = new IdentityHashMap<>();

	public Parsed2Calc() {
		this(new CalcProject());
	}

	public Parsed2Calc(CalcProject project) {
		super();
		this.project = project;
	}

	public void convert(ParsedProject project, Task<?> task) throws Exception {
		task.setTotal(project.caves.size());
		for (ParsedCave cave : project.caves.values()) {
			task.runSubtask(1, subtask -> convert(cave, subtask));
		}
	}

	void convert(ParsedCave cave, Task<?> task) throws Exception {
		String caveName = ParsedField.hasValue(cave.name) ? cave.name.value : "";
		CalcCave calcCave = project.caves.get(caveName);
		if (calcCave == null) {
			calcCave = new CalcCave();
			calcCave.name = caveName;
			project.caves.put(calcCave.name, calcCave);
		}
		final CalcCave finalCalcCave = calcCave;
		task.runSubtasks(tripTask -> {
			tripTask.setTotal(cave.trips.size());
			for (ParsedTrip trip : cave.trips) {
				convert(trip, finalCalcCave);
				tripTask.increment();
			}
		}, fixedStationTask -> {
			fixedStationTask.setTotal(cave.fixedStations.size());
			for (ParsedFixedStation fixedStation : cave.fixedStations.values()) {
				convert(fixedStation, finalCalcCave);
				fixedStationTask.increment();
			}
		});
	}

	void convert(ParsedFixedStation fixedStation, CalcCave cave) {
		CalcStation station = project.stations.get(fixedStation.key());
		if (station == null) {
			return;
		}
		if (fixedStation.location instanceof ParsedNEVLocation) {
			ParsedNEVLocation nev = (ParsedNEVLocation) fixedStation.location;
			if (ParsedField.hasValue(nev.northing)) {
				station.position[2] = -nev.northing.value.doubleValue(Length.meters);
			}
			if (ParsedField.hasValue(nev.easting)) {
				station.position[0] = nev.easting.value.doubleValue(Length.meters);
			}
			if (ParsedField.hasValue(nev.elevation)) {
				station.position[1] = nev.elevation.value.doubleValue(Length.meters);
			}
		}
	}

	void convert(ParsedTrip trip, CalcCave cave) {
		CalcTrip calcTrip = trips.get(trip);
		if (calcTrip == null) {
			calcTrip = new CalcTrip();
			calcTrip.cave = cave;
			trips.put(trip, calcTrip);
		}
		cave.trips.add(calcTrip);
		for (int i = 0; i < trip.shots.size(); i++) {
			ParsedShot parsedShot = trip.shots.get(i);
			if (parsedShot == null) {
				continue;
			}
			ParsedStation parsedFromStation = trip.stations.get(i);
			ParsedStation parsedToStation = trip.stations.get(i + 1);
			CalcShot calcShot = convert(parsedFromStation, parsedShot, parsedToStation, trip);
			if (calcShot.key() != null) {
				calcTrip.shots.put(calcShot.key(), calcShot);
			}
			calcShot.trip = calcTrip;
			if (calcShot.fromStation != null) {
				cave.stationsBySurveyDesignation.put(
						getSurveyDesignation(calcShot.fromStation.name), calcShot.fromStation);
			}
			if (calcShot.toStation != null) {
				cave.stationsBySurveyDesignation.put(
						getSurveyDesignation(calcShot.toStation.name), calcShot.toStation);
			}
		}
	}

	CalcShot convert(ParsedStation fromStation, ParsedShot shot, ParsedStation toStation,
			ParsedTrip trip) {
		CalcShot result = new CalcShot();
		result.date = ParsedField.getValue(trip.date);

		convertDistance(shot, trip, result);
		convertAzimuth(shot, trip, result);
		convertInclination(shot, trip, result);

		link(fromStation, shot, toStation, result);
		convertLruds(fromStation, shot, toStation, result);

		return result;
	}

	public void convertDistance(ParsedShot parsed, ParsedTrip trip, CalcShot shot) {
		double total = 0.0;
		int count = 0;
		for (ParsedShotMeasurement measurement : parsed.measurements) {
			if (!ParsedField.hasValue(measurement.distance)) {
				continue;
			}
			total += measurement.distance.value.doubleValue(Length.meters);
			count++;
		}
		shot.distance = total / count;
		if (ParsedField.hasValue(trip.distanceCorrection)) {
			shot.distance += trip.distanceCorrection.value.doubleValue(Length.meters);
		}
	}

	public void convertAzimuth(ParsedShot parsed, ParsedTrip trip, CalcShot shot) {
		double x = 0;
		double y = 0;
		for (ParsedShotMeasurement measurement : parsed.measurements) {
			UnitizedDouble<Angle> azimuth = ParsedField.getValue(measurement.azimuth);
			if (azimuth == null) {
				continue;
			}
			if (measurement.isBacksight && !trip.areBackAzimuthsCorrected) {
				azimuth = Angle.opposite(azimuth);
			}
			azimuth = azimuth.add(measurement.isBacksight
					? trip.backAzimuthCorrection.value
					: trip.frontAzimuthCorrection.value);
			double angle = azimuth.doubleValue(Angle.radians);
			x += Math.cos(angle);
			y += Math.sin(angle);
		}
		shot.azimuth = Math.atan2(y, x) + trip.declination.value.get(Angle.radians);
	}

	public void convertInclination(ParsedShot parsed, ParsedTrip trip, CalcShot shot) {
		double total = 0.0;
		int count = 0;
		for (ParsedShotMeasurement measurement : parsed.measurements) {
			UnitizedDouble<Angle> inclination = ParsedField.getValue(measurement.inclination);
			if (inclination == null) {
				continue;
			}
			if (measurement.isBacksight && !trip.areBackInclinationsCorrected) {
				inclination = inclination.negate();
			}
			inclination = inclination.add(measurement.isBacksight
					? trip.backInclinationCorrection.value
					: trip.frontInclinationCorrection.value);
			total += inclination.doubleValue(Angle.radians);
			count++;
		}
		shot.inclination = total / count;
	}

	public void link(ParsedStation parsedFromStation, ParsedShot parsed, ParsedStation parsedToStation, CalcShot shot) {
		StationKey fromKey = parsedFromStation.key();
		StationKey toKey = parsedToStation.key();

		if (fromKey != null) {
			if (fromKey.equals(toKey)) {
				return;
			}
			CalcStation fromStation = project.stations.get(fromKey);
			if (fromStation == null) {
				fromStation = new CalcStation();
				fromStation.name = fromKey.station;
				fromStation.cave = fromKey.cave;
				project.stations.put(fromKey, fromStation);
			}
			shot.fromStation = fromStation;
		}

		if (toKey != null) {
			CalcStation toStation = project.stations.get(toKey);
			if (toStation == null) {
				toStation = new CalcStation();
				toStation.name = toKey.station;
				toStation.cave = toKey.cave;
				project.stations.put(toKey, toStation);
			}
			shot.toStation = toStation;
		}

		if (fromKey != null && toKey != null) {
			shot.fromStation.shots.put(toKey, shot);
			shot.toStation.shots.put(fromKey, shot);

			ShotKey shotKey = new ShotKey(fromKey, toKey);

			project.shots.put(shotKey, shot);
		}
	}

	public CalcCrossSection convert(ParsedCrossSection section) {
		CalcCrossSection result = new CalcCrossSection();
		result.facingAzimuth = ParsedField.hasValue(section.facingAzimuth)
				? section.facingAzimuth.value.doubleValue(Angle.radians)
				: Double.NaN;
		result.measurements = new double[section.measurements.length];
		for (int i = 0; i < section.measurements.length; i++) {
			result.measurements[i] = ParsedField.hasValue(section.measurements[i])
					? section.measurements[i].value.doubleValue(Length.meters)
					: 0.05;
		}
		return result;
	}

	public void convertLruds(ParsedStation parsedFromStation, ParsedShot parsed, ParsedStation parsedToStation,
			CalcShot shot) {
		if (parsedFromStation.crossSection != null) {
			shot.fromCrossSection = convert(parsedFromStation.crossSection);
		}
		if (parsedToStation.crossSection != null) {
			shot.toCrossSection = convert(parsedToStation.crossSection);
		}
	}
}
