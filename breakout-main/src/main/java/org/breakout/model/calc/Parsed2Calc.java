package org.breakout.model.calc;

import static org.breakout.util.StationNames.getSurveyDesignation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.andork.collect.SmallArrayMap;
import org.andork.task.Task;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;
import org.breakout.model.ShotKey;
import org.breakout.model.StationKey;
import org.breakout.model.parsed.Lead;
import org.breakout.model.parsed.ParsedCave;
import org.breakout.model.parsed.ParsedCrossSection;
import org.breakout.model.parsed.ParsedField;
import org.breakout.model.parsed.ParsedFixedStation;
import org.breakout.model.parsed.ParsedLatLonLocation;
import org.breakout.model.parsed.ParsedNEVLocation;
import org.breakout.model.parsed.ParsedProject;
import org.breakout.model.parsed.ParsedShot;
import org.breakout.model.parsed.ParsedShotMeasurement;
import org.breakout.model.parsed.ParsedStation;
import org.breakout.model.parsed.ParsedTrip;
import org.breakout.proj4.IdentityCoordinateTransform;
import org.breakout.proj4.ToGeocentricCoordinateTransform;
import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.ProjCoordinate;
import org.osgeo.proj4j.datum.Ellipsoid;
import org.osgeo.proj4j.datum.GeocentricConverter;

/**
 * Parses ParsedRows and SurveyTrips into graph of CalcStations, CalcShots, and
 * CalcTrips. Station positions and passage walls can then be calculated on the
 * graph.
 */
public class Parsed2Calc {
	public final CalcProject project;

	final Map<ParsedTrip, CalcTrip> trips = new IdentityHashMap<>();
	int numFixedStations = 0;
	final Map<String, List<ParsedFixedStation>> fixedStationsByCrs = new HashMap<>();

	private static final CRSFactory crsFactory = new CRSFactory();
	private static final ProjCoordinate projCoord = new ProjCoordinate();

	public Parsed2Calc() {
		this(new CalcProject());
	}

	public Parsed2Calc(CalcProject project) {
		super();
		this.project = project;
	}

	public void convert(ParsedProject project, Task<?> task) throws Exception {
		task.setTotal(project.caves.size() + 3);
		for (ParsedCave cave : project.caves.values()) {
			task.runSubtask(1, subtask -> convert(cave, subtask));
		}
		task.runSubtask(1, stationTask -> {
			stationTask.setTotal(this.project.stations.size());
			for (CalcStation station : this.project.stations.values()) {
				station.shots = station.numShots > 5
					? new LinkedHashMap<>(station.numShots)
					: new SmallArrayMap<>(station.numShots);
				stationTask.increment();
			}
		});
		task.runSubtask(1, shotTask -> {
			shotTask.setTotal(this.project.shots.size());
			for (CalcShot shot : this.project.shots.values()) {
				shot.fromStation.shots.put(shot.toStation.key(), shot);
				shot.toStation.shots.put(shot.fromStation.key(), shot);
				shotTask.increment();
			}
		});
		task.runSubtask(1, fixedStationTask -> {
			if (fixedStationsByCrs.isEmpty()) {
				return;
			}

			fixedStationTask.setStatus("performing coordinate conversions");

			if (fixedStationsByCrs.size() == 1 && fixedStationsByCrs.keySet().iterator().next().isEmpty()) {
				List<ParsedFixedStation> stations = fixedStationsByCrs.values().iterator().next();
				fixedStationTask.setTotal(stations.size() + 1);
				double[] avgNev = fixedStationTask.callSubtask(1,
					avgTask -> getAverageNEV(stations, avgTask));
				CoordinateReferenceSystem toCrs = crsFactory.createFromParameters(null,
					"+proj=aeqd" + 
					" +x_0=" + avgNev[0] +
					" +y_0=" + avgNev[1] +
					" +ellps=WGS84");
				CoordinateReferenceSystem fromCrs = crsFactory.createFromParameters(null,
					"+proj=aeqd +ellps=WGS84");
				
				CoordinateTransform xform = new BasicCoordinateTransform(fromCrs, toCrs);
				CoordinateTransform geoXform = new BasicCoordinateTransform(fromCrs.createGeographic(), toCrs);

				for (ParsedFixedStation station : stations) {
					convert(station, xform, geoXform);
					fixedStationTask.increment();
				}
				return;
			}

			fixedStationTask.setTotal(fixedStationsByCrs.size() + 1);

			ProjCoordinate avgLatLong = fixedStationTask.callSubtask(1,
				avgTask -> getAverageLatLong(fixedStationsByCrs, avgTask));

			CoordinateReferenceSystem toCrs = crsFactory.createFromParameters(null,
				"+proj=aeqd" +
				" +lat_0=" + avgLatLong.y +
				" +lon_0=" + avgLatLong.x +
				" +ellps=WGS84");
			this.project.coordinateReferenceSystem = toCrs;

			for (Map.Entry<String, List<ParsedFixedStation>> entry : fixedStationsByCrs.entrySet()) {
				CoordinateReferenceSystem fromCrs = crsFactory.createFromParameters(null, entry.getKey());
				CoordinateTransform xform = new BasicCoordinateTransform(fromCrs, toCrs);
				CoordinateTransform geoXform = new BasicCoordinateTransform(fromCrs.createGeographic(), toCrs);
				for (ParsedFixedStation station : entry.getValue()) {
					convert(station, xform, geoXform);
				}
				fixedStationTask.increment();
			}
		});
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
		}, leadTask -> {
			leadTask.setTotal(cave.leads.size());
			for (Map.Entry<String, List<Lead>> e : cave.leads.entrySet()) {
				convert(new StationKey(caveName, e.getKey()), e.getValue());
				leadTask.increment();
			}
		});
	}
	
	void convert(StationKey stationKey, List<Lead> leads) {
		CalcStation station = project.stations.get(stationKey);
		if (station != null) station.leads = leads;
	}
	
	double[] getAverageNEV(List<ParsedFixedStation> stations, Task<?> task) {
		task.setTotal(stations.size());
			
		int i = 0;
		double[] x = new double[stations.size()];
		double[] y = new double[stations.size()];
		double[] z = new double[stations.size()];

		ProjCoordinate projCoord = new ProjCoordinate();

		for (ParsedFixedStation station : stations) {
			if (station.location instanceof ParsedNEVLocation) {
				getLocation(station, projCoord, IdentityCoordinateTransform.INSTANCE, IdentityCoordinateTransform.INSTANCE);
			}
			if (Double.isFinite(projCoord.x) &&
				Double.isFinite(projCoord.y) &&
				Double.isFinite(projCoord.z)) {
				x[i] = projCoord.x;
				y[i] = projCoord.y;
				z[i] = projCoord.z;
				i++;
			}
			task.increment();
		}
		
		return new double[] {
			average(x, 0, i),
			average(y, 0, i),
			average(z, 0, i),
		};
	}

	ProjCoordinate getAverageLatLong(Map<String, List<ParsedFixedStation>> stations, Task<?> task) {
		int numFixedStations = stations.values().stream()
			.map(group -> group.size())
			.reduce(0, (total, next) -> total + next);

		task.setTotal(numFixedStations);
			
		int i = 0;
		double[] x = new double[numFixedStations];
		double[] y = new double[numFixedStations];
		double[] z = new double[numFixedStations];
		
		ProjCoordinate projCoord = new ProjCoordinate();

		for (Map.Entry<String, List<ParsedFixedStation>> entry : stations.entrySet()) {
			CoordinateReferenceSystem sourceCrs = crsFactory.createFromParameters(null, entry.getKey());
			ToGeocentricCoordinateTransform sourceToGeocentric = new ToGeocentricCoordinateTransform(sourceCrs);
			CoordinateReferenceSystem sourceGeodeticCrs = sourceCrs.createGeographic();
			ToGeocentricCoordinateTransform sourceGeodeticToGeocentric = new ToGeocentricCoordinateTransform(sourceGeodeticCrs);
			for (ParsedFixedStation station : entry.getValue()) {
				getLocation(station, projCoord, sourceToGeocentric, sourceGeodeticToGeocentric);
				if (Double.isFinite(projCoord.x) &&
					Double.isFinite(projCoord.y) &&
					Double.isFinite(projCoord.z)) {
					x[i] = projCoord.x;
					y[i] = projCoord.y;
					z[i] = projCoord.z;
					i++;
				}
				task.increment();
			}
		}	
		
		projCoord.x = average(x, 0, i);
		projCoord.y = average(y, 0, i);
		projCoord.z = average(z, 0, i);
		
		GeocentricConverter converter = new GeocentricConverter(Ellipsoid.WGS84);
		converter.convertGeocentricToGeodetic(projCoord);
		projCoord.x = Math.toDegrees(projCoord.x);
		projCoord.y = Math.toDegrees(projCoord.y);
		
		return projCoord;
	}
	
	static double average(double[] arr, int start, int end) {
		int n = end - start;
		if (n < 1000) {
			double total = 0;
			for (int i = start; i < end; i++) {
				total += arr[i];
			}
			return total / n;
		}
		int mid = (start + end) / 2;
		return (average(arr, start, mid) + average(arr, mid, end)) / 2;
	}
	
	static ProjCoordinate getLocation(ParsedFixedStation fixedStation, ProjCoordinate projCoord, CoordinateTransform xform, CoordinateTransform geoXform) {
		if (fixedStation.location instanceof ParsedNEVLocation) {
			ParsedNEVLocation nev = (ParsedNEVLocation) fixedStation.location;
			if (ParsedField.hasValue(nev.northing)) {
				projCoord.y = nev.northing.value.doubleValue(Length.meters);
			} else {
				projCoord.y = Double.NaN;
			}
			if (ParsedField.hasValue(nev.easting)) {
				projCoord.x = nev.easting.value.doubleValue(Length.meters);
			} else {
				projCoord.x = Double.NaN;
			}
			if (ParsedField.hasValue(nev.elevation)) {
				projCoord.z = nev.elevation.value.doubleValue(Length.meters);
			} else {
				projCoord.z = Double.NaN;
			}
			xform.transform(projCoord, projCoord);
		} else if (fixedStation.location instanceof ParsedLatLonLocation) {
			ParsedLatLonLocation loc = (ParsedLatLonLocation) fixedStation.location;
			if (ParsedField.hasValue(loc.latitude)) {
				projCoord.y = loc.latitude.value.doubleValue(Angle.degrees);
			} else {
				projCoord.y = Double.NaN;
			}
			if (ParsedField.hasValue(loc.longitude)) {
				projCoord.x = loc.longitude.value.doubleValue(Angle.degrees);
			} else {
				projCoord.x = Double.NaN;
			}
			if (ParsedField.hasValue(loc.elevation)) {
				projCoord.z = loc.elevation.value.doubleValue(Length.meters);
			} else {
				projCoord.z = Double.NaN;
			}
			geoXform.transform(projCoord, projCoord);
		}
		return projCoord;
	}

	void convert(ParsedFixedStation fixedStation) {
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

	void convert(ParsedFixedStation fixedStation, CoordinateTransform xform, CoordinateTransform geoXform) {
		CalcStation station = project.stations.get(fixedStation.key());
		if (station == null) {
			return;
		}
		getLocation(fixedStation, projCoord, xform, geoXform);
		station.position[0] = projCoord.x;
		station.position[1] = projCoord.z;
		station.position[2] = -projCoord.y;
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
			calcShot.setHasSurveyNotes(parsedShot.hasSurveyNotes());
			calcShot.setExcludeDistance(parsedShot.isExcludeDistance());
			calcShot.setExcludeFromPlotting(parsedShot.isExcludeFromPlotting());
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
		String crs = ParsedField.hasValue(trip.datum)
			? (ParsedField.hasValue(trip.utmZone)
					? "+proj=utm +zone=" + trip.utmZone.value
					: "") +
				" +datum=" + trip.datum.value +
				(ParsedField.hasValue(trip.ellipsoid) 
					? " +ellps=" + trip.ellipsoid.value
					: "")
			: "";
		for (ParsedFixedStation station : trip.fixedStations.values()) {
			List<ParsedFixedStation> stations = fixedStationsByCrs.get(crs);
			if (stations == null) {
				stations = new ArrayList<>();
				fixedStationsByCrs.put(crs, stations);
			}
			stations.add(station);
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
			shot.fromStation.numShots++;
			shot.toStation.numShots++;

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
