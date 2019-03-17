package org.breakout.model.calc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

import org.andork.collect.Iterables;
import org.andork.collect.PriorityEntry;
import org.andork.math.misc.Angles;
import org.breakout.model.CrossSectionType;
import org.breakout.model.ShotKey;

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

	static boolean shotsFaceOppositeDirections(CalcShot shot1, CalcShot shot2) {
		return shot1.fromStation == shot2.fromStation ||
				shot1.toStation == shot2.toStation;
	}

	static double averageAzimuth(CalcShot shot1, CalcShot shot2) {
		return Angles.average(
				shot1.azimuth,
				shotsFaceOppositeDirections(shot1, shot2) ? Angles.opposite(shot2.azimuth) : shot2.azimuth);
	}

	static double hemisphereAzimuth(double azimuth) {
		azimuth = Angles.positive(azimuth);
		return azimuth > Math.PI ? azimuth - Math.PI : azimuth;
	}

	static double hemisphereAzimuth(CalcShot shot) {
		return hemisphereAzimuth(shot.azimuth);
	}

	static double hemisphereAzimuth(CalcCrossSection section) {
		return hemisphereAzimuth(section.facingAzimuth);
	}

	static double MAX_CROSS_SECTION_ALIGNMENT_DIFFERENCE = Math.PI / 4;

	static CalcCrossSection findAlignedCrossSection(CalcShot shot, CalcStation station) {
		double bestDifference = MAX_CROSS_SECTION_ALIGNMENT_DIFFERENCE;
		CalcCrossSection alignedCrossSection = null;
		for (CalcShot otherShot : station.shots.values()) {
			CalcCrossSection otherCrossSection = otherShot.getCrossSectionAt(station);
			if (otherCrossSection == null || otherCrossSection.type != CrossSectionType.LRUD) {
				continue;
			}
			double difference = Math.min(
					Angles.difference(shot.azimuth, otherCrossSection.facingAzimuth),
					Angles.difference(Angles.opposite(shot.azimuth), otherCrossSection.facingAzimuth));
			if (difference <= bestDifference) {
				bestDifference = difference;
				alignedCrossSection = Angles.difference(shot.azimuth,
						otherCrossSection.facingAzimuth) > MAX_CROSS_SECTION_ALIGNMENT_DIFFERENCE
								? otherCrossSection.rotateLRUDs180Degrees()
								: otherCrossSection.clone();
			}
		}
		return alignedCrossSection;
	}

	static void cloneCrossSectionFromOtherStation(CalcShot shot, CalcStation station) {
		CalcCrossSection crossSection = shot.getCrossSectionAt(shot.otherStation(station));
		if (crossSection == null) {
			return;
		}
		crossSection = crossSection.clone();
		crossSection.facingAzimuth = shot.azimuth;
		for (CalcShot otherShot : station.shots.values()) {
			if (otherShot == shot) {
				continue;
			}
			CalcCrossSection otherCrossSection = otherShot.getCrossSectionAt(station);
			if (otherCrossSection == null) {
				continue;
			}
			if (otherCrossSection.type != crossSection.type) {
				continue;
			}
			switch (crossSection.type) {
			case LRUD:
				// make sure up/down of cloned cross section are within max up/down
				// of other cross sections at this station
				crossSection.measurements[2] = Math.min(crossSection.measurements[2],
						otherCrossSection.measurements[2]);
				crossSection.measurements[3] = Math.min(crossSection.measurements[3],
						otherCrossSection.measurements[3]);
				break;
			case NSEW:
				// make sure NSEW of cloned cross section are within max NSEW
				// of other cross sections at this station
				for (int i = 0; i < 4; i++) {
					crossSection.measurements[i] = Math.min(crossSection.measurements[i],
							otherCrossSection.measurements[i]);
				}
				break;
			}
		}
		shot.setCrossSectionAt(station, crossSection);
	}

	/**
	 * If the cross sections of {@code shot1} and/or {@code shot2} at
	 * {@code station} are missing, sets them, and ensures that they are aligned
	 * and bisecting the shot angles (unless one station or the other had a
	 * fixed {@link CalcCrossSection#facingAzimuth}).
	 */
	static void linkCrossSections(CalcShot shot1, CalcStation station, CalcShot shot2) {
		boolean shotsFaceOppositeDirections = shotsFaceOppositeDirections(shot1, shot2);
		
		CalcCrossSection section1 = shot1.getCrossSectionAt(station);
		CalcCrossSection section2 = shot2.getCrossSectionAt(station);
		if (section1 != null && section2 != null) {
			if (Double.isNaN(section1.facingAzimuth)) {
				if (Double.isNaN(section2.facingAzimuth)) {
					section1.facingAzimuth = averageAzimuth(shot1, shot2);
					section2.facingAzimuth = shotsFaceOppositeDirections
							? Angles.opposite(section1.facingAzimuth)
							: section1.facingAzimuth;
				} else {
					section1.facingAzimuth = shotsFaceOppositeDirections
							? Angles.opposite(section2.facingAzimuth)
							: section2.facingAzimuth;
				}
			} else if (Double.isNaN(section2.facingAzimuth)) {
				section2.facingAzimuth = shotsFaceOppositeDirections
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
					if (shotsFaceOppositeDirections) {
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
				section1.facingAzimuth = averageAzimuth(shot1, shot2);
			}
			section2 = shotsFaceOppositeDirections ? section1.rotateLRUDs180Degrees() : section1.clone();
		} else {
			if (Double.isNaN(section2.facingAzimuth)) {
				section2.facingAzimuth = averageAzimuth(shot2, shot1);
			}
			section1 = shotsFaceOppositeDirections ? section2.rotateLRUDs180Degrees() : section2.clone();
		}
		shot1.setCrossSectionAt(station, section1);
		shot2.setCrossSectionAt(station, section2);
	}

	static void linkCrossSections(CalcStation station) {
		if (station.shots.size() == 2) {
			Iterator<CalcShot> i = station.shots.values().iterator();
			CalcShot shot1 = i.next();
			CalcShot shot2 = i.next();
			linkCrossSections(shot1, station, shot2);
			return;
		}
		for (CalcShot shot : station.shots.values()) {
			CalcCrossSection crossSection = shot.getCrossSectionAt(station);
			if (crossSection != null) {
				if (Double.isNaN(crossSection.facingAzimuth)) {
					crossSection.facingAzimuth = shot.azimuth;
				}
				continue;
			}

			CalcCrossSection alignedCrossSection = findAlignedCrossSection(shot, station);

			if (alignedCrossSection != null) {
				shot.setCrossSectionAt(station, alignedCrossSection);
			} else {
				cloneCrossSectionFromOtherStation(shot, station);
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
			linkCrossSections(station);
		}
		
		for (CalcShot shot : project.shots.values()) {
			if (shot.fromCrossSection == null && !shot.fromStation.isDeadEnd()) {
				shot.fromCrossSection = new CalcCrossSection(
					CrossSectionType.LRUD,
					new double[] { 0.05, 0.05, 0.05, 0.05 },
					shot.azimuth);
				getMinUpDown(shot, shot.fromStation, shot.fromCrossSection);
			}
			if (shot.toCrossSection == null && !shot.toStation.isDeadEnd()) {
				shot.toCrossSection = new CalcCrossSection(
					CrossSectionType.LRUD,
					new double[] { 0.05, 0.05, 0.05, 0.05 },
					shot.azimuth);
				getMinUpDown(shot, shot.toStation, shot.toCrossSection);
			}
		}
	}

	private static void getMinUpDown(CalcShot shot, CalcStation station, CalcCrossSection out) {
		for (CalcShot otherShot : station.shots.values()) {
			if (otherShot == shot) continue;
			CalcCrossSection otherCrossSection = otherShot.getCrossSectionAt(station);
			if (otherCrossSection == null || otherCrossSection.type != out.type) continue;
			for (int i = 2; i < 4; i++) {
				if (Double.isNaN(out.measurements[i]) ||
					otherCrossSection.measurements[i] < out.measurements[i]) {
					out.measurements[i] = otherCrossSection.measurements[i];
				}
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

	static void createNormals(CalcCrossSection crossSection, float[] normals, int startIndex, boolean flipLR) {
		if (crossSection == null) {
			return;
		}

		// remember:
		// x axis (for coordinates at indices [0, 3, 6, 9]) points east
		// y axis (for coordinates at indices [1, 4, 7, 10]) points up
		// z axis (for coordinates at indices [2, 5, 8, 11]) points **south**
		
		int negateIfFlipped = flipLR ? -1 : 1;

		switch (crossSection.type) {
		case LRUD:
			// Up
			normals[startIndex + 1] = 1;
			// Right
			normals[startIndex + 3] = (float) Math.cos(crossSection.facingAzimuth) * negateIfFlipped;
			normals[startIndex + 5] = (float) Math.sin(crossSection.facingAzimuth) * negateIfFlipped;
			// Down
			normals[startIndex + 7] = -1;
			// Left
			normals[startIndex + 9] = (float) -Math.cos(crossSection.facingAzimuth) * negateIfFlipped;
			normals[startIndex + 11] = (float) -Math.sin(crossSection.facingAzimuth) * negateIfFlipped;
			break;
		case NSEW:
			normals[startIndex + 2] = (float) -crossSection.measurements[0]; // North
			normals[startIndex + 3] = (float) crossSection.measurements[2]; // East
			normals[startIndex + 8] = (float) crossSection.measurements[1]; // South
			normals[startIndex + 9] = (float) -crossSection.measurements[3]; // West
			break;
		}
	}

	static void createVertices(CalcStation station, CalcCrossSection crossSection, float[] vertices, int startIndex, boolean flipLR) {
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
		
		float rightX = (float) (x + Math.cos(crossSection.facingAzimuth) * crossSection.measurements[1]);
		float rightZ = (float) (z + Math.sin(crossSection.facingAzimuth) * crossSection.measurements[1]);
		float leftX = (float) (x - Math.cos(crossSection.facingAzimuth) * crossSection.measurements[0]);
		float leftZ = (float) (z - Math.sin(crossSection.facingAzimuth) * crossSection.measurements[0]);

		switch (crossSection.type) {
		case LRUD:
			// Up
			vertices[startIndex++] = (float) x;
			vertices[startIndex++] = (float) (y + crossSection.measurements[2]); // Up
			vertices[startIndex++] = (float) z;
			// Right
			vertices[startIndex++] = flipLR ? leftX : rightX;
			vertices[startIndex++] = (float) y;
			vertices[startIndex++] = flipLR ? leftZ : rightZ;
			// Down
			vertices[startIndex++] = (float) x;
			vertices[startIndex++] = (float) (y - crossSection.measurements[3]); // Down
			vertices[startIndex++] = (float) z;
			// Left
			vertices[startIndex++] = flipLR ? rightX : leftX; 
			vertices[startIndex++] = (float) y;
			vertices[startIndex++] = flipLR ? rightZ : leftZ; 
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

	static float orientation(float[] vertices, int i0, int i1, int i2, int i3) {
		float adx = vertices[i0] - vertices[i3];
		float bdx = vertices[i1] - vertices[i3];
		float cdx = vertices[i2] - vertices[i3];
		float ady = vertices[i0 + 1] - vertices[i3 + 1];
		float bdy = vertices[i1 + 1] - vertices[i3 + 1];
		float cdy = vertices[i2 + 1] - vertices[i3 + 1];
		float adz = vertices[i0 + 2] - vertices[i3 + 2];
		float bdz = vertices[i1 + 2] - vertices[i3 + 2];
		float cdz = vertices[i2 + 2] - vertices[i3 + 2];
		float bdxcdy = bdx * cdy;
		float cdxbdy = cdx * bdy;
		float cdxady = cdx * ady;
		float adxcdy = adx * cdy;
		float adxbdy = adx * bdy;
		float bdxady = bdx * ady;
		return adz * (bdxcdy - cdxbdy)
				+ bdz * (cdxady - adxcdy)
				+ cdz * (adxbdy - bdxady);
	}

	/**
	 * Calculates the vertices (right now just the LRUD points) for all shots
	 * from the station positions (from
	 * {@link #calculateStationPositions(CalcProject)} and cross sections.
	 */
	static void calculateVertices(CalcProject project) {
		for (Map.Entry<ShotKey, CalcShot> entry : project.shots.entrySet()) {
			CalcShot shot = entry.getValue();
			calculateVertices(shot);
		}
	}

	static void calculateVertices(CalcShot shot) {
		int fromVertexCount = getVertexCount(shot.fromCrossSection);
		int toVertexCount = getVertexCount(shot.toCrossSection);
		boolean flipLR = shot.fromCrossSection != null &&
			shot.toCrossSection != null &&
			Angles.difference(shot.fromCrossSection.facingAzimuth, shot.toCrossSection.facingAzimuth) > Math.PI / 2;
		shot.normals = new float[(fromVertexCount + toVertexCount) * 3];
		createNormals(shot.fromCrossSection, shot.normals, 0, flipLR);
		createNormals(shot.toCrossSection, shot.normals, fromVertexCount * 3, false);
		shot.vertices = new float[(fromVertexCount + toVertexCount) * 3];
		createVertices(shot.fromStation, shot.fromCrossSection, shot.vertices, 0, flipLR);
		createVertices(shot.toStation, shot.toCrossSection, shot.vertices, fromVertexCount * 3, false);
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
				int i1 = (i + 1) % fromVertexCount;
				int i2 = i + fromVertexCount;
				int i3 = fromVertexCount + (i + 1) % toVertexCount;
				if (orientation(shot.vertices, i * 3, i1 * 3, i2 * 3, i3 * 3) > 0) {
					indices[k++] = i;
					indices[k++] = i1;
					indices[k++] = i2;
					indices[k++] = i3;
					indices[k++] = i2;
					indices[k++] = i1;
				} else {
					indices[k++] = i;
					indices[k++] = i1;
					indices[k++] = i3;
					indices[k++] = i3;
					indices[k++] = i2;
					indices[k++] = i;
				}
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

	/**
	 * Calculates the position of all the stations that aren't already fixed.
	 * Right now this is a half-assed algorithm that doesn't do any loop closure
	 * at all, it just naively computes the position of one station to the next
	 * based upon the shot measurements. It uses Dijkstra's algorithm to
	 * traverse through the stations from nearest to a fixed station to farthest
	 * from a fixed station, so at least it's deterministic and mostly unaffected by
	 * the shot/station ordering.
	 */
	static void calculateStationPositions(CalcProject project) {
		PriorityQueue<PriorityEntry<Double, CalcShot>> queue = new PriorityQueue<>();
		for (CalcShot shot : project.shots.values()) {
			if (shot.fromStation.hasPosition() || shot.toStation.hasPosition()) {
				queue.add(new PriorityEntry<>(0.0, shot));
			}
		}
		if (queue.isEmpty()) {
			for (CalcShot shot : project.shots.values()) {
				Arrays.fill(shot.fromStation.position, 0);
				queue.add(new PriorityEntry<>(0.0, shot));
				break;
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
				if ((!nextShot.fromStation.hasPosition() || !nextShot.toStation.hasPosition()) && Double.isFinite(shot.distance)) {
					queue.add(new PriorityEntry<>(distance + shot.distance, nextShot));
				}
			}
		}
	}

	static void calculateStationPositions(CalcShot shot) {
		if (!Double.isFinite(shot.distance) ||
				!Double.isFinite(shot.azimuth) ||
				!Double.isFinite(shot.inclination)) {
			return;
		}
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
