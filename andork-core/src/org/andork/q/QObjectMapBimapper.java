package org.andork.q;

import java.util.LinkedHashMap;
import java.util.Map;

import org.andork.func.Bimapper;
import org.andork.q.QSpec.Attribute;

public class QObjectMapBimapper<S extends QSpec<S>> implements Bimapper<QObject<S>, Map<?, ?>> {
	S			spec;

	Bimapper[]	attrBimappers;

	public QObjectMapBimapper(S spec, Bimapper... attrBimappers) {
		if (attrBimappers.length != spec.getAttributeCount()) {
			throw new IllegalArgumentException("attrBimappers.length must equal spec.getAttributeCount()");
		}
		this.spec = spec;
		this.attrBimappers = attrBimappers;
	}

	public static <S extends QSpec<S>> QObjectMapBimapper<S> newInstance(S spec, Bimapper... attrBimappers) {
		return new QObjectMapBimapper<S>(spec, attrBimappers);
	}

	@Override
	public Map<?, ?> map(QObject<S> in) {
		Map<Object, Object> result = new LinkedHashMap<Object, Object>();
		for (int i = 0; i < spec.getAttributeCount(); i++) {
			Attribute<?> attribute = spec.attributeAt(i);
			if (in.has(attribute)) {
				Object value = in.get(attribute);
				result.put(attribute.getName(), value == null || attrBimappers[i] == null ? value : attrBimappers[i].map(value));

			}
		}
		return result;
	}

	@Override
	public QObject<S> unmap(Map<?, ?> out) {
		QObject<S> result = spec.newObject();
		for (int i = 0; i < spec.getAttributeCount(); i++) {
			Attribute<?> attribute = spec.attributeAt(i);
			if (out.containsKey(attribute.getName())) {
				Object value = out.get(attribute.getName());
				result.set(attribute, value == null || attrBimappers[i] == null ? value : attrBimappers[i].unmap(value));
			}
		}
		return result;
	}
}
