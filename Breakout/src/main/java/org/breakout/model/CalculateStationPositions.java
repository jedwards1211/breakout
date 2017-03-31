package org.breakout.model;

import java.util.stream.Stream;

import org.andork.graph.Graphs;

public class CalculateStationPositions {
	public static void calculateStationPositions(Stream<CalcShot> shots) {
		Graphs.traverseEdges(
				shots.filter(
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

	private static void calculateStationPositions(CalcShot shot) {
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
