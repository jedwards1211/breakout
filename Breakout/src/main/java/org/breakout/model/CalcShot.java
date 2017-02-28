package org.breakout.model;

import java.util.ArrayList;
import java.util.List;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public class CalcShot {
	public CalcTrip trip;

	/**
	 * an earlier shot with the same from/to stations that this shot overrides,
	 * if any
	 */
	public CalcShot overrides;
	/**
	 * a later shot with the same from/to stations that overrides this shot, if
	 * any
	 */
	public CalcShot overriddenBy;

	public CalcStation fromStation;
	public CalcStation toStation;

	public UnitizedDouble<Length> distance;
	public boolean excludeDistance;
	public UnitizedDouble<Angle> azimuth;
	public UnitizedDouble<Angle> inclination;

	public List<float[]> fromSplayPoints = new ArrayList<>();
	public List<float[]> toSplayPoints = new ArrayList<>();

	public ShotKey key() {
		return fromStation != null && toStation != null
				? new ShotKey(fromStation.key(), toStation.key())
				: null;
	}
}
