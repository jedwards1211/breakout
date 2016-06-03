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

import java.util.List;

/**
 * A {@link MultiMap} that stores all values for the same key in a {@link List}
 * in the order they were added, and allows duplicate values.
 *
 * @author james.a.edwards
 *
 * @param <K>
 *            the key type.
 * @param <V>
 *            the value type.
 */
public interface ListMultiMap<K, V> extends MultiMap<K, V> {
	/**
	 * @param key
	 *            the key to get values mapped to.
	 * @return all values mapped to {@code key}. This list is not necessarily
	 *         modifiable or backed by the {@code ListMultiMap}.
	 */
	@Override
	public abstract List<V> get(K key);
}
