package org.breakout.model;

import static org.andork.util.JavaScript.truthy;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public class ParsedRow {
	public ParsedTrip trip;

	public String fromCave;
	public String fromStation;
	public String toCave;
	public String toStation;
	public UnitizedDouble<Length> distance;
	public boolean excludeDistance;
	public UnitizedDouble<Angle> frontAzimuth;
	public UnitizedDouble<Angle> backAzimuth;
	public UnitizedDouble<Angle> frontInclination;
	public UnitizedDouble<Angle> backInclination;
	public CrossSectionType crossSectionType;
	public UnitizedDouble<Length> left;
	public UnitizedDouble<Length> right;
	public UnitizedDouble<Length> up;
	public UnitizedDouble<Length> down;
	public UnitizedDouble<Length> northing;
	public UnitizedDouble<Length> easting;
	public UnitizedDouble<Length> elevation;

	public ParsedRow overrides;
	public ParsedRow overriddenBy;

	public StationKey fromKey() {
		return truthy(fromStation) ? new StationKey(fromCave, fromStation) : null;
	}

	public StationKey toKey() {
		return truthy(toStation) ? new StationKey(toCave, toStation) : null;
	}

	public ShotKey key() {
		return truthy(fromStation) && truthy(toStation)
				? new ShotKey(fromCave, fromStation, toCave, toStation)
				: null;
	}
}
