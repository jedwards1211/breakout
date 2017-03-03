package org.breakout.model;

import java.util.Date;

public class CalcShot {
	public CalcShot overrides;
	public CalcShot overriddenBy;

	public CalcStation fromStation;
	public CalcStation toStation;

	public double distance;
	public boolean excludeDistance;
	public double azimuth;
	public double inclination;

	public float[] fromSplayNormals;
	public float[] toSplayNormals;

	public float[] fromSplayPoints;
	public float[] toSplayPoints;

	public Date date;

	public ShotKey key() {
		return fromStation != null && toStation != null
				? new ShotKey(fromStation.key(), toStation.key())
				: null;
	}

	public CalcStation otherStation(CalcStation station) {
		if (station == fromStation) {
			return toStation;
		}
		if (station == toStation) {
			return fromStation;
		}
		throw new IllegalArgumentException("station must be this shot's from or to station");
	}
}
