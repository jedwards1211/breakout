package org.breakout.model.calc;

import java.util.ArrayList;
import java.util.List;

import org.andork.collect.HashSetMultiMap;
import org.andork.collect.MultiMap;

public class CalcCave {
	public String name;
	public final List<CalcTrip> trips = new ArrayList<>();
	public final MultiMap<String, CalcStation> stationsBySurveyDesignation = new HashSetMultiMap<>();
}
