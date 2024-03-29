package org.breakout.model.parsed;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

import com.github.krukow.clj_ds.PersistentVector;

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
	public String name;
	public List<String> surveyors;
	public ParsedField<Date> date;
	public ParsedField<String> datum;
	public ParsedField<String> ellipsoid;
	public ParsedField<Integer> utmZone;
	public final Map<String, ParsedFixedStation> fixedStations = new HashMap<>();

	public final List<ParsedStation> stations = new ArrayList<>();
	public final List<ParsedShot> shots = new ArrayList<>();
	public PersistentVector<String> attachedFiles;
}
