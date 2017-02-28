package org.breakout.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CalcStation {
	public String name;
	public String cave;

	public final double[] position = new double[3];

	public final Map<StationKey, CalcShot> shots = new LinkedHashMap<>();
	public final Map<StationKey, CalcCrossSection> crossSections = new HashMap<>();

	public StationKey key() {
		return new StationKey(cave, name);
	}
}
