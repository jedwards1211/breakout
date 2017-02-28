package org.andork.unit;

import java.text.NumberFormat;

public abstract class UnitizedNumber<T extends UnitType<T>> {
	public final Unit<T> unit;

	protected UnitizedNumber(Unit<T> unit) {
		super();
		if (unit == null) {
			throw new IllegalArgumentException("unit must be non-null");
		}
		this.unit = unit;
	}

	public abstract UnitizedNumber<T> add(UnitizedNumber<T> addend);

	public abstract UnitizedNumber<T> sub(UnitizedNumber<T> addend);

	public abstract UnitizedNumber<T> mul(Number multiplicand);

	public abstract UnitizedNumber<T> negate();

	public abstract UnitizedNumber<T> in(Unit<T> unit);

	public abstract Number get(Unit<T> unit);

	public abstract double doubleValue(Unit<T> unit);

	public abstract boolean isNegative();

	public abstract boolean isPositive();

	public abstract boolean isZero();

	public abstract UnitizedNumber<T> mod(UnitizedNumber<T> modulus);

	public abstract UnitizedNumber<T> abs();

	public float floatValue(Unit<T> unit) {
		return (float) doubleValue(unit);
	}

	public boolean isFinite() {
		return !isInfinite() && !isNaN();
	}

	public boolean isInfinite() {
		return false;
	}

	public boolean isNaN() {
		return false;
	}

	@Override
	public String toString() {
		return unit.toString(this);
	}

	public String toString(NumberFormat format) {
		return unit.toString(this, format);
	}
}
