package org.breakout.model;

import java.util.HashMap;
import java.util.Map;

import org.andork.unit.Angle;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;

public class MetacaveAngleParser extends MetacaveMeasurementParser<Angle> {
	public UnitizedDouble<Angle> angle;

	public static final Map<String, Unit<Angle>> units = new HashMap<>();
	static {
		units.put("deg", Angle.degrees);
		units.put("rad", Angle.radians);
		units.put("grad", Angle.gradians);
		units.put("mil", Angle.milsNATO);
		units.put("%", Angle.percentGrade);
	}

	public boolean isValidUnit(Unit<Angle> unit) {
		return unit != null;
	}

	@Override
	public UnitizedDouble<Angle> parse(String text, Unit<Angle> defaultUnit) {
		angle = null;
		severity = null;
		message = null;

		Unit<Angle> unit;
		UnitizedDouble<Angle> angle;

		String[] parts = text.trim().toLowerCase().split("\\s+");
		if (parts.length == 0) {
			return null;
		}

		if (parts.length > 1) {
			unit = units.get(parts[1]);
			if (!isValidUnit(unit)) {
				severity = Severity.ERROR;
				message = "invalid unit: " + parts[1];
				return null;
			}
		} else {
			unit = defaultUnit;
		}

		try {
			double value = Double.parseDouble(parts[0]);
			angle = new UnitizedDouble<>(value, unit);
		} catch (NumberFormatException ex) {
			severity = Severity.ERROR;
			message = "invalid number: " + parts[0];
			return null;
		}

		for (int i = 2; i < parts.length - 1; i += 2) {
			unit = units.get(parts[i + 1]);
			if (!isValidUnit(unit)) {
				severity = Severity.ERROR;
				message = "invalid unit: " + parts[i + 1];
				return null;
			}
			try {
				double value = Double.parseDouble(parts[i]);
				angle = angle.add(new UnitizedDouble<>(value, unit));
			} catch (NumberFormatException ex) {
				severity = Severity.ERROR;
				message = "invalid number: " + parts[i];
				return null;
			}
		}

		return this.angle = angle;
	}
}
