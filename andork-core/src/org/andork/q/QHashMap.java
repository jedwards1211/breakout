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

import java.util.HashMap;
import java.util.Map;

import org.andork.func.Mapper;

public class QHashMap<K, V> extends QMap<K, V, HashMap<K, V>> {
	public static <K, V> QHashMap<K, V> newInstance() {
		return new QHashMap<K, V>();
	}

	@Override
	protected HashMap<K, V> createMap() {
		return new HashMap<K, V>();
	}

	@Override
	public QElement deepClone(Mapper<Object, Object> childMapper) {
		QHashMap<K, V> result = newInstance();
		for (Map.Entry<K, V> entry : entrySet()) {
			result.put(entry.getKey(), (V) childMapper.map(entry.getValue()));
		}
		return result;
	}
}
