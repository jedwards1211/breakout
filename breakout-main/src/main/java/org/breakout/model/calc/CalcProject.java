package org.breakout.model.calc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.breakout.model.ShotKey;
import org.breakout.model.StationKey;
import org.osgeo.proj4j.CoordinateReferenceSystem;

public class CalcProject {
	public final Map<String, CalcCave> caves = new HashMap<>();
	public final Map<ShotKey, CalcShot> shots = new LinkedHashMap<>();
	public final Map<StationKey, CalcStation> stations = new LinkedHashMap<>();
	public CoordinateReferenceSystem coordinateReferenceSystem;

	public Set<ShotKey> getPlottedShotKeys(Set<ShotKey> result) {
		shots.forEach((key, shot) -> {
			if (!shot.isExcludeFromPlotting()) result.add(key);
		});
		return result;
	}
	
	public Set<ShotKey> getPlottedShotKeys() {
		return getPlottedShotKeys(new HashSet<>());
	}
}
