package org.breakout.model.parsed;

import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public class ParsedNEVLocation {
	public ParsedField<UnitizedDouble<Length>> northing;
	public ParsedField<UnitizedDouble<Length>> easting;
	public ParsedField<UnitizedDouble<Length>> elevation;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParsedNEVLocation [northing=").append(northing).append(", easting=").append(easting)
				.append(", elevation=").append(elevation).append("]");
		return builder.toString();
	}
}
