package org.breakout.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalcProject {
	public final List<CalcShot> rows = new ArrayList<>();
	public final Map<ShotKey, CalcShot> shots = new HashMap<>();
	public final Map<StationKey, CalcStation> stations = new HashMap<>();
}