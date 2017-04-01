package org.breakout.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class CalcProject {
	public final Map<ShotKey, CalcShot> shots = new LinkedHashMap<>();
	public final Map<StationKey, CalcStation> stations = new LinkedHashMap<>();
}
