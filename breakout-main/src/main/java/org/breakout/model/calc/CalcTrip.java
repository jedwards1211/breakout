package org.breakout.model.calc;

import java.util.LinkedHashMap;

import org.breakout.model.ShotKey;

public class CalcTrip {
	public CalcCave cave;
	public final LinkedHashMap<ShotKey, CalcShot> shots = new LinkedHashMap<>();
}
