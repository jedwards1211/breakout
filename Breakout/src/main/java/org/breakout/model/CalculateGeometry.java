package org.breakout.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.andork.graph.Graphs;
import org.andork.math.misc.AngleUtils;

/**
 * All of the routines to compute the passage geometry from the parsed
 * measurements, including the (currently lame) station position calculation.
 */
public class CalculateGeometry {
	public static void calculateGeometry(CalcProject project) {
		interpolateAzimuthsOfVerticalShots(project);
		linkCrossSections(project);
		calculateStationPositions(project);
		calculateSplayNormals(project);
		calculateSplayPoints(project);
	}

	public static double ALMOST_VERTICAL = Math.toRadians(89);

	static boolean isVertical(CalcShot shot) {
		return !Double.isFinite(shot.azimuth) && Math.abs(shot.inclination) > ALMOST_VERTICAL;
	}

	/**
	 * Up to this point, vertical shots don't have an azimuth. This method sets
	 * the azimuth on vertical shots by interpolating the azimuths of the
	 * surrounding non-vertical shots. This is sort of a hack, but it's done so
	 * that cross sections where a non-vertical shot meets a vertical shot will
	 * get displayed properly. It also prevents the vector offset calculations
	 * from crapping out if the azimuth is NaN.
	 */
	static void interpolateAzimuthsOfVerticalShots(CalcProject project) {
		for (CalcShot shot : project.shots.values()) {
			if (isVertical(shot)) {
				// TODO actually interpolate
				shot.azimuth = 0;
			}
		}
	}

	/**
	 * The project passed will mostly only have cross sections assigned at from
	 * stations. We need to figure out what the cross section at the to station
	 * of every shot is -- usually it's just the cross section at the from
	 * station of the next shot, but sometimes more cleverness is needed.
	 */
	static void linkCrossSections(CalcProject project) {
		/*
		 * TODO fix problem junctions in Fisher Ridge: NK5 NK6 TB165 KP84 NT46
		 * nR1 NT81 GA19 QAA$1 Q5 QD$4 QD$12
		 *
		 * nR1 is a particularly tricky junction to solve
		 *
		 * GA19's really weird, makes no sense
		 *
		 * EY38 presents an interesting challenge, LRUD is linked to OB1 but
		 * passage continues to EY39. Should probably prioritize same survey
		 * designation somehow
		 */
		CalcShot prevShot = null;
		Set<CalcCrossSection> joinedCrossSections = new HashSet<>();
		for (CalcShot shot : project.shots.values()) {
			CalcCrossSection fromCrossSection = shot.fromStation.crossSections.get(shot.toStation.key());
			CalcCrossSection toCrossSection = shot.toStation.crossSections.get(shot.fromStation.key());
			if (fromCrossSection != null) {
				if (prevShot != null && prevShot.toStation == shot.fromStation) {
					shot.fromStation.crossSections.put(prevShot.fromStation.key(),
							fromCrossSection);
					joinedCrossSections.add(fromCrossSection);
					fromCrossSection.facingAzimuth = AngleUtils.average(prevShot.azimuth, shot.azimuth);
				} else {
					for (CalcShot other : shot.fromStation.shots.values()) {
						if (other == shot) {
							continue;
						}
						if (!other.toStation.crossSections.containsKey(other.fromStation.key())) {
							other.toStation.crossSections.put(other.fromStation.key(), fromCrossSection);
							if (joinedCrossSections.add(fromCrossSection)) {
								fromCrossSection.facingAzimuth = AngleUtils.average(other.azimuth, shot.azimuth);
							}
							break;
						}
					}
				}
			} else {
				CalcShot lastShot = null;
				for (CalcShot otherShot : shot.fromStation.shots.values()) {
					if (otherShot == shot) {
						continue;
					}
					CalcCrossSection otherCrossSection = otherShot.toStation.crossSections
							.get(otherShot.fromStation.key());
					if (otherCrossSection != null && !joinedCrossSections.contains(otherCrossSection)) {
						lastShot = otherShot;
						fromCrossSection = otherCrossSection;
					}
				}
				if (lastShot != null && fromCrossSection != null) {
					shot.fromStation.crossSections.put(shot.toStation.key(), fromCrossSection);
					if (joinedCrossSections.add(fromCrossSection)) {
						fromCrossSection.facingAzimuth = AngleUtils.average(lastShot.azimuth, shot.azimuth);
					}
				}
			}
			if (prevShot != null && toCrossSection == null && prevShot.fromStation == shot.toStation) {
				toCrossSection = prevShot.fromStation.crossSections.get(prevShot.toStation.key());
				if (toCrossSection != null) {
					shot.toStation.crossSections.put(shot.fromStation.key(), toCrossSection);
					if (joinedCrossSections.add(toCrossSection)) {
						toCrossSection.facingAzimuth = AngleUtils.average(shot.azimuth, prevShot.azimuth);
					}
				}
			}
			prevShot = shot;
		}

		for (CalcShot shot : project.shots.values()) {
			CalcCrossSection fromCrossSection = shot.fromStation.crossSections.get(shot.toStation.key());
			CalcCrossSection toCrossSection = shot.toStation.crossSections.get(shot.fromStation.key());
			// TODO this shouldn't blindly copy the cross section. It should
			// pick an appropriate facing azimuth based upon what else is at
			// the junction, and adjust the cross section to keep uniform
			// passage size with that new facing azimuth. E.g. NT46-NTW1
			// in Fisher Ridge
			if (fromCrossSection == null && toCrossSection != null) {
				shot.fromStation.crossSections.put(shot.toStation.key(), toCrossSection);
			} else if (toCrossSection == null && fromCrossSection != null) {
				shot.toStation.crossSections.put(shot.fromStation.key(), fromCrossSection);
			}
		}
	}

	static float[] createSplayNormals(CalcCrossSection crossSection) {
		if (crossSection == null) {
			float[] result = new float[12];
			Arrays.fill(result, 0f);
			return result;
		}

		float[] result = new float[crossSection.measurements.length * 3];

		// remember:
		// x axis (for coordinates at indices [0, 3, 6, 9]) points east
		// y axis (for coordinates at indices [1, 4, 7, 10]) points up
		// z axis (for coordinates at indices [2, 5, 8, 11]) points **south**

		/*
		 * TODO this whole approach of computing the splay points from the splay
		 * normals is totally wrong. Not only does it create zero length normals
		 * when the measurements are zero, it creates non-unitary normals when
		 * measurements are nonzero.
		 */

		switch (crossSection.type) {
		case LRUD:
			// Left
			result[0] = (float) -(Math.cos(crossSection.facingAzimuth) * crossSection.measurements[0]);
			result[2] = (float) -(Math.sin(crossSection.facingAzimuth) * crossSection.measurements[0]);
			// Right
			result[3] = (float) (Math.cos(crossSection.facingAzimuth) * crossSection.measurements[1]);
			result[5] = (float) (Math.sin(crossSection.facingAzimuth) * crossSection.measurements[1]);
			result[7] = (float) crossSection.measurements[2]; // Up
			result[10] = (float) -crossSection.measurements[3]; // Down
			break;
		case NSEW:
			result[2] = (float) -crossSection.measurements[0]; // North
			result[5] = (float) crossSection.measurements[1]; // South
			result[6] = (float) crossSection.measurements[2]; // East
			result[9] = (float) -crossSection.measurements[3]; // West
			break;
		}

		return result;
	}

	/**
	 * After {@link #linkCrossSections(CalcProject)} is done, all shots should
	 * have cross sections at the from and to stations, so we're ready to create
	 * normals from the from/to stations to the cross sections' splay points.
	 */
	static void calculateSplayNormals(CalcProject project) {
		for (Map.Entry<ShotKey, CalcShot> entry : project.shots.entrySet()) {
			CalcShot shot = entry.getValue();
			CalcCrossSection fromCrossSection = shot.fromStation.crossSections.get(shot.toStation.key());
			CalcCrossSection toCrossSection = shot.toStation.crossSections.get(shot.fromStation.key());
			shot.fromSplayNormals = createSplayNormals(fromCrossSection);
			shot.toSplayNormals = createSplayNormals(toCrossSection);
		}
	}

	/**
	 * Calculates the splay points (right now just the LRUD points) for all
	 * shots from the station positions (from
	 * {@link #calculateStationPositions(CalcProject)} and the splay normals
	 * (from {@link #calculateSplayNormals(CalcProject)}).
	 */
	static void calculateSplayPoints(CalcProject project) {
		for (CalcShot shot : project.shots.values()) {
			shot.fromSplayPoints = new float[shot.fromSplayNormals.length];
			for (int i = 0; i < shot.fromSplayNormals.length; i += 3) {
				shot.fromSplayPoints[i] = (float) (shot.fromStation.position[0] + shot.fromSplayNormals[i]);
				shot.fromSplayPoints[i + 1] = (float) (shot.fromStation.position[1] + shot.fromSplayNormals[i + 1]);
				shot.fromSplayPoints[i + 2] = (float) (shot.fromStation.position[2] + shot.fromSplayNormals[i + 2]);
			}
			shot.toSplayPoints = new float[shot.toSplayNormals.length];
			for (int i = 0; i < shot.toSplayNormals.length; i += 3) {
				shot.toSplayPoints[i] = (float) (shot.toStation.position[0] + shot.toSplayNormals[i]);
				shot.toSplayPoints[i + 1] = (float) (shot.toStation.position[1] + shot.toSplayNormals[i + 1]);
				shot.toSplayPoints[i + 2] = (float) (shot.toStation.position[2] + shot.toSplayNormals[i + 2]);
			}
		}
	}

	/**
	 * Calculates the position of all the stations that aren't already fixed.
	 * Right now this is a half-assed algorithm that doesn't do any loop closure
	 * at all, it just naively computes the position of one station to the next
	 * based upon the shot measurements. It uses Dijkstra's algorithm to
	 * traverse through the stations from nearest to a fixed station to farthest
	 * from a fixed station, so at least it's deterministic and unaffected by
	 * the shot/station ordering.
	 */
	static void calculateStationPositions(CalcProject project) {
		Graphs.traverseEdges(
				project.shots.values().stream().filter(
						shot -> shot.fromStation.hasPosition() || shot.toStation.hasPosition()),
				shot -> shot.distance,
				shot -> {
					calculateStationPositions(shot);
					return Stream.concat(
							shot.fromStation.shots.values().stream(),
							shot.toStation.shots.values().stream());
				},
				() -> true);
	}

	static void calculateStationPositions(CalcShot shot) {
		double xOffs = shot.distance * Math.cos(shot.inclination) * Math.sin(shot.azimuth);
		double yOffs = shot.distance * Math.sin(shot.inclination);
		double zOffs = shot.distance * Math.cos(shot.inclination) * -Math.cos(shot.azimuth);
		if (shot.fromStation.hasPosition() && !shot.toStation.hasPosition()) {
			shot.toStation.position[0] = shot.fromStation.position[0] + xOffs;
			shot.toStation.position[1] = shot.fromStation.position[1] + yOffs;
			shot.toStation.position[2] = shot.fromStation.position[2] + zOffs;
		} else if (!shot.fromStation.hasPosition() && shot.toStation.hasPosition()) {
			shot.fromStation.position[0] = shot.toStation.position[0] - xOffs;
			shot.fromStation.position[1] = shot.toStation.position[1] - yOffs;
			shot.fromStation.position[2] = shot.toStation.position[2] - zOffs;
		}
	}
}
