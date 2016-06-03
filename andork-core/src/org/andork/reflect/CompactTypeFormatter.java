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
package org.andork.reflect;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import org.andork.collect.HashSetMultiMap;

public class CompactTypeFormatter implements TypeFormatter {
	HashSetMultiMap<String, Class<?>> disambiguationMap = HashSetMultiMap.newInstance();

	@Override
	public String format(Type t) {
		if (t instanceof WildcardType) {
			WildcardType wt = (WildcardType) t;
			StringBuffer sb = new StringBuffer("?");
			Type[] lowerBounds = wt.getLowerBounds();
			Type[] upperBounds = wt.getUpperBounds();

			if (lowerBounds != null && lowerBounds.length > 0) {
				for (int i = 0; i < lowerBounds.length; i++) {
					if (lowerBounds[i] != Object.class) {
						sb.append(" super ").append(format(lowerBounds[i]));
					}
				}
			}
			if (upperBounds != null && upperBounds.length > 0) {
				for (int i = 0; i < upperBounds.length; i++) {
					if (upperBounds[i] != Object.class) {
						sb.append(" extends ").append(format(upperBounds[i]));
					}
				}
			}
			return sb.toString();
		} else if (t instanceof TypeVariable<?>) {
			return ((TypeVariable<?>) t).getName();
		} else if (t instanceof GenericArrayType) {
			return format(((GenericArrayType) t).getGenericComponentType()) + "[]";
		} else if (t instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) t;
			StringBuffer sb = new StringBuffer();
			sb.append(format(pt.getRawType()));
			sb.append('<');
			for (int i = 0; i < pt.getActualTypeArguments().length; i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(format(pt.getActualTypeArguments()[i]));
			}
			sb.append('>');
			return sb.toString();
		} else if (t instanceof Class<?>) {
			Class<?> c = (Class<?>) t;
			if (c.isArray()) {
				return format(c.getComponentType()) + "[]";
			}
			StringBuffer sb = new StringBuffer();
			while (c != null) {
				if (sb.length() != 0) {
					sb.insert(0, '.');
				}
				if (c.getEnclosingClass() == null) {
					disambiguationMap.put(c.getSimpleName(), c);
					if (disambiguationMap.get(c.getSimpleName()).size() > 1) {
						sb.insert(0, c.getName());
					} else {
						sb.insert(0, c.getSimpleName());
					}
				} else {
					sb.insert(0, c.getSimpleName());
				}
				c = c.getEnclosingClass();
			}
			return sb.toString();
		}
		return t.toString();
	}
}
