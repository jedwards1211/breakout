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

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

public class MultiMaps {
	public static <K, V, C extends Collection<? super V>> C get(Map<K, C> m, K key, C emptyCollection) {
		C c = m.get(key);
		return c == null ? emptyCollection : c;
	}

	public static <K, V, C extends Collection<? super V>> int
		put(Map<K, C> m, K key, V value, Supplier<? extends C> collectionFactory) {
		C c = m.get(key);
		if (c == null) {
			c = collectionFactory.get();
			m.put(key, c);
		}
		c.add(value);
		return c.size();
	}

	public static <K, V> MultiMap<K, V> unmodifiableMultiMap(final MultiMap<K, V> m) {
		return new MultiMap<K, V>() {
			@Override
			public void clear() {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			@Override
			public boolean contains(K key, V value) {
				return m.contains(key, value);
			}

			@Override
			public boolean containsKey(K key) {
				return m.containsKey(key);
			}

			@Override
			public Set<Entry<K, V>> entrySet() {
				return m.entrySet();
			}

			@Override
			public Collection<V> get(K key) {
				return m.get(key);
			}

			@Override
			public V getOnlyValue(K key) {
				return m.getOnlyValue(key);
			}

			@Override
			public Set<K> keySet() {
				return m.keySet();
			}

			@Override
			public boolean put(K key, V value) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			@Override
			public boolean putAll(K key, Collection<? extends V> values) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			@Override
			public boolean putAll(Map<? extends K, ? extends V> m) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			@Override
			public <K2 extends K, V2 extends V> boolean putAll(MultiMap<K2, V2> m) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			@Override
			public boolean remove(K key, V value) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			@Override
			public boolean removeAll(K key, Collection<? extends V> values) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			@Override
			public boolean removeAll(Map<? extends K, ? extends V> m) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			@Override
			public <K2 extends K, V2 extends V> boolean removeAll(MultiMap<K2, V2> m) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			@Override
			public Collection<V> values() {
				return m.values();
			}
		};
	}

	@SuppressWarnings("rawtypes")
	private static final MultiMap emptyMultiMap = unmodifiableMultiMap(new HashSetMultiMap<>());

	@SuppressWarnings("unchecked")
	public static <K, V> MultiMap<K, V> emptyMultiMap() {
		return emptyMultiMap;
	}
}
