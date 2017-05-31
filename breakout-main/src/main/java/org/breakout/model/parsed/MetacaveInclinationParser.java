package org.breakout.model.parsed;

import org.andork.unit.Angle;
import org.andork.unit.Angle.AngleUnit;
import org.breakout.model.parsed.ParseMessage.Severity;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;

public class MetacaveInclinationParser {
	public static ParsedField<UnitizedDouble<Angle>> parse(String text, Unit<Angle> defaultUnit) {
		ParsedField<UnitizedDouble<Angle>> result = MetacaveInclinationParser.parse(text, defaultUnit);
		if (result != null && result.value != null) {
			AngleUnit unit = (AngleUnit) result.value.unit;
			double limit = unit.range.get(unit) / 4;
			if (result.value.get(unit) < -limit || result.value.get(unit) > limit) {
				result.message = new ParseMessage(Severity.ERROR, "inclination out of range: " + result);
			}
		}
		return result;
	}
}
