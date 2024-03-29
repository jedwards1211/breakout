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
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class InvertibleHashSetMultiMap<K, V> implements InvertibleSetMultiMap<K, V> {
	public static <K, V> InvertibleHashSetMultiMap<K, V> newInstance() {
		return new InvertibleHashSetMultiMap<K, V>();
	}

	private final HashSetMultiMap<K, V> kv;

	private final HashSetMultiMap<V, K> vk;

	public InvertibleHashSetMultiMap() {
		kv = new HashSetMultiMap<K, V>();
		vk = new HashSetMultiMap<V, K>();
	}

	private InvertibleHashSetMultiMap(HashSetMultiMap<K, V> kv, HashSetMultiMap<V, K> vk) {
		this.kv = kv;
		this.vk = vk;
	}

	@Override
	public void clear() {
		kv.clear();
		vk.clear();
	}

	@Override
	public boolean contains(K key, V value) {
		return kv.contains(key, value);
	}

	@Override
	public boolean containsKey(K key) {
		return kv.containsKey(key);
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return kv.entrySet();
	}

	@Override
	public Set<V> get(K key) {
		return kv.get(key);
	}

	@Override
	public V getOnlyValue(K key) {
		return kv.getOnlyValue(key);
	}

	@Override
	public InvertibleHashSetMultiMap<V, K> inverse() {
		return new InvertibleHashSetMultiMap<V, K>(vk, kv);
	}

	@Override
	public Set<K> keySet() {
		return Collections.unmodifiableSet(kv.keySet());
	}

	@Override
	public boolean put(K key, V value) {
		return kv.put(key, value) && vk.put(value, key);
	}

	@Override
	public boolean putAll(K key, Collection<? extends V> values) {
		boolean result = false;
		for (V value : values) {
			result |= put(key, value);
		}
		return result;
	}

	@Override
	public boolean putAll(Map<? extends K, ? extends V> m) {
		boolean result = false;
		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			result |= put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	@Override
	public <K2 extends K, V2 extends V> boolean putAll(MultiMap<K2, V2> m) {
		boolean result = false;
		for (K2 key : m.keySet()) {
			result |= putAll(key, m.get(key));
		}
		return result;
	}

	@Override
	public boolean remove(K key, V value) {
		return kv.remove(key, value) && vk.remove(value, key);
	}

	@Override
	public boolean removeAll(K key, Collection<? extends V> values) {
		boolean result = false;
		for (V value : values) {
			result |= remove(key, value);
		}
		return result;
	}

	@Override
	public boolean removeAll(Map<? extends K, ? extends V> m) {
		boolean result = false;
		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			result |= remove(entry.getKey(), entry.getValue());
		}
		return result;
	}

	@Override
	public <K2 extends K, V2 extends V> boolean removeAll(MultiMap<K2, V2> m) {
		boolean result = false;
		for (K2 key : m.keySet()) {
			result |= removeAll(key, m.get(key));
		}
		return result;
	}

	@Override
	public Set<V> values() {
		return vk.keySet();
	}
}
