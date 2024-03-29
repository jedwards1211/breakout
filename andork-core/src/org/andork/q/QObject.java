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

import java.util.Arrays;

import org.andork.func.Mapper;
import org.andork.model.Model;
import org.andork.q.QSpec.Attribute;

public final class QObject<S extends QSpec<S>> extends QElement implements Model {
	private static final Object NOT_PRESENT = new Object();

	public static <S extends QSpec<S>> QObject<S> newInstance(S spec) {
		return new QObject<S>(spec);
	}

	private final S spec;

	private final Object[] attributes;

	/**
	 * Creates a {@code QObject} with the given spec.
	 *
	 * @param spec the {@link QSpec} for this QObject.
	 */
	private QObject(S spec) {
		this.spec = spec;
		this.attributes = new Object[spec.getAttributeCount()];
		Arrays.fill(this.attributes, NOT_PRESENT);
	}

	private <T> void checkBelongs(Attribute<T> attribute) {
		if (spec.attributes[attribute.index] != attribute) {
			throw new IllegalArgumentException("attribute does not belong to this spec");
		}
	}

	private static class DefaultChildMapper implements Mapper<Object, Object> {
		@Override
		public Object map(Object in) {
			if (in instanceof QElement) {
				return ((QElement) in).deepClone(this);
			}
			return in;
		}

		public static DefaultChildMapper instance = new DefaultChildMapper();
	}

	public QObject<S> deepClone() {
		return deepClone(DefaultChildMapper.instance);
	}

	@Override
	public QObject<S> deepClone(Mapper<Object, Object> childMapper) {
		QObject<S> result = spec.newObject();
		for (int i = 0; i < attributes.length; i++) {
			if (attributes[i] != NOT_PRESENT) {
				result.set(spec.attributeAt(i), childMapper.map(attributes[i]));
			}
		}
		return result;
	}

	/**
	 * Gets the value of an attribute.<br>
	 * <br>
	 * To see the list of attributes this QObject has, use
	 * {@link QSpec#getAttributes() getSpec().getAttributes()}.
	 *
	 * @param attribute the attribute to get the value of.
	 * @return the value of the attribute (may be {@code null}).
	 */
	public <T> T get(Attribute<T> attribute) {
		checkBelongs(attribute);
		return attributes[attribute.index] == NOT_PRESENT ? null : (T) attributes[attribute.index];
	}

	@Override
	public Object get(Object key) {
		return get((Attribute) key);
	}

	/**
	 * @return the {@link QSpec} of this QObject.
	 */
	public S getSpec() {
		return spec;
	}

	public boolean has(Attribute<?> attribute) {
		checkBelongs(attribute);
		return attributes[attribute.index] != NOT_PRESENT;
	}

	public <T> T remove(Attribute<T> attribute) {
		checkBelongs(attribute);
		Object oldValue = attributes[attribute.index];
		if (oldValue == NOT_PRESENT) {
			return null;
		}
		if (oldValue instanceof QElement) {
			((QElement) oldValue).changeSupport().removePropertyChangeListener(propagator);
		}
		attributes[attribute.index] = NOT_PRESENT;
		changeSupport.firePropertyChange(this, attribute, oldValue, null);
		return (T) oldValue;
	}

	public <T> T set(Attribute<T> attribute, T newValue) {
		checkBelongs(attribute);
		T oldValue = (T) attributes[attribute.index];
		if (oldValue == NOT_PRESENT) {
			oldValue = null;
		}
		if (oldValue instanceof QElement) {
			((QElement) oldValue).changeSupport().removePropertyChangeListener(propagator);
		}
		attributes[attribute.index] = newValue;
		if (newValue instanceof QElement) {
			((QElement) newValue).changeSupport().addPropertyChangeListener(propagator);
		}
		if (!attribute.equals.test(oldValue, newValue)) {
			changeSupport.firePropertyChange(this, attribute, oldValue, newValue);
		}
		return oldValue;
	}

	public <T> T setIfNull(Attribute<T> attribute, T defaultValue) {
		T value = get(attribute);
		if (value != null)
			return value;
		set(attribute, defaultValue);
		return defaultValue;
	}

	@Override
	public void set(Object key, Object newValue) {
		set((Attribute) key, newValue);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('{');
		for (int i = 0; i < attributes.length; i++) {
			if (attributes[i] != NOT_PRESENT) {
				if (builder.length() > 1) {
					builder.append(',');
				}
				builder.append('"').append(spec.attributes[i].getName()).append("\":");
				builder.append(String.valueOf(attributes[i]));
			}
		}
		builder.append('}');
		return builder.toString();
	}

	public Object valueAt(int index) {
		return attributes[index] == NOT_PRESENT ? null : attributes[index];
	}
}
