/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.q;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.andork.func.BigDecimalBimapper;
import org.andork.func.BigIntegerBimapper;
import org.andork.func.Bimapper;
import org.andork.func.Bimappers;
import org.andork.func.BooleanBimapper;
import org.andork.func.DoubleBimapper;
import org.andork.func.EnumBimapper;
import org.andork.func.FloatArray2ListBimapper;
import org.andork.func.FloatBimapper;
import org.andork.func.IntegerBimapper;
import org.andork.func.LongBimapper;
import org.andork.func.StringBimapper;
import org.andork.q.QSpec.Attribute;

public class QObjectMapBimapper<S extends QSpec<S>> implements Bimapper<QObject<S>, Object> {
	private static final Logger LOGGER = Logger.getLogger(QObjectMapBimapper.class.getName());

	public static <S extends QSpec<S>> QObjectMapBimapper<S> newInstance(S spec, Bimapper... attrBimappers) {
		return new QObjectMapBimapper<S>(spec, attrBimappers);
	}

	S spec;

	static final Bimapper EXCLUDED = Bimappers.identity();
	Bimapper[] attrBimappers;

	public QObjectMapBimapper(S spec) {
		this(spec, new Bimapper[spec.getAttributeCount()]);
		for (int i = 0; i < spec.getAttributeCount(); i++) {
			Class<?> valueClass = spec.attributeAt(i).getValueClass();
			if (valueClass == String.class) {
				attrBimappers[i] = StringBimapper.instance;
			}
			if (valueClass == Boolean.class) {
				attrBimappers[i] = BooleanBimapper.instance;
			} else if (valueClass == Integer.class) {
				attrBimappers[i] = IntegerBimapper.instance;
			} else if (valueClass == Long.class) {
				attrBimappers[i] = LongBimapper.instance;
			} else if (valueClass == Float.class) {
				attrBimappers[i] = FloatBimapper.instance;
			} else if (valueClass == float[].class) {
				attrBimappers[i] = FloatArray2ListBimapper.instance;
			} else if (valueClass.isEnum()) {
				attrBimappers[i] = EnumBimapper.newInstance((Class<Enum>) valueClass);
			} else if (valueClass == Double.class) {
				attrBimappers[i] = DoubleBimapper.instance;
			} else if (valueClass == BigInteger.class) {
				attrBimappers[i] = BigIntegerBimapper.instance;
			} else if (valueClass == BigDecimal.class) {
				attrBimappers[i] = BigDecimalBimapper.instance;
			}
		}
	}

	public QObjectMapBimapper(S spec, Bimapper... attrBimappers) {
		if (attrBimappers.length != spec.getAttributeCount()) {
			throw new IllegalArgumentException("attrBimappers.length must equal spec.getAttributeCount()");
		}
		this.spec = spec;
		this.attrBimappers = attrBimappers;
	}

	public QObjectMapBimapper<S> exclude(Attribute<?> attr) {
		attrBimappers[attr.index] = EXCLUDED;
		return this;
	}

	public QObjectMapBimapper<S> map(Attribute<?> attr, Bimapper bimapper) {
		attrBimappers[attr.index] = bimapper;
		return this;
	}

	@Override
	public Object map(QObject<S> in) {
		if (in == null) {
			return null;
		}
		Map<Object, Object> result = new LinkedHashMap<Object, Object>();
		for (int i = 0; i < spec.getAttributeCount(); i++) {
			Attribute<?> attribute = spec.attributeAt(i);
			if (in.has(attribute) && attrBimappers[i] != EXCLUDED) {
				Object value = in.get(attribute);
				result.put(attribute.getName(),
						value == null || attrBimappers[i] == null ? value : attrBimappers[i].map(value));

			}
		}
		return result;
	}

	@Override
	public QObject<S> unmap(Object out) {
		if (out == null) {
			return null;
		}
		Map<?, ?> m = (Map<?, ?>) out;
		QObject<S> result = spec.newObject();
		for (int i = 0; i < spec.getAttributeCount(); i++) {
			Attribute<?> attribute = spec.attributeAt(i);
			if (m.containsKey(attribute.getName()) && attrBimappers[i] != EXCLUDED) {
				Object value = m.get(attribute.getName());
				try {
					result.set(attribute,
							value == null || attrBimappers[i] == null ? value : attrBimappers[i].unmap(value));
				} catch (Throwable t) {
					LOGGER.log(Level.WARNING, "Failed to set attribute: " + attribute, t);
				}
			}
		}
		return result;
	}
}
