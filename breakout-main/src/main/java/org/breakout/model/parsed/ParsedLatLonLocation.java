package org.breakout.model.parsed;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public class ParsedLatLonLocation {
	public ParsedField<UnitizedDouble<Angle>> latitude;
	public ParsedField<UnitizedDouble<Angle>> longitude;
	public ParsedField<UnitizedDouble<Length>> elevation;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParsedNEVLocation [latitude=").append(latitude).append(", longitude=").append(longitude)
				.append(", elevation=").append(elevation).append("]");
		return builder.toString();
	}
}
