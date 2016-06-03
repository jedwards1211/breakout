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
import java.util.Set;

/**
 * A map where a single key can map to multiple values. Depending on the
 * subtype, duplicate values may or may not be permitted.
 *
 * Unlike {@link Map}, {@link #keySet()}, {@link #entrySet()}, {@link #values()}
 * , etc. are not guaranteed to be views; they may be unmodifiable or copies of
 * the underlying data.
 *
 * @author james.a.edwards
 *
 * @param <K>
 *            the key type.
 * @param <V>
 *            the value type.
 */
public interface MultiMap<K, V> {

	/**
	 * Removes all key-value pairs from this {@code MultiMap}.
	 */
	public abstract void clear();

	/**
	 * @param key
	 *            a key.
	 * @param value
	 *            a value.
	 * @return whether the given key-value pair exists in this {@code MultiMap}.
	 */
	public abstract boolean contains(K key, V value);

	/**
	 * @param key
	 *            a key.
	 * @return whether any values are associated with {@code key} in this
	 *         {@code MultiMap}.
	 */
	public abstract boolean containsKey(K key);

	/**
	 * @return a {@link Set} of all key-value pairs in this {@code MultiMap}.
	 *         This set is not necessarily modifiable or backed by the
	 *         {@code MultiMap}.
	 */
	public abstract Set<Map.Entry<K, V>> entrySet();

	/**
	 * @param key
	 *            the key to get values mapped to.
	 * @return all values mapped to {@code key}. This collection is not
	 *         necessarily modifiable or backed by the {@code MultiMap}.
	 */
	public abstract Collection<V> get(K key);

	/**
	 * @param key
	 *            the key to get the only value for.
	 * @return the only value mapped to {@code key}, or {@code null} if none
	 *         exists.
	 * @throws IllegalArgumentException
	 *             if there is more than one value mapped to {@code key}.
	 */
	public abstract V getOnlyValue(K key);

	/**
	 * @return a {@link Set} of all keys in this {@code MultiMap}. This set is
	 *         not necessarily modifiable or backed by the {@code MultiMap}.
	 */
	public abstract Set<K> keySet();

	/**
	 * Adds a key-value pair to this {@code MultiMap}.
	 *
	 * @param key
	 *            the key.
	 * @param value
	 *            the value to associate with {@code key}.
	 * @return {@code true} if this {@code MultiMap} was changed as a result of
	 *         the call.
	 */
	public abstract boolean put(K key, V value);

	/**
	 * Adds multiple key-value pairs to this {@code MultiMap}.
	 *
	 * @param key
	 *            the key.
	 * @param values
	 *            the values to associate with {@code key}.
	 * @return {@code true} if this {@code MultiMap} was changed as a result of
	 *         the call.
	 */
	public abstract boolean putAll(K key, Collection<? extends V> values);

	/**
	 * Adds multiple key-value pairs to this {@code MultiMap}.
	 *
	 * @param m
	 *            a {@link Map} of key-value pairs to add.
	 * @return {@code true} if this {@code MultiMap} was changed as a result of
	 *         the call.
	 */
	public abstract boolean putAll(Map<? extends K, ? extends V> m);

	/**
	 * Adds multiple key-value pairs to this {@code MultiMap}.
	 *
	 * @param m
	 *            a {@code MultiMap} of key-value pairs to add.
	 * @return {@code true} if this {@code MultiMap} was changed as a result of
	 *         the call.
	 */
	public abstract <K2 extends K, V2 extends V> boolean putAll(MultiMap<K2, V2> m);

	/**
	 * Removes a key-value pair from this {@code MultiMap}.
	 *
	 * @param key
	 *            the key.
	 * @param value
	 *            the value to dissociate from {@code key}.
	 * @return {@code true} if this {@code MultiMap} was changed as a result of
	 *         the call.
	 */
	public abstract boolean remove(K key, V value);

	/**
	 * Removes multiple key-value pairs from this {@code MultiMap}.
	 *
	 * @param key
	 *            the key.
	 * @param values
	 *            the values to dissociate from {@code key}.
	 * @return {@code true} if this {@code MultiMap} was changed as a result of
	 *         the call.
	 */
	public abstract boolean removeAll(K key, Collection<? extends V> values);

	/**
	 * Removes multiple key-value pairs from this {@code MultiMap}.
	 *
	 * @param m
	 *            a {@link Map} of key-value pairs to remove from this
	 *            {@code MultiMap}.
	 * @return {@code true} if this {@code MultiMap} was changed as a result of
	 *         the call.
	 */
	public abstract boolean removeAll(Map<? extends K, ? extends V> m);

	/**
	 * Removes multiple key-value pairs from this {@code MultiMap}.
	 *
	 * @param m
	 *            a {@code MultiMap} of key-value pairs to remove from this
	 *            {@code MultiMap}.
	 * @return {@code true} if this {@code MultiMap} was changed as a result of
	 *         the call.
	 */
	public abstract <K2 extends K, V2 extends V> boolean removeAll(MultiMap<K2, V2> m);

	/**
	 * @return all values for all keys. This collection is not necessarily
	 *         modifiable or backed by the {@code MultiMap}.
	 */
	public abstract Collection<V> values();
}
