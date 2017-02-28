package org.breakout.model;

import org.andork.unit.Angle;
import org.andork.unit.Angle.AngleUnit;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;

public class MetacaveInclinationParser extends MetacaveAngleParser {
	@Override
	public UnitizedDouble<Angle> parse(String text, Unit<Angle> defaultUnit) {
		UnitizedDouble<Angle> result = super.parse(text, defaultUnit);
		if (result != null) {
			AngleUnit unit = (AngleUnit) result.unit;
			double limit = unit.range.get(unit) / 4;
			if (result.get(unit) < -limit || result.get(unit) > limit) {
				severity = Severity.ERROR;
				message = "inclination out of range: " + result;
				result = null;
			}
		}
		return result;
	}
}
