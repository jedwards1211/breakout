package org.breakout.model;

import java.util.HashMap;
import java.util.Map;

public class CalcProject {
	public final Map<ShotKey, CalcShot> shots = new HashMap<>();
	public final Map<StationKey, CalcStation> stations = new HashMap<>();
}
