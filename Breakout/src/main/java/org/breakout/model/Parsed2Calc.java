package org.breakout.model;

import java.util.Objects;

import org.andork.math.misc.AngleUtils;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

/**
 * Parses ParsedRows and SurveyTrips into graph of CalcStations, CalcShots, and
 * CalcTrips. Station positions and passage walls can then be calculated on the
 * graph.
 */
public class Parsed2Calc {
	public final CalcProject project;
	public final ParseMessages messages = new ParseMessages();

	public Parsed2Calc() {
		this(new CalcProject());
	}

	public Parsed2Calc(CalcProject project) {
		super();
		this.project = project;
	}

	public void convert(ParsedProject project) {
		for (ParsedTrip trip : project.trips) {
			convert(trip);
		}
	}

	void convert(ParsedTrip trip) {
		ParsedRow prevRow = null;
		CalcShot prevShot = null;
		for (ParsedRow row : trip.rows) {
			if (row.key() != null) {
				CalcShot shot = convert(row);
				if (prevRow != null && prevShot != null) {
					if (Objects.equals(prevRow.toKey(), row.fromKey())) {
						shot.prev = prevShot;
						prevShot.next = shot;
					} else if (Objects.equals(prevRow.fromKey(), row.toKey())) {
						shot.next = prevShot;
						prevShot.prev = shot;
					}
				}
				prevShot = shot;
			} else {
				prevShot = null;
			}
			prevRow = row;
		}
	}

	CalcShot convert(ParsedRow parsed) {
		CalcShot shot = new CalcShot();
		shot.date = parsed.trip.date;

		convertDistance(parsed, shot);
		convertAzimuth(parsed, shot);
		convertInclination(parsed, shot);

		link(parsed, shot);
		convertLruds(parsed, shot);
		convertNev(parsed, shot);

		return shot;
	}

	public void convertDistance(ParsedRow parsed, CalcShot shot) {
		if (parsed.distance != null) {
			shot.distance = parsed.distance.add(parsed.trip.distanceCorrection).doubleValue(Length.meters);
		} else {
			shot.distance = Double.NaN;
		}
	}

	public void convertAzimuth(ParsedRow parsed, CalcShot shot) {
		UnitizedDouble<Angle> frontAzimuth = parsed.frontAzimuth;
		UnitizedDouble<Angle> backAzimuth = parsed.backAzimuth;
		if (frontAzimuth != null) {
			frontAzimuth = Angle.normalize(frontAzimuth.add(parsed.trip.frontAzimuthCorrection));
		}
		if (backAzimuth != null) {
			backAzimuth = Angle.normalize(backAzimuth.add(parsed.trip.backAzimuthCorrection));
			if (!parsed.trip.areBackAzimuthsCorrected) {
				backAzimuth = Angle.opposite(backAzimuth);
			}
		}
		if (frontAzimuth != null) {
			if (backAzimuth != null) {
				shot.azimuth = AngleUtils.average(
						frontAzimuth.doubleValue(Angle.radians),
						backAzimuth.doubleValue(Angle.radians));
			} else {
				shot.azimuth = frontAzimuth.doubleValue(Angle.radians);
			}
		} else if (backAzimuth != null) {
			shot.azimuth = backAzimuth.doubleValue(Angle.radians);
		} else {
			shot.azimuth = Double.NaN;
		}
		shot.azimuth += parsed.trip.declination.get(Angle.radians);
	}

	public void convertInclination(ParsedRow parsed, CalcShot shot) {
		UnitizedDouble<Angle> frontInclination = parsed.frontInclination;
		UnitizedDouble<Angle> backInclination = parsed.backInclination;

		if (backInclination != null) {
			shot.inclination = backInclination.add(parsed.trip.backInclinationCorrection)
					.doubleValue(Angle.radians);
			if (!parsed.trip.areBackInclinationsCorrected) {
				shot.inclination = -shot.inclination;
			}
			if (frontInclination != null) {
				shot.inclination = (shot.inclination +
						frontInclination.add(parsed.trip.frontInclinationCorrection)
								.doubleValue(Angle.radians))
						* 0.5;
			}
		} else if (frontInclination != null) {
			shot.inclination = frontInclination.add(parsed.trip.frontInclinationCorrection)
					.doubleValue(Angle.radians);
		} else {
			shot.inclination = Double.NaN;
		}
	}

	public void link(ParsedRow parsed, CalcShot shot) {
		StationKey fromKey = parsed.fromStation != null
				? new StationKey(parsed.fromCave, parsed.fromStation) : null;
		StationKey toKey = parsed.toStation != null
				? new StationKey(parsed.toCave, parsed.toStation) : null;

		if (fromKey != null) {
			if (fromKey.equals(toKey)) {
				return;
			}
			CalcStation fromStation = project.stations.get(fromKey);
			if (fromStation == null) {
				fromStation = new CalcStation();
				fromStation.name = parsed.fromStation;
				fromStation.cave = parsed.fromCave;
				project.stations.put(fromKey, fromStation);
			}
			shot.fromStation = fromStation;
		}

		if (toKey != null) {
			CalcStation toStation = project.stations.get(toKey);
			if (toStation == null) {
				toStation = new CalcStation();
				toStation.name = parsed.toStation;
				toStation.cave = parsed.toCave;
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

	public void convertLruds(ParsedRow parsed, CalcShot shot) {
		if (shot.fromStation == null) {
			return;
		}

		if (parsed.left != null || parsed.right != null ||
				parsed.up != null || parsed.down != null) {
			CalcCrossSection crossSection = new CalcCrossSection();

			crossSection.type = parsed.crossSectionType;
			crossSection.measurements = new double[] {
					parsed.left != null ? parsed.left.doubleValue(Length.meters) : 0,
					parsed.right != null ? parsed.right.doubleValue(Length.meters) : 0,
					parsed.up != null ? parsed.up.doubleValue(Length.meters) : 0,
					parsed.down != null ? parsed.down.doubleValue(Length.meters) : 0,
			};

			if (shot.toStation != null) {
				shot.fromCrossSection = crossSection;
				crossSection.facingAzimuth = shot.azimuth;
			} else {
				// go through all so that we get the most recent shot that
				// matches
				for (CalcShot other : shot.fromStation.shots.values()) {
					if (other.toStation == shot.fromStation && other.toCrossSection == null) {
						other.toCrossSection = crossSection;
						crossSection.facingAzimuth = other.azimuth;
						break;
					} else if (other.fromStation == shot.fromStation && other.fromCrossSection == null) {
						other.fromCrossSection = crossSection;
						crossSection.facingAzimuth = other.azimuth;
						break;
					}
				}
			}
		}
	}

	public void convertNev(ParsedRow parsed, CalcShot shot) {
		if (shot.fromStation != null) {
			if (parsed.northing != null) {
				shot.fromStation.position[0] = parsed.easting.doubleValue(Length.meters);
			}
			if (parsed.easting != null) {
				shot.fromStation.position[1] = parsed.elevation.doubleValue(Length.meters);
			}
			if (parsed.elevation != null) {
				shot.fromStation.position[2] = -parsed.northing.doubleValue(Length.meters);
			}
		}
	}
}
