package org.breakout.model;

import org.andork.func.Bimapper;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitType;

public class Unit2StringBimapper<T extends UnitType<T>> implements Bimapper<Unit<T>, String> {
	private Unit2StringBimapper(UnitType<T> type) {
		this.type = type;
	}
	
	public static final Unit2StringBimapper<Length> length = new Unit2StringBimapper<>(Length.type);
	public static final Unit2StringBimapper<Angle> angle = new Unit2StringBimapper<>(Angle.type);
	
	private final UnitType<T> type;

	@Override
	public String map(Unit<T> in) {
		return in.id;
	}

	@Override
	public Unit<T> unmap(String out) {
		return type.unitsById().get(out);
	}
}
