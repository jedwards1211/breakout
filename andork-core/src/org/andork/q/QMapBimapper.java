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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.andork.func.Bimapper;

public class QMapBimapper<K, KM, V, VM> implements Bimapper<QMap<K, V, ?>, Object> {
	private static final Logger LOGGER = Logger.getLogger(QObjectMapBimapper.class.getName());

	public static <K, KM, V, VM> QMapBimapper<K, KM, V, VM> newInstance(Bimapper<K, KM> keyBimapper,
			Bimapper<V, VM> valueBimapper) {
		return new QMapBimapper<K, KM, V, VM>(keyBimapper, valueBimapper);
	}

	private Bimapper<K, KM> keyBimapper;

	private Bimapper<V, VM> valueBimapper;

	private QMapBimapper(Bimapper<K, KM> keyBimapper, Bimapper<V, VM> valueBimapper) {
		super();
		this.keyBimapper = keyBimapper;
		this.valueBimapper = valueBimapper;
	}

	@Override
	public Object map(QMap<K, V, ?> in) {
		if (in == null) {
			return null;
		}
		Map<KM, VM> result = new LinkedHashMap<KM, VM>();
		for (Map.Entry<K, V> entry : in.entrySet()) {
			result.put(keyBimapper == null ? (KM) entry.getKey() : keyBimapper.map(entry.getKey()),
					valueBimapper == null ? (VM) entry.getValue() : valueBimapper.map(entry.getValue()));
		}
		return result;
	}

	@Override
	public QMap<K, V, ?> unmap(Object out) {
		if (out == null) {
			return null;
		}

		Map<KM, VM> m = (Map<KM, VM>) out;
		QLinkedHashMap<K, V> result = QLinkedHashMap.newInstance();
		for (Map.Entry<KM, VM> entry : m.entrySet()) {
			try {
				K key = keyBimapper == null ? (K) entry.getKey() : keyBimapper.unmap(entry.getKey());
				V value = valueBimapper == null ? (V) entry.getValue() : valueBimapper.unmap(entry.getValue());
				result.put(key, value);
			} catch (Throwable t) {
				LOGGER.log(Level.WARNING, "Failed to add entry: " + entry, t);
			}
		}

		return result;
	}

}
