package org.breakout.model.parsed;

import java.text.DecimalFormat;

import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;

public class Lead {
	public String description;
	public ParsedField<UnitizedDouble<Length>> width;
	public ParsedField<UnitizedDouble<Length>> height;

	private final DecimalFormat sizeFormat = new DecimalFormat("0.#");

	public String describeSize(Unit<Length> unit) {
		UnitizedDouble<Length> width = ParsedField.getValue(this.width);
		UnitizedDouble<Length> height = ParsedField.getValue(this.height);
		if (width == null || height == null)
			return null;
		return String.format("%sw %sh", sizeFormat.format(width.doubleValue(unit)),
				sizeFormat.format(height.doubleValue(unit)));
	}
}
