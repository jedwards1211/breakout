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
		StringBuilder builder = new StringBuilder();
		if (width != null) {
			builder.append(sizeFormat.format(width.doubleValue(unit)))
				.append('w');
		}
		if (height != null) {
			if (builder.length() > 0) builder.append(' ');
			builder.append(sizeFormat.format(height.doubleValue(unit)))
				.append('h');
		}
		return builder.length() > 0 ? builder.toString() : null;
	}
}
