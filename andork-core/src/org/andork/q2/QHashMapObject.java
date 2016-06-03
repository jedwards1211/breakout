package org.andork.q2;

import java.util.HashMap;
import java.util.Map;

import org.andork.q2.QSpec.Property;

public class QHashMapObject<S extends QSpec> extends QMapObject<S> {

	public static <S extends QSpec> QHashMapObject<S> create(S spec) {
		return new QHashMapObject<S>(spec);
	}

	public QHashMapObject(S spec) {
		super(spec);
	}

	@Override
	protected Map<Property<?>, Object> createValuesMap() {
		return new HashMap<>();
	}
}
