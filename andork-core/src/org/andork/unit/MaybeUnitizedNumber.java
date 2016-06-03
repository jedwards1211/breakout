package org.andork.unit;

public abstract class MaybeUnitizedNumber<T extends UnitType<T>> {
	public final Unit<T> unit;

	protected MaybeUnitizedNumber(Unit<T> unit) {
		super();
		this.unit = unit;
	}

	public abstract double doubleValue(Unit<T> unit, Unit<T> defaultUnit);
}
