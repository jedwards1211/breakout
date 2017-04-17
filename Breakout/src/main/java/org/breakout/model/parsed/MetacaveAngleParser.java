package org.breakout.model.parsed;

import java.util.HashMap;
import java.util.Map;

import org.andork.unit.Angle;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;
import org.breakout.model.parsed.ParseMessage.Severity;

public class MetacaveAngleParser {
	public UnitizedDouble<Angle> angle;

	public static final Map<String, Unit<Angle>> units = new HashMap<>();
	static {
		units.put("deg", Angle.degrees);
		units.put("rad", Angle.radians);
		units.put("grad", Angle.gradians);
		units.put("mil", Angle.milsNATO);
		units.put("%", Angle.percentGrade);
	}

	public static boolean isValidUnit(Unit<Angle> unit) {
		return unit != null;
	}

	public static ParsedField<UnitizedDouble<Angle>> parse(String text, Unit<Angle> defaultUnit) {
		if (text == null) {
			return null;
		}
		text = text.trim();
		if (text.isEmpty()) {
			return null;
		}

		Unit<Angle> unit;
		UnitizedDouble<Angle> angle;

		String[] parts = text.toLowerCase().split("\\s+");
		if (parts.length == 0) {
			return null;
		}

		if (parts.length > 1) {
			unit = units.get(parts[1]);
			if (!isValidUnit(unit)) {
				return new ParsedField<>(Severity.ERROR, "invalid unit: " + parts[1]);
			}
		} else {
			unit = defaultUnit;
		}

		try {
			double value = Double.parseDouble(parts[0]);
			angle = new UnitizedDouble<>(value, unit);
		} catch (NumberFormatException ex) {
			return new ParsedField<>(Severity.ERROR, "invalid number: " + parts[0]);
		}

		for (int i = 2; i < parts.length - 1; i += 2) {
			unit = units.get(parts[i + 1]);
			if (!isValidUnit(unit)) {
				return new ParsedField<>(Severity.ERROR, "invalid unit: " + parts[i + 1]);
			}
			try {
				double value = Double.parseDouble(parts[i]);
				angle = angle.add(new UnitizedDouble<>(value, unit));
			} catch (NumberFormatException ex) {
				return new ParsedField<>(Severity.ERROR, "invalid number: " + parts[i]);
			}
		}

		return new ParsedField<>(angle);
	}
}
