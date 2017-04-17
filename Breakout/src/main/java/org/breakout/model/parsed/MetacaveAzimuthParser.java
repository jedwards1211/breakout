package org.breakout.model.parsed;

import org.andork.unit.Angle;
import org.andork.unit.Angle.AngleUnit;
import org.breakout.model.parsed.ParseMessage.Severity;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;

public class MetacaveAzimuthParser {

	public static ParsedField<UnitizedDouble<Angle>> parse(String text, Unit<Angle> defaultUnit) {
		ParsedField<UnitizedDouble<Angle>> result = MetacaveAngleParser.parse(text, defaultUnit);
		if (result != null && result.value != null) {
			AngleUnit unit = (AngleUnit) result.value.unit;
			if (unit == Angle.percentGrade) {
				result.message = new ParseMessage(Severity.ERROR, "invalid azimuth unit: " + unit);
			}
			if (result.value.isNegative() || result.value.doubleValue(unit) >= unit.range.doubleValue(unit)) {
				result.message = new ParseMessage(Severity.ERROR, "azimuth out of range: " + result);
			}
		}
		return result;
	}
}
