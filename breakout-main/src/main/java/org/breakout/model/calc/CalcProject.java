package org.breakout.model.calc;

import java.util.LinkedHashMap;
import java.util.Map;

import org.breakout.model.ShotKey;
import org.breakout.model.StationKey;

public class CalcProject {
	public final Map<ShotKey, CalcShot> shots = new LinkedHashMap<>();
	public final Map<StationKey, CalcStation> stations = new LinkedHashMap<>();
}
