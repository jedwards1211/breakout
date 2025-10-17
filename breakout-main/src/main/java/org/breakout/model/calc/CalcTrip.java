package org.breakout.model.calc;

import java.util.LinkedHashMap;
import java.util.List;

import org.breakout.model.ShotKey;

public class CalcTrip {
	public CalcCave cave;
	public String name;
	public final LinkedHashMap<ShotKey, CalcShot> shots = new LinkedHashMap<>();
	public List<String> attachedFiles;
}
