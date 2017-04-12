package org.breakout.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

import org.andork.collect.PriorityEntry;
import org.andork.math.misc.Angles;
import org.andork.util.Iterables;

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

	static double averageAzimuth(CalcShot shot1, CalcStation station, CalcShot shot2) {
		boolean shotsFaceOppositeDirections = station == shot1.toStation != (station == shot2.fromStation);
		return Angles.average(
				shot1.azimuth,
				shotsFaceOppositeDirections ? Angles.opposite(shot2.azimuth) : shot2.azimuth);
	}

	/**
	 * If the cross sections of {@code shot1} and/or {@code shot2} at
	 * {@code station} are missing, sets them, and ensures that they are aligned
	 * and bisecting the shot angles (unless one station or the other had a
	 * fixed {@link CalcCrossSection#facingAzimuth}).
	 */
	static void linkCrossSections(CalcShot shot1, CalcStation station, CalcShot shot2) {
		boolean opposing = station == shot1.toStation != (station == shot2.fromStation);

		CalcCrossSection section1 = shot1.getCrossSectionAt(station);
		CalcCrossSection section2 = shot2.getCrossSectionAt(station);
		if (section1 != null && section2 != null) {
			if (Double.isNaN(section1.facingAzimuth)) {
				if (Double.isNaN(section2.facingAzimuth)) {
					section1.facingAzimuth = averageAzimuth(shot1, station, shot2);
					section2.facingAzimuth = opposing
							? Angles.opposite(section1.facingAzimuth)
							: section1.facingAzimuth;
				} else {
					section1.facingAzimuth = opposing
							? Angles.opposite(section2.facingAzimuth)
							: section2.facingAzimuth;
				}
			} else {
				section2.facingAzimuth = opposing
						? Angles.opposite(section1.facingAzimuth)
						: section1.facingAzimuth;
			}
			return;
		}

		if (section1 == null && section2 == null) {
			section1 = shot1.getCrossSectionAt(shot1.otherStation(station));
			section2 = shot2.getCrossSectionAt(shot2.otherStation(station));
			if (section1 != null && section2 != null && section1.type == section2.type) {
				CalcCrossSection average = new CalcCrossSection();
				average.type = section1.type;
				switch (section1.type) {
				case LRUD:
					average.measurements = new double[4];
					if (opposing) {
						average.measurements[0] = (section1.measurements[0] + section2.measurements[1]) * 0.5;
						average.measurements[1] = (section1.measurements[1] + section2.measurements[0]) * 0.5;
					} else {
						average.measurements[0] = (section1.measurements[0] + section2.measurements[0]) * 0.5;
						average.measurements[1] = (section1.measurements[1] + section2.measurements[1]) * 0.5;
					}
					average.measurements[2] = (section1.measurements[2] + section2.measurements[2]) * 0.5;
					average.measurements[3] = (section1.measurements[3] + section2.measurements[3]) * 0.5;
					break;
				case NSEW:
					average.measurements = new double[4];
					for (int i = 0; i < 4; i++) {
						average.measurements[i] = (section1.measurements[i] + section2.measurements[i]) * 0.5;
					}
					break;
				}
				section1 = average;
				section2 = null;
			} else if (section1 == null && section2 == null) {
				section1 = new CalcCrossSection();
				section1.measurements = new double[4];
				Arrays.fill(section1.measurements, 0.05f);
				section1.type = CrossSectionType.LRUD;
			} else {
				if (section1 != null) {
					section1 = section1.clone();
					section1.facingAzimuth = Double.NaN;
				}
				if (section2 != null) {
					section2 = section2.clone();
					section2.facingAzimuth = Double.NaN;
				}
			}
		}
		if (section1 != null && section2 == null) {
			if (Double.isNaN(section1.facingAzimuth)) {
				section1.facingAzimuth = averageAzimuth(shot1, station, shot2);
			}
			section2 = opposing ? section1.rotateLRUDs180Degrees() : section1.clone();
		} else {
			if (Double.isNaN(section2.facingAzimuth)) {
				section2.facingAzimuth = averageAzimuth(shot2, station, shot1);
			}
			section1 = opposing ? section2.rotateLRUDs180Degrees() : section2.clone();
		}
		shot1.setCrossSectionAt(station, section1);
		shot2.setCrossSectionAt(station, section2);
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
		for (CalcShot shot : project.shots.values()) {
			if (prevShot != null) {
				if (prevShot.toStation == shot.fromStation) {
					linkCrossSections(prevShot, shot.fromStation, shot);
				} else if (prevShot.fromStation == shot.toStation) {
					linkCrossSections(prevShot, shot.toStation, shot);
				}
			}
			prevShot = shot;
		}

		for (CalcStation station : project.stations.values()) {
			if (station.shots.size() == 2) {
				Iterator<CalcShot> i = station.shots.values().iterator();
				CalcShot shot1 = i.next();
				CalcShot shot2 = i.next();
				linkCrossSections(shot1, station, shot2);
			}
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
				shot.fromCrossSection = toCrossSection.clone();
				shot.fromCrossSection.facingAzimuth = shot.azimuth;
			} else if (toCrossSection == null && fromCrossSection != null) {
				shot.toCrossSection = fromCrossSection.clone();
				shot.toCrossSection.facingAzimuth = shot.azimuth;
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
					indices[k++] = (i + 1) % fromVertexCount;
					indices[k++] = fromVertexCount;
				}
			} else if (fromVertexCount == 1) {
				int[] indices = shot.indices = new int[toVertexCount * 3];
				int k = 0;
				for (int i = 0; i < toVertexCount; i++) {
					indices[k++] = i + 1;
					indices[k++] = (i + 1) % toVertexCount + 1;
					indices[k++] = 0;
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
					indices[k++] = (i + 1) % fromVertexCount;
					indices[k++] = i + fromVertexCount;
					indices[k++] = fromVertexCount + (i + 1) % toVertexCount;
					indices[k++] = i + fromVertexCount;
					indices[k++] = (i + 1) % fromVertexCount;
				}
				if (shot.fromStation.isDeadEnd()) {
					for (int i = 2; i < fromVertexCount; i++) {
						indices[k++] = 0;
						indices[k++] = i;
						indices[k++] = i - 1;
					}
				}
				if (shot.toStation.isDeadEnd()) {
					for (int i = 2; i < toVertexCount; i++) {
						indices[k++] = fromVertexCount;
						indices[k++] = fromVertexCount + i;
						indices[k++] = fromVertexCount + i - 1;
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
		PriorityQueue<PriorityEntry<Double, CalcShot>> queue = new PriorityQueue<>();
		for (CalcShot shot : project.shots.values()) {
			if (shot.fromStation.hasPosition() || shot.toStation.hasPosition()) {
				queue.add(new PriorityEntry<>(0.0, shot));
			}
		}
		while (!queue.isEmpty()) {
			PriorityEntry<Double, CalcShot> entry = queue.poll();
			double distance = entry.getKey();
			CalcShot shot = entry.getValue();
			calculateStationPositions(shot);
			for (CalcShot nextShot : Iterables.concat(
					shot.fromStation.shots.values(),
					shot.toStation.shots.values())) {
				if (!nextShot.fromStation.hasPosition() || !nextShot.toStation.hasPosition()) {
					queue.add(new PriorityEntry<>(distance + shot.distance, nextShot));
				}
			}
		}
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
