package org.andork.unit;

public class UnitizedDouble<T extends UnitType<T>> extends UnitizedNumber<T> {
	private final double value;

	public UnitizedDouble(Number value, Unit<T> unit) {
		super(unit);
		this.value = value.doubleValue();
	}

	public UnitizedDouble(double value, Unit<T> unit) {
		super(unit);
		this.value = value;
	}

	/**
	 * Adds a {@link UnitizedDouble} to this one. The result will be in this
	 * {@link UnitizedDouble}'s units.
	 *
	 * @param addend
	 *            the {@link UnitizedDouble} to add to this one.
	 * @return
	 */
	@Override
	public UnitizedDouble<T> add(UnitizedNumber<T> addend) {
		return new UnitizedDouble<T>(value + addend.doubleValue(unit), unit);
	}

	@Override
	public Double get(Unit<T> unit) {
		return doubleValue(unit);
	}

	@Override
	public boolean isInfinite() {
		return Double.isInfinite(value);
	}

	@Override
	public boolean isNaN() {
		return Double.isNaN(value);
	}

	@Override
	public double doubleValue(Unit<T> unit) {
		if (unit == this.unit) {
			return value;
		}
		return this.unit.type.convert(value, this.unit, unit);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof UnitizedDouble) {
			UnitizedDouble<?> u = (UnitizedDouble<?>) o;
			return value == u.value && unit == u.unit;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(value) * 31 ^ unit.hashCode();
	}

	@Override
	public UnitizedDouble<T> in(Unit<T> unit) {
		if (unit == this.unit) {
			return this;
		}
		return new UnitizedDouble<>(doubleValue(unit), unit);
	}

	@Override
	public UnitizedDouble<T> negate() {
		return new UnitizedDouble<T>(-value, unit);
	}

	/**
	 * Subtracts a {@link UnitizedDouble} from this one. The result will be in
	 * this {@link UnitizedDouble}'s units.
	 *
	 * @param addend
	 *            the {@link UnitizedDouble} to subtract from this one.
	 * @return
	 */
	@Override
	public UnitizedDouble<T> sub(UnitizedNumber<T> addend) {
		return new UnitizedDouble<T>(value - addend.doubleValue(unit), unit);
	}

	@Override
	public UnitizedDouble<T> mul(Number multiplicand) {
		return new UnitizedDouble<T>(value * multiplicand.doubleValue(), unit);
	}
}
