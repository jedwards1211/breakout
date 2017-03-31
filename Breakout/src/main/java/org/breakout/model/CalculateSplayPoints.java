package org.breakout.model;

import java.util.Collection;

public class CalculateSplayPoints {
	public static void calculateSplayPoints(Collection<CalcShot> shots) {
		for (CalcShot shot : shots) {
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
}
