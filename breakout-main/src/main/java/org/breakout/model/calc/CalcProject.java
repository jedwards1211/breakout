package org.breakout.model.calc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.breakout.model.ShotKey;
import org.breakout.model.StationKey;
import org.osgeo.proj4j.CoordinateReferenceSystem;

public class CalcProject {
	public final Map<String, CalcCave> caves = new HashMap<>();
	public final Map<ShotKey, CalcShot> shots = new LinkedHashMap<>();
	public final Map<StationKey, CalcStation> stations = new LinkedHashMap<>();
	public final double[] zeroOffset = new double[3];
	public CoordinateReferenceSystem coordinateReferenceSystem;
}
