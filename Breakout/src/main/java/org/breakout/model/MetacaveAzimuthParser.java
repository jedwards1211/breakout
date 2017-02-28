package org.breakout.model;

import org.andork.unit.Angle;
import org.andork.unit.Angle.AngleUnit;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;

public class MetacaveAzimuthParser extends MetacaveAngleParser {
	@Override
	public boolean isValidUnit(Unit<Angle> unit) {
		return super.isValidUnit(unit) && unit != Angle.percentGrade;
	}

	@Override
	public UnitizedDouble<Angle> parse(String text, Unit<Angle> defaultUnit) {
		UnitizedDouble<Angle> result = super.parse(text, defaultUnit);
		if (result != null) {
			AngleUnit unit = (AngleUnit) result.unit;
			if (result.isNegative() || result.doubleValue(unit) >= unit.range.doubleValue(unit)) {
				message = ParseMessage.error("azimuth out of range: " + result);
				result = null;
			}
		}
		return result;
	}
}
