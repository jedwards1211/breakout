package org.breakout.model;

import org.andork.unit.Unit;
import org.andork.unit.UnitType;
import org.andork.unit.UnitizedDouble;

public abstract class MetacaveMeasurementParser<T extends UnitType<T>> {
	public static enum Severity {
		INFO, WARNING, ERROR;
	}

	public Severity severity;
	public String message;

	public abstract UnitizedDouble<T> parse(String text, Unit<T> defaultUnit);
}
