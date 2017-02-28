package org.breakout.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public class CalcStation {
	public String name;
	public String cave;
	public UnitizedDouble<Length> northing;
	public UnitizedDouble<Length> easting;
	public UnitizedDouble<Length> elevation;

	public final double[] position = new double[3];

	public final Map<StationKey, CalcRow> shots = new LinkedHashMap<>();
}
