package org.breakout.model;

import java.util.ArrayList;
import java.util.List;

import org.andork.collect.LinkedListMultiMap;
import org.andork.collect.MultiMap;
import org.andork.model.Property;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public class CalcRow {
	public CalcTrip trip;

	/** the previous shot in this traverse, if any */
	public CalcRow prev;
	/** the next shot in this traverse, if any */
	public CalcRow next;
	/**
	 * an earlier shot with the same from/to stations that this shot overrides,
	 * if any
	 */
	public CalcRow overrides;
	/**
	 * a later shot with the same from/to stations that overrides this shot, if
	 * any
	 */
	public CalcRow overriddenBy;

	public MultiMap<Property<SurveyRow, ?>, ParseMessage> messages = null;

	public void addMessage(Property<SurveyRow, ?> property, ParseMessage message) {
		if (messages == null) {
			messages = new LinkedListMultiMap<>();
		}
		messages.put(property, message);
	}

	public CalcStation fromStation;
	public CalcStation toStation;

	public UnitizedDouble<Length> distance;
	public boolean excludeDistance;
	public UnitizedDouble<Angle> azimuth;
	public UnitizedDouble<Angle> inclination;

	public UnitizedDouble<Length> left;
	public UnitizedDouble<Length> right;
	public UnitizedDouble<Length> up;
	public UnitizedDouble<Length> down;

	public List<float[]> fromSplayPoints = new ArrayList<>();
	public List<float[]> toSplayPoints = new ArrayList<>();
}
