package org.breakout.awt;

import java.text.NumberFormat;

import org.andork.bind.Binder;
import org.andork.unit.Unit;
import org.andork.unit.UnitType;
import org.andork.unit.UnitizedNumber;

public class UnitizedNumber2StringBinder<T extends UnitType<T>> extends Binder<String> {
	UnitType<T> type;
	Binder<UnitizedNumber<T>> numberBinder;
	Binder<Unit<T>> unitBinder;
	Binder<NumberFormat> formatBinder;
	
	public UnitizedNumber2StringBinder(UnitType<T> type, Binder<UnitizedNumber<T>> numberBinder,
			Binder<Unit<T>> unitBinder, Binder<NumberFormat> formatBinder) {
		super();
		this.type = type;
		this.numberBinder = numberBinder;
		this.unitBinder = unitBinder;
		this.formatBinder = formatBinder;
		bind0(numberBinder, this);
		bind0(unitBinder, this);
		bind0(formatBinder, this);
	}

	@Override
	public String get() {
		UnitizedNumber<T> number = numberBinder.get();
		if (number == null) return "--";
		Unit<T> unit = unitBinder.get();
		if (unit != null) {
			number = number.in(unit);
		}
		NumberFormat format = formatBinder.get();
		return format != null ? number.toString(formatBinder.get()) : number.toString();
	}

	@Override
	public void set(String newValue) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void update(boolean force) {
		 updateDownstream(force);
	}
}
