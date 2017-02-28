package org.breakout.model;

import java.util.HashMap;
import java.util.Map;

import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;

public class MetacaveLengthParser extends MetacaveMeasurementParser<Length> {
	public UnitizedDouble<Length> length;

	public static final Map<String, Unit<Length>> units = new HashMap<>();
	static {
		units.put("m", Length.meters);
		units.put("cm", Length.centimeters);
		units.put("yd", Length.yards);
		units.put("ft", Length.feet);
		units.put("in", Length.inches);
	}

	@Override
	public UnitizedDouble<Length> parse(String text, Unit<Length> defaultUnit) {
		length = null;
		severity = null;
		message = null;

		Unit<Length> unit;
		UnitizedDouble<Length> length;

		String[] parts = text.trim().toLowerCase().split("\\s+");
		if (parts.length == 0) {
			return null;
		}

		if (parts.length > 1) {
			unit = units.get(parts[1]);
			if (unit == null) {
				severity = Severity.ERROR;
				message = "invalid unit: " + parts[1];
				return null;
			}
		} else {
			unit = defaultUnit;
		}

		try {
			double value = Double.parseDouble(parts[0]);
			length = new UnitizedDouble<>(value, unit);
		} catch (NumberFormatException ex) {
			severity = Severity.ERROR;
			message = "invalid number: " + parts[0];
			return null;
		}

		for (int i = 2; i < parts.length - 1; i += 2) {
			unit = units.get(parts[i + 1]);
			if (unit == null) {
				severity = Severity.ERROR;
				message = "invalid unit: " + parts[i + 1];
				return null;
			}
			try {
				double value = Double.parseDouble(parts[i]);
				length = length.add(new UnitizedDouble<>(value, unit));
			} catch (NumberFormatException ex) {
				severity = Severity.ERROR;
				message = "invalid number: " + parts[i];
				return null;
			}
		}

		return this.length = length;
	}
}
