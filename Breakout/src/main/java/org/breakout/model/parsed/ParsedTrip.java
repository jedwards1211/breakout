package org.breakout.model.parsed;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public class ParsedTrip {
	public List<ParseMessage> messages = new ArrayList<>(1);
	public ParsedField<UnitizedDouble<Angle>> declination;
	public ParsedField<UnitizedDouble<Length>> distanceCorrection;
	public ParsedField<UnitizedDouble<Angle>> frontAzimuthCorrection;
	public ParsedField<UnitizedDouble<Angle>> frontInclinationCorrection;
	public ParsedField<UnitizedDouble<Angle>> backAzimuthCorrection;
	public ParsedField<UnitizedDouble<Angle>> backInclinationCorrection;
	public boolean areBackAzimuthsCorrected;
	public boolean areBackInclinationsCorrected;
	public ParsedField<Date> date;

	public final List<ParsedStation> stations = new ArrayList<>();
	public final List<ParsedShot> shots = new ArrayList<>();
}