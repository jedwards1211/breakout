package org.breakout.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class CalcStation {
	public String name;
	public String cave;

	public final double[] position = { Double.NaN, Double.NaN, Double.NaN };

	public final Map<StationKey, CalcShot> shots = new LinkedHashMap<>();

	public StationKey key() {
		return new StationKey(cave, name);
	}

	public boolean hasPosition() {
		for (int i = 0; i < 3; i++) {
			if (!Double.isFinite(position[i])) {
				return false;
			}
		}
		return true;
	}
}
