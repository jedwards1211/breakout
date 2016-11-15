package org.andork.unit;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class UnitType<T extends UnitType<T>> {
	private final Set<Unit<T>> units = new LinkedHashSet<>();
	private final Set<Unit<T>> unmodifiableUnits = Collections.unmodifiableSet(units);

	protected final void addUnit(Unit<T> unit) {
		units.add(unit);
	}

	public abstract double convert(double d, Unit<T> from, Unit<T> to);

	public float convertf(float f, Unit<T> from, Unit<T> to) {
		return (float) convert(f, from, to);
	}

	public final Set<Unit<T>> units() {
		return unmodifiableUnits;
	}
}
