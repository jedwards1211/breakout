package org.breakout.model;

import java.util.Date;
import java.util.List;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public class ParsedTrip {
	public UnitizedDouble<Angle> declination;
	public UnitizedDouble<Length> distanceCorrection;
	public UnitizedDouble<Angle> frontAzimuthCorrection;
	public UnitizedDouble<Angle> frontInclinationCorrection;
	public UnitizedDouble<Angle> backAzimuthCorrection;
	public UnitizedDouble<Angle> backInclinationCorrection;
	public boolean areBackAzimuthsCorrected;
	public boolean areBackInclinationsCorrected;
	public Date date;

	public List<ParsedRow> rows;
}
