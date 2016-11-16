package org.breakout.model;

import static org.andork.util.JavaScript.isFinite;
import static org.andork.util.JavaScript.or;
import static org.andork.util.StringUtils.toStringOrNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.andork.collect.CollectionUtils;
import org.andork.math.misc.AngleUtils;
import org.andork.math3d.Vecmath;
import org.andork.swing.async.Subtask;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.breakout.model.SurveyTableModel.Row;
import org.breakout.model.SurveyTableModel.Trip;

public class SurveyTableParser {
	private static class ParsedTripHeader {
		double declination;
		double distanceCorrection;
		double frontAzimuthCorrection;
		double frontInclinationCorrection;
		double backAzimuthCorrection;
		double backInclinationCorrection;
	}

	private static class ShotKey {
		final String fromCave;
		final String fromStation;
		final String toCave;
		final String toStation;

		public ShotKey(Row row) {
			this(row.getFromCave(), row.getFromStation(), row.getToCave(), row.getToStation());
		}

		public ShotKey(String fromCave, String fromStation, String toCave, String toStation) {
			super();
			this.fromCave = or(fromCave, "");
			this.fromStation = fromStation;
			this.toCave = or(toCave, "");
			this.toStation = toStation;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}

			ShotKey other = (ShotKey) obj;

			if (fromCave.equals(other.fromCave) && fromStation.equals(other.fromStation)) {
				return toCave.equals(other.toCave) && toStation.equals(other.toStation);
			}
			// reverse is equal too
			else if (fromCave.equals(other.toCave) && fromStation.equals(other.toStation)) {
				return toCave.equals(other.fromCave) && toStation.equals(other.fromStation);
			}
			return false;
		}

		@Override
		public int hashCode() {
			String fromCave = this.fromCave;
			String fromStation = this.fromStation;
			String toCave = this.toCave;
			String toStation = this.toStation;

			// alphabetize so that reverse of this shot has the same hash code
			if (fromCave.compareTo(toCave) > 0 || fromStation.compareTo(toStation) > 0) {
				// swap
				fromCave = this.toCave;
				fromStation = this.toStation;
				toCave = this.fromCave;
				toStation = this.fromStation;
			}
			final int prime = 31;
			int result = 1;
			result = prime * result + fromCave.hashCode();
			result = prime * result + fromStation.hashCode();
			result = prime * result + toCave.hashCode();
			result = prime * result + toStation.hashCode();
			return result;
		}
	}

	private static class StationKey {
		final String cave;
		final String station;

		public StationKey(String cave, String station) {
			this.cave = or(cave, "");
			this.station = or(station, "");
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			StationKey other = (StationKey) obj;
			return cave.equals(other.cave) && station.equals(other.station);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + cave.hashCode();
			result = prime * result + station.hashCode();
			return result;
		}

	}

	private static final Trip defaultTrip = new Trip();

	public static List<Shot> createShots(List<Row> rows, Subtask subtask) {
		if (subtask != null) {
			subtask.setTotal(rows.size());
		}

		// MetacaveExporter exporter = new MetacaveExporter();
		// exporter.export(rows);
		// Gson gson = new GsonBuilder().setPrettyPrinting().create();
		// gson.toJson(exporter.getRoot(), System.out);

		Map<StationKey, Station> stations = new LinkedHashMap<>();
		Map<ShotKey, Shot> shots = new LinkedHashMap<>();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		List<Shot> shotList = new ArrayList<Shot>();

		IdentityHashMap<Trip, ParsedTripHeader> parsedTripHeaders = new IdentityHashMap<>();

		int i = 0;
		for (Iterator<Row> iter = rows.iterator(); iter.hasNext(); i++) {
			Row row = iter.next();
			final Trip trip = row.getTrip() == null ? defaultTrip : row.getTrip();
			Function<String, Double> toMeters = s -> Length.type.convert(
					parse(s), trip.getDistanceUnit(), Length.meters);
			Function<String, Float> toMetersf = s -> Length.type.convertf(
					parseFloat(s), trip.getDistanceUnit(), Length.meters);
			ParsedTripHeader header = parsedTripHeaders.get(trip);
			if (header == null) {
				header = parseTripHeader(trip);
				parsedTripHeaders.put(trip, header);
			}

			Shot shot = null;

			try {
				String fromName = toStringOrNull(row.getFromStation());
				String toName = toStringOrNull(row.getToStation());
				double dist = toMeters.apply(row.getDistance());
				double fsAzm = Angle.type.convert(parse(row.getFrontAzimuth()), trip.getFrontAzimuthUnit(),
						Angle.radians);
				double bsAzm = Angle.type.convert(parse(row.getBackAzimuth()), trip.getBackAzimuthUnit(),
						Angle.radians);
				double fsInc = Angle.type.convert(parse(row.getFrontInclination()), trip.getFrontInclinationUnit(),
						Angle.radians);
				double bsInc = Angle.type.convert(parse(row.getBackInclination()), trip.getBackInclinationUnit(),
						Angle.radians);

				if (!trip.areBackAzimuthsCorrected()) {
					bsAzm = AngleUtils.oppositeAngle(bsAzm);
				}
				if (!trip.areBackInclinationsCorrected()) {
					bsInc = -bsInc;
				}

				float left = toMetersf.apply(row.getLeft());
				float right = toMetersf.apply(row.getRight());
				float up = toMetersf.apply(row.getUp());
				float down = toMetersf.apply(row.getDown());

				if (fromName == null || toName == null) {
					continue;
				}

				shot = shots.get(new ShotKey(row));
				if (shot == null) {
					shot = new Shot();
				} else {
					// TODO make sure caves are reversed too
					if (shot.from.equals(row.getToStation()) &&
							shot.to.equals(row.getFromStation())) {
						// reverse measurements
						shot = new Shot();
						String s = fromName;
						fromName = toName;
						toName = s;

						double d = fsAzm;
						fsAzm = bsAzm;
						bsAzm = d;

						d = fsInc;
						fsInc = bsInc;
						bsInc = d;
					}

					if (Double.isNaN(dist) || Double.isNaN(fsInc) && Double.isNaN(bsInc)) {
						continue;
					}
				}

				double north = toMeters.apply(row.getNorthing());
				double east = toMeters.apply(row.getEasting());
				double elev = toMeters.apply(row.getElevation());

				Station from = getStation(stations, new StationKey(row.getFromCave(), row.getFromStation()));
				Station to = getStation(stations, new StationKey(row.getToCave(), row.getToStation()));

				Vecmath.setdNoNaNOrInf(from.position, east, elev, -north);

				shot = new Shot();
				shot.from = from;
				shot.to = to;
				shot.dist = dist + header.distanceCorrection;
				shot.inc = Shot.averageInc(
						fsInc + header.frontInclinationCorrection,
						bsInc + header.backInclinationCorrection);
				shot.azm = AngleUtils.normalize(
						Shot.averageAzm(
								fsAzm + header.frontAzimuthCorrection,
								bsAzm + header.backAzimuthCorrection)
								+ header.declination);
				shot.desc = row.getTrip() == null ? null : row.getTrip().getName();

				try {
					shot.date = dateFormat.parse(trip.getDate());
				} catch (Exception ex) {

				}

				CrossSection xSection = shot.fromXsection;
				xSection.type = CrossSectionType.LRUD;
				xSection.dist[0] = isFinite(left) ? left : xSection.dist[0];
				xSection.dist[1] = isFinite(right) ? right : xSection.dist[1];
				xSection.dist[2] = isFinite(up) ? up : xSection.dist[2];
				xSection.dist[3] = isFinite(down) ? down : xSection.dist[3];

				if (subtask != null) {
					if (subtask.isCanceling()) {
						return null;
					}
					subtask.setCompleted(i);
				}
			} catch (Exception ex) {
				shot = null;
			} finally {
				if (shot != null) {
					shots.put(new ShotKey(row), shot);
				}
				// DO add null shots to shotList
				shotList.add(shot);
			}
		}

		for (Shot shot : shots.values()) {
			shot.from.shots.add(shot);
			shot.to.shots.add(shot);
		}

		boolean fixedStationFound = false;

		for (Station station : stations.values()) {
			updateCrossSections(station);
			if (!Vecmath.hasNaNsOrInfinites(station.position)) {
				fixedStationFound = true;
			}
		}

		// if there are no fixed stations, set the first station's position to 0
		if (!fixedStationFound && !stations.isEmpty()) {
			Station station = stations.values().iterator().next();
			Arrays.fill(station.position, 0);
		}

		int number = 0;
		for (Shot shot : shotList) {
			if (shot != null) {
				shot.number = number++;
			}
		}

		return shotList;
	}

	private static Station getStation(Map<StationKey, Station> stations, StationKey key) {
		Station station = stations.get(key);
		if (station == null) {
			station = new Station();
			station.name = key.station;
			stations.put(key, station);
		}
		return station;
	}

	private static double parse(Object o) {
		return parse(o, Double.NaN);
	}

	private static double parse(Object o, double defaultValue) {
		if (o == null) {
			return defaultValue;
		}
		try {
			return Double.valueOf(o.toString());
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	private static float parseFloat(Object o) {
		if (o == null) {
			return Float.NaN;
		}
		try {
			return Float.valueOf(o.toString());
		} catch (Exception ex) {
			return Float.NaN;
		}
	}

	private static ParsedTripHeader parseTripHeader(Trip trip) {
		ParsedTripHeader result = new ParsedTripHeader();
		if (trip == null) {
			return result;
		}
		result.declination = Angle.type.convert(
				parse(trip.getDeclination(), 0),
				trip.getAngleUnit(), Angle.radians);
		result.distanceCorrection = Length.type.convert(
				parse(trip.getDistanceCorrection(), 0),
				trip.getDistanceUnit(), Length.meters);
		result.frontAzimuthCorrection = Angle.type.convert(
				parse(trip.getFrontAzimuthCorrection(), 0),
				trip.getFrontAzimuthUnit(), Angle.radians);
		result.frontInclinationCorrection = Angle.type.convert(
				parse(trip.getFrontInclinationCorrection(), 0),
				trip.getFrontInclinationUnit(), Angle.radians);
		result.backAzimuthCorrection = Angle.type.convert(
				parse(trip.getBackAzimuthCorrection(), 0),
				trip.getBackAzimuthUnit(), Angle.radians);
		result.backInclinationCorrection = Angle.type.convert(
				parse(trip.getBackInclinationCorrection(), 0),
				trip.getBackInclinationUnit(), Angle.radians);
		return result;
	}

	private static void updateCrossSections(Station station) {
		if (station.shots.size() == 2) {
			Iterator<Shot> shotIter = station.shots.iterator();
			Shot shot1 = shotIter.next();
			Shot shot2 = shotIter.next();
			CrossSection sect1 = shot1.crossSectionAt(station);
			CrossSection sect2 = shot2.crossSectionAt(station);

			boolean opposite = station == shot1.from == (station == shot2.from);

			for (int i = 0; i < Math.min(sect1.dist.length, sect2.dist.length); i++) {
				int oi = i > 1 ? i : opposite ? 1 - i : i;

				if (Double.isNaN(sect1.dist[i])) {
					sect1.dist[i] = sect2.dist[oi];
				}
				if (Double.isNaN(sect2.dist[i])) {
					sect2.dist[i] = sect1.dist[oi];
				}
			}
		}

		int populatedCount = CollectionUtils.moveToFront(station.shots, shot -> {
			CrossSection section = shot.crossSectionAt(station);
			return section.type == CrossSectionType.LRUD && !Double.isNaN(section.dist[0])
					&& !Double.isNaN(section.dist[1]);
		});

		for (int i = populatedCount; i < station.shots.size(); i++) {
			Shot shot = station.shots.get(i);
			CrossSection section = shot.crossSectionAt(station);
			if (section.type == CrossSectionType.LRUD) {
				double leftAzm = shot.azm - Math.PI * 0.5;
				double rightAzm = shot.azm + Math.PI * 0.5;

				boolean populateLeft = Double.isNaN(section.dist[0]);
				boolean populateRight = Double.isNaN(section.dist[0]);

				for (int i2 = 0; i2 < populatedCount; i2++) {
					Shot populated = station.shots.get(i2);
					CrossSection popCrossSection = populated.crossSectionAt(station);

					double popLeftAzm = populated.azm - Math.PI * 0.5;
					double popRightAzm = populated.azm + Math.PI * 0.5;

					if (populateLeft) {
						double candidateLeft;
						candidateLeft = popCrossSection.dist[0] * Math.cos(AngleUtils.angle(leftAzm, popLeftAzm));
						section.dist[0] = (float) Vecmath.nmax(section.dist[0], candidateLeft);
						candidateLeft = popCrossSection.dist[1] * Math.cos(AngleUtils.angle(leftAzm, popRightAzm));
						section.dist[0] = (float) Vecmath.nmax(section.dist[0], candidateLeft);
					}

					if (populateRight) {
						double candidateRight;
						candidateRight = popCrossSection.dist[0] * Math.cos(AngleUtils.angle(rightAzm, popLeftAzm));
						section.dist[1] = (float) Vecmath.nmax(section.dist[1], candidateRight);
						candidateRight = popCrossSection.dist[1] * Math.cos(AngleUtils.angle(rightAzm, popRightAzm));
						section.dist[1] = (float) Vecmath.nmax(section.dist[1], candidateRight);
					}
				}
			}
		}

		for (Shot shot : station.shots) {
			CrossSection sect1 = shot.crossSectionAt(station);
			CrossSection sect2 = shot.crossSectionAt(shot.otherStation(station));

			for (int i = 0; i < Math.min(sect1.dist.length, sect2.dist.length); i++) {
				if (Double.isNaN(sect1.dist[i])) {
					sect1.dist[i] = sect2.dist[i];
				}
			}
		}
	}
}
