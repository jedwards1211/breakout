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
package org.andork.collect;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A {@link SetMultiMap} that stores all values for the same key in a
 * {@link LinkedHashSet}. In the future, it may preserve the order in which
 * <i>all</i> key-value pairs were added; currently, it just preserves addition
 * order among pairs with the same key.
 *
 * @author james.a.edwards
 *
 * @param <K>
 *            the key type.
 * @param <V>
 *            the value type.
 */
public class LinkedHashSetMultiMap<K, V> extends AbstractSetMultiMap<K, V> implements SetMultiMap<K, V> {
	public static <K, V> LinkedHashSetMultiMap<K, V> newInstance() {
		return new LinkedHashSetMultiMap<K, V>();
	}

	@Override
	protected Map<K, Set<V>> createKeyMap() {
		return new LinkedHashMap<K, Set<V>>();
	}

	@Override
	protected Set<V> createValueSet() {
		return new LinkedHashSet<V>();
	}
}
