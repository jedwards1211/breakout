package org.breakout.model;

import java.util.Map;

public class CalculateSplayNormals {
	public static void calculateSplayNormals(Map<ShotKey, ParsedRow> parsed, Map<ShotKey, CalcShot> calc) {
		for (Map.Entry<ShotKey, CalcShot> entry : calc.entrySet()) {
			CalcShot shot = entry.getValue();
			shot.fromSplayNormals = new float[] { 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, -1f, 0f };
			shot.toSplayNormals = new float[] { 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, -1f, 0f };
			// TODO
		}
	}
}
