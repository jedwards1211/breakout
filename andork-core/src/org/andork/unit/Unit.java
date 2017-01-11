package org.andork.unit;

import java.text.NumberFormat;

public class Unit<T extends UnitType<T>> {
	public final T type;
	public final String id;

	public Unit(T type, String id) {
		super();
		this.type = type;
		this.id = id;
	}

	@Override
	public String toString() {
		return id;
	}

	public String toString(UnitizedNumber<T> number) {
		return number.get(this) + " " + id;
	}

	public String toString(UnitizedNumber<T> number, NumberFormat format) {
		return format.format(number.get(this)) + " " + id;
	}
}
