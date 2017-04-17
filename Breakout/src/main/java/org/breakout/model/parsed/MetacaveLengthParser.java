package org.breakout.model.parsed;

import java.util.HashMap;
import java.util.Map;

import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;
import org.breakout.model.parsed.ParseMessage.Severity;

public class MetacaveLengthParser {
	public UnitizedDouble<Length> length;

	public static final Map<String, Unit<Length>> units = new HashMap<>();
	static {
		units.put("m", Length.meters);
		units.put("cm", Length.centimeters);
		units.put("yd", Length.yards);
		units.put("ft", Length.feet);
		units.put("in", Length.inches);
	}

	public static ParsedField<UnitizedDouble<Length>> parse(String text, Unit<Length> defaultUnit) {
		if (text == null) {
			return null;
		}
		text = text.trim();
		if (text.isEmpty()) {
			return null;
		}

		Unit<Length> unit;
		UnitizedDouble<Length> length;

		String[] parts = text.toLowerCase().split("\\s+");
		if (parts.length == 0) {
			return null;
		}

		if (parts.length > 1) {
			unit = units.get(parts[1]);
			if (unit == null) {
				return new ParsedField<>(Severity.ERROR, "invalid unit: " + parts[1]);
			}
		} else {
			unit = defaultUnit;
		}

		try {
			double value = Double.parseDouble(parts[0]);
			length = new UnitizedDouble<>(value, unit);
		} catch (NumberFormatException ex) {
			return new ParsedField<>(Severity.ERROR, "invalid number: " + parts[0]);
		}

		for (int i = 2; i < parts.length - 1; i += 2) {
			unit = units.get(parts[i + 1]);
			if (unit == null) {
				return new ParsedField<>(Severity.ERROR, "invalid unit: " + parts[i + 1]);
			}
			try {
				double value = Double.parseDouble(parts[i]);
				length = length.add(new UnitizedDouble<>(value, unit));
			} catch (NumberFormatException ex) {
				return new ParsedField<>(Severity.ERROR, "invalid number: " + parts[i]);
			}
		}

		return new ParsedField<>(length);
	}
}
