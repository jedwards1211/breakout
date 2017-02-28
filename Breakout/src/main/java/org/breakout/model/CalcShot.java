package org.breakout.model;

import java.util.ArrayList;
import java.util.List;

public class CalcShot {
	public CalcShot overrides;
	public CalcShot overriddenBy;

	public CalcStation fromStation;
	public CalcStation toStation;

	public double distance;
	public boolean excludeDistance;
	public double azimuth;
	public double inclination;

	public List<float[]> fromSplayNormals = new ArrayList<>();
	public List<float[]> toSplayNormals = new ArrayList<>();

	public ShotKey key() {
		return fromStation != null && toStation != null
				? new ShotKey(fromStation.key(), toStation.key())
				: null;
	}
}
