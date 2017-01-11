package org.andork.unit;

public class UnitizedFloat<T extends UnitType<T>> extends UnitizedNumber<T> {
	private final float value;

	public UnitizedFloat(Number value, Unit<T> unit) {
		super(unit);
		this.value = value.floatValue();
	}

	public UnitizedFloat(float value, Unit<T> unit) {
		super(unit);
		this.value = value;
	}

	/**
	 * Adds a {@link UnitizedFloat} to this one. The result will be in this
	 * {@link UnitizedFloat}'s units.
	 *
	 * @param addend
	 *            the {@link UnitizedFloat} to add to this one.
	 * @return
	 */
	@Override
	public UnitizedFloat<T> add(UnitizedNumber<T> addend) {
		return new UnitizedFloat<T>(value + addend.floatValue(unit), unit);
	}

	@Override
	public Float get(Unit<T> unit) {
		return floatValue(unit);
	}

	@Override
	public double doubleValue(Unit<T> unit) {
		if (unit == this.unit) {
			return value;
		}
		return this.unit.type.convert(value, this.unit, unit);
	}

	@Override
	public boolean isInfinite() {
		return Float.isInfinite(value);
	}

	@Override
	public boolean isNaN() {
		return Float.isNaN(value);
	}

	@Override
	public float floatValue(Unit<T> unit) {
		if (unit == this.unit) {
			return value;
		}
		return (float) this.unit.type.convert(value, this.unit, unit);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof UnitizedFloat) {
			UnitizedFloat<?> u = (UnitizedFloat<?>) o;
			return value == u.value && unit == u.unit;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Float.hashCode(value) * 31 ^ unit.hashCode();
	}

	@Override
	public UnitizedFloat<T> in(Unit<T> unit) {
		if (unit == this.unit) {
			return this;
		}
		return new UnitizedFloat<>(floatValue(unit), unit);
	}

	@Override
	public UnitizedFloat<T> negate() {
		return new UnitizedFloat<T>(-value, unit);
	}

	/**
	 * Subtracts a {@link UnitizedFloat} from this one. The result will be in
	 * this {@link UnitizedFloat}'s units.
	 *
	 * @param addend
	 *            the {@link UnitizedFloat} to subtract from this one.
	 * @return
	 */
	@Override
	public UnitizedFloat<T> sub(UnitizedNumber<T> addend) {
		return new UnitizedFloat<T>(value - addend.floatValue(unit), unit);
	}

	@Override
	public UnitizedFloat<T> mul(Number multiplicand) {
		return new UnitizedFloat<T>(value * multiplicand.doubleValue(), unit);
	}
}
