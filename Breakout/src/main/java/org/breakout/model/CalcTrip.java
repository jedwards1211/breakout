package org.breakout.model;

import org.andork.collect.LinkedListMultiMap;
import org.andork.collect.MultiMap;
import org.andork.model.Property;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public class CalcTrip {
	public MultiMap<Property<SurveyTrip, ?>, ParseMessage> messages = null;

	public void addMessage(Property<SurveyTrip, ?> property, ParseMessage message) {
		if (messages == null) {
			messages = new LinkedListMultiMap<>();
		}
		messages.put(property, message);
	}

	public UnitizedDouble<Angle> declination;
	public UnitizedDouble<Length> distanceCorrection;
	public UnitizedDouble<Angle> frontAzimuthCorrection;
	public UnitizedDouble<Angle> frontInclinationCorrection;
	public UnitizedDouble<Angle> backAzimuthCorrection;
	public UnitizedDouble<Angle> backInclinationCorrection;
}
