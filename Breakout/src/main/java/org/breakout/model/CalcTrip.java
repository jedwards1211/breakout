package org.breakout.model;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public class CalcTrip {
	public UnitizedDouble<Angle> declination;
	public UnitizedDouble<Length> distanceCorrection;
	public UnitizedDouble<Angle> frontAzimuthCorrection;
	public UnitizedDouble<Angle> frontInclinationCorrection;
	public UnitizedDouble<Angle> backAzimuthCorrection;
	public UnitizedDouble<Angle> backInclinationCorrection;
}
