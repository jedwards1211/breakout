package org.breakout.model;

import static org.andork.util.StringUtils.toStringOrNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.andork.collect.CollectionUtils;
import org.andork.math.misc.AngleUtils;
import org.andork.math3d.Vecmath;
import org.andork.swing.async.Subtask;
import org.breakout.model.NewSurveyTableModel.Row;
import org.breakout.model.NewSurveyTableModel.Trip;

public class SurveyTableParser {
	private static float coalesceNaNOrInf(float a, float b) {
		return Float.isNaN(a) || Float.isInfinite(a) ? b : a;
	}

	public static List<Shot> createShots(List<Row> rows, Subtask subtask) {
		if (subtask != null) {
			subtask.setTotal(rows.size());
		}

		Map<String, Station> stations = new LinkedHashMap<String, Station>();
		Map<String, Shot> shots = new LinkedHashMap<String, Shot>();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		List<Shot> shotList = new ArrayList<Shot>();

		int i = 0;
		for (Iterator<Row> iter = rows.iterator(); iter.hasNext(); i++) {
			Row row = iter.next();
			Trip trip = row.getTrip();

			Shot shot = null;

			try {
				String fromName = toStringOrNull(row.getFromStation());
				String toName = toStringOrNull(row.getToStation());
				double dist = parse(row.getDistance());
				double fsAzm = Math.toRadians(parse(row.getFrontAzimuth()));
				double bsAzm = Math.toRadians(parse(row.getBackAzimuth()));
				double fsInc = Math.toRadians(parse(row.getFrontInclination()));
				double bsInc = Math.toRadians(parse(row.getBackInclination()));

				float left = parseFloat(row.getLeft());
				float right = parseFloat(row.getRight());
				float up = parseFloat(row.getUp());
				float down = parseFloat(row.getDown());

				if (fromName == null || toName == null) {
					continue;
				}

				shot = shots.get(Shot.getName(fromName, toName));
				if (shot == null) {
					shot = shots.get(Shot.getName(toName, fromName));
					if (shot != null) {
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
					} else {
						if (Double.isNaN(dist) || Double.isNaN(fsInc) && Double.isNaN(bsInc)) {
							continue;
						}
					}
				}

				double north = parse(row.getNorthing());
				double east = parse(row.getEasting());
				double elev = parse(row.getElevation());

				Station from = getStation(stations, fromName);
				Station to = getStation(stations, toName);

				Vecmath.setdNoNaNOrInf(from.position, east, elev, -north);

				shot = new Shot();
				shot.from = from;
				shot.to = to;
				shot.dist = dist;
				shot.inc = Shot.averageInc(fsInc, bsInc);
				shot.azm = Shot.averageAzm(shot.inc, fsAzm, bsAzm);
				// shot.desc = row.get(Row.desc);

				try {
					shot.date = dateFormat.parse(trip.getDate());
				} catch (Exception ex) {

				}

				CrossSection xSection = shot.fromXsection;
				xSection.type = CrossSectionType.LRUD;
				xSection.dist[0] = coalesceNaNOrInf(left, xSection.dist[0]);
				xSection.dist[1] = coalesceNaNOrInf(right, xSection.dist[1]);
				xSection.dist[2] = coalesceNaNOrInf(up, xSection.dist[2]);
				xSection.dist[3] = coalesceNaNOrInf(down, xSection.dist[3]);

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
					shots.put(shotName(shot), shot);
				}
				// DO add null shots to shotList
				shotList.add(shot);
			}
		}

		for (Shot shot : shots.values()) {
			shot.from.shots.add(shot);
			shot.to.shots.add(shot);
		}

		for (Station station : stations.values()) {
			updateCrossSections(station);
		}

		int number = 0;
		for (Shot shot : shotList) {
			if (shot != null) {
				shot.number = number++;
			}
		}

		return shotList;
	}

	private static Station getStation(Map<String, Station> stations, String name) {
		Station station = stations.get(name);
		if (station == null) {
			station = new Station();
			station.name = name;
			stations.put(name, station);
		}
		return station;
	}

	private static double parse(Object o) {
		if (o == null) {
			return Double.NaN;
		}
		try {
			return Double.valueOf(o.toString());
		} catch (Exception ex) {
			return Double.NaN;
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

	protected static String shotName(Shot shot) {
		return shot.from.name + " - " + shot.to.name;
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
