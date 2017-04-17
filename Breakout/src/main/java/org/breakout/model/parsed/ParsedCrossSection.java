package org.breakout.model.parsed;

import java.util.Arrays;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;
import org.breakout.model.CrossSectionType;

public class ParsedCrossSection {
	public CrossSectionType type = CrossSectionType.LRUD;
	public ParsedField<UnitizedDouble<Angle>> facingAzimuth;
	public ParsedField<UnitizedDouble<Length>>[] measurements;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParsedCrossSection [type=").append(type).append(", facingAzimuth=").append(facingAzimuth)
				.append(", measurements=").append(Arrays.toString(measurements)).append("]");
		return builder.toString();
	}
}
