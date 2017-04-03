package org.breakout.model;

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
		calculateVertices(project);
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
			CalcCrossSection fromCrossSection = shot.fromCrossSection;
			CalcCrossSection toCrossSection = shot.toCrossSection;
			if (fromCrossSection != null) {
				if (prevShot != null && prevShot.toStation == shot.fromStation) {
					prevShot.toCrossSection = fromCrossSection;
					joinedCrossSections.add(fromCrossSection);
					fromCrossSection.facingAzimuth = AngleUtils.average(prevShot.azimuth, shot.azimuth);
				} else {
					for (CalcShot other : shot.fromStation.shots.values()) {
						if (other == shot) {
							continue;
						}
						if (other.toCrossSection == null) {
							other.toCrossSection = fromCrossSection;
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
					CalcCrossSection otherCrossSection = otherShot.toCrossSection;
					if (otherCrossSection != null && !joinedCrossSections.contains(otherCrossSection)) {
						lastShot = otherShot;
						fromCrossSection = otherCrossSection;
					}
				}
				if (lastShot != null && fromCrossSection != null) {
					shot.fromCrossSection = fromCrossSection;
					if (joinedCrossSections.add(fromCrossSection)) {
						fromCrossSection.facingAzimuth = AngleUtils.average(lastShot.azimuth, shot.azimuth);
					}
				}
			}
			if (prevShot != null && toCrossSection == null && prevShot.fromStation == shot.toStation) {
				toCrossSection = prevShot.fromCrossSection;
				if (toCrossSection != null) {
					shot.toCrossSection = toCrossSection;
					if (joinedCrossSections.add(toCrossSection)) {
						toCrossSection.facingAzimuth = AngleUtils.average(shot.azimuth, prevShot.azimuth);
					}
				}
			}
			prevShot = shot;
		}

		for (CalcShot shot : project.shots.values()) {
			CalcCrossSection fromCrossSection = shot.fromCrossSection;
			CalcCrossSection toCrossSection = shot.toCrossSection;
			// TODO this shouldn't blindly copy the cross section. It should
			// pick an appropriate facing azimuth based upon what else is at
			// the junction, and adjust the cross section to keep uniform
			// passage size with that new facing azimuth. E.g. NT46-NTW1
			// in Fisher Ridge
			if (fromCrossSection == null && toCrossSection != null) {
				shot.fromCrossSection = toCrossSection;
			} else if (toCrossSection == null && fromCrossSection != null) {
				shot.toCrossSection = fromCrossSection;
			}
		}
	}

	static int getVertexCount(CalcCrossSection crossSection) {
		if (crossSection == null) {
			return 1;
		}

		switch (crossSection.type) {
		case LRUD:
		case NSEW:
			return 4;
		}
		return 1;
	}

	static void createNormals(CalcCrossSection crossSection, float[] normals, int startIndex) {
		if (crossSection == null) {
			return;
		}

		// remember:
		// x axis (for coordinates at indices [0, 3, 6, 9]) points east
		// y axis (for coordinates at indices [1, 4, 7, 10]) points up
		// z axis (for coordinates at indices [2, 5, 8, 11]) points **south**

		switch (crossSection.type) {
		case LRUD:
			// Up
			normals[startIndex + 1] = 1;
			// Right
			normals[startIndex + 3] = (float) Math.cos(crossSection.facingAzimuth);
			normals[startIndex + 5] = (float) Math.sin(crossSection.facingAzimuth);
			// Down
			normals[startIndex + 7] = -1;
			// Left
			normals[startIndex + 9] = (float) -Math.cos(crossSection.facingAzimuth);
			normals[startIndex + 11] = (float) -Math.sin(crossSection.facingAzimuth);
			break;
		case NSEW:
			normals[startIndex + 2] = (float) -crossSection.measurements[0]; // North
			normals[startIndex + 3] = (float) crossSection.measurements[2]; // East
			normals[startIndex + 8] = (float) crossSection.measurements[1]; // South
			normals[startIndex + 9] = (float) -crossSection.measurements[3]; // West
			break;
		}
	}

	static void createVertices(CalcStation station, CalcCrossSection crossSection, float[] vertices, int startIndex) {
		double x = station.position[0];
		double y = station.position[1];
		double z = station.position[2];

		if (crossSection == null) {
			vertices[startIndex++] = (float) x;
			vertices[startIndex++] = (float) y;
			vertices[startIndex++] = (float) z;
			return;
		}

		// remember:
		// x axis (for coordinates at indices [0, 3, 6, 9]) points east
		// y axis (for coordinates at indices [1, 4, 7, 10]) points up
		// z axis (for coordinates at indices [2, 5, 8, 11]) points **south**

		switch (crossSection.type) {
		case LRUD:
			// Up
			vertices[startIndex++] = (float) x;
			vertices[startIndex++] = (float) (y + crossSection.measurements[2]); // Up
			vertices[startIndex++] = (float) z;
			// Right
			vertices[startIndex++] = (float) (x + Math.cos(crossSection.facingAzimuth) * crossSection.measurements[1]);
			vertices[startIndex++] = (float) y;
			vertices[startIndex++] = (float) (z + Math.sin(crossSection.facingAzimuth) * crossSection.measurements[1]);
			// Down
			vertices[startIndex++] = (float) x;
			vertices[startIndex++] = (float) (y - crossSection.measurements[3]); // Down
			vertices[startIndex++] = (float) z;
			// Left
			vertices[startIndex++] = (float) (x - Math.cos(crossSection.facingAzimuth) * crossSection.measurements[0]);
			vertices[startIndex++] = (float) y;
			vertices[startIndex++] = (float) (z - Math.sin(crossSection.facingAzimuth) * crossSection.measurements[0]);
			break;
		case NSEW:
			// North
			vertices[startIndex++] = (float) x;
			vertices[startIndex++] = (float) y;
			vertices[startIndex++] = (float) (z - crossSection.measurements[0]);
			// East
			vertices[startIndex++] = (float) (x + crossSection.measurements[2]);
			vertices[startIndex++] = (float) y;
			vertices[startIndex++] = (float) z;
			// South
			vertices[startIndex++] = (float) x;
			vertices[startIndex++] = (float) y;
			vertices[startIndex++] = (float) crossSection.measurements[1];
			// West
			vertices[startIndex++] = (float) (x - crossSection.measurements[3]);
			vertices[startIndex++] = (float) y;
			vertices[startIndex++] = (float) z;
			break;
		}
	}

	/**
	 * Calculates the vertices (right now just the LRUD points) for all shots
	 * from the station positions (from
	 * {@link #calculateStationPositions(CalcProject)} and cross sections.
	 */
	static void calculateVertices(CalcProject project) {
		for (Map.Entry<ShotKey, CalcShot> entry : project.shots.entrySet()) {
			CalcShot shot = entry.getValue();
			int fromVertexCount = getVertexCount(shot.fromCrossSection);
			int toVertexCount = getVertexCount(shot.toCrossSection);
			shot.normals = new float[(fromVertexCount + toVertexCount) * 3];
			createNormals(shot.fromCrossSection, shot.normals, 0);
			createNormals(shot.toCrossSection, shot.normals, fromVertexCount * 3);
			shot.vertices = new float[(fromVertexCount + toVertexCount) * 3];
			createVertices(shot.fromStation, shot.fromCrossSection, shot.vertices, 0);
			createVertices(shot.toStation, shot.toCrossSection, shot.vertices, fromVertexCount * 3);
			shot.polarities = new float[fromVertexCount + toVertexCount];
			for (int i = fromVertexCount; i < shot.polarities.length; i++) {
				shot.polarities[i] = 1;
			}
			if (fromVertexCount == 1 && toVertexCount == 1) {
				shot.indices = new int[0];
			} else if (toVertexCount == 1) {
				int[] indices = shot.indices = new int[fromVertexCount * 3];
				int k = 0;
				for (int i = 0; i < fromVertexCount; i++) {
					indices[k++] = i;
					indices[k++] = fromVertexCount;
					indices[k++] = (i + 1) % fromVertexCount;
				}
			} else if (fromVertexCount == 1) {
				int[] indices = shot.indices = new int[toVertexCount * 3];
				int k = 0;
				for (int i = 0; i < toVertexCount; i++) {
					indices[k++] = i + 1;
					indices[k++] = 0;
					indices[k++] = (i + 1) % toVertexCount + 1;
				}
			} else if (fromVertexCount == toVertexCount) {
				int triangleCount = fromVertexCount + toVertexCount;
				if (shot.fromStation.isDeadEnd()) {
					triangleCount += fromVertexCount - 2;
				}
				if (shot.toStation.isDeadEnd()) {
					triangleCount += toVertexCount - 2;
				}
				int[] indices = shot.indices = new int[triangleCount * 3];
				int k = 0;
				for (int i = 0; i < fromVertexCount; i++) {
					indices[k++] = i;
					indices[k++] = i + fromVertexCount;
					indices[k++] = (i + 1) % fromVertexCount;
					indices[k++] = fromVertexCount + (i + 1) % toVertexCount;
					indices[k++] = (i + 1) % fromVertexCount;
					indices[k++] = i + fromVertexCount;
				}
				if (shot.fromStation.isDeadEnd()) {
					for (int i = 2; i < fromVertexCount; i++) {
						indices[k++] = 0;
						indices[k++] = i - 1;
						indices[k++] = i;
					}
				}
				if (shot.toStation.isDeadEnd()) {
					for (int i = 2; i < toVertexCount; i++) {
						indices[k++] = fromVertexCount;
						indices[k++] = fromVertexCount + i - 1;
						indices[k++] = fromVertexCount + i;
					}
				}
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
