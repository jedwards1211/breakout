package org.andork.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.andork.generic.Factory;

public class MultiMaps {
	public static <K, V> MultiMap<K, V> unmodifiableMultiMap(final MultiMap<K, V> m) {
		return new MultiMap<K, V>() {
			public void clear() {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			public boolean contains(K key, V value) {
				return m.contains(key, value);
			}

			public boolean containsKey(K key) {
				return m.containsKey(key);
			}

			public Set<Entry<K, V>> entrySet() {
				return m.entrySet();
			}

			public V getOnlyValue(K key) {
				return m.getOnlyValue(key);
			}

			public Collection<V> get(K key) {
				return m.get(key);
			}

			public Set<K> keySet() {
				return m.keySet();
			}

			public boolean put(K key, V value) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			public boolean putAll(K key, Collection<? extends V> values) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			public boolean putAll(Map<? extends K, ? extends V> m) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			public <K2 extends K, V2 extends V> boolean putAll(MultiMap<K2, V2> m) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			public boolean remove(K key, V value) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			public boolean removeAll(K key, Collection<? extends V> values) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			public boolean removeAll(Map<? extends K, ? extends V> m) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			public <K2 extends K, V2 extends V> boolean removeAll(MultiMap<K2, V2> m) {
				throw new UnsupportedOperationException("unmodifiable MultiMap");
			}

			public Collection<V> values() {
				return m.values();
			}
		};
	}

	public static <K, V, C extends Collection<? super V>> int put(Map<K, C> m, K key, V value, Factory<? extends C> collectionFactory) {
		C c = m.get(key);
		if (c == null) {
			c = collectionFactory.newInstance();
			m.put(key, c);
		}
		c.add(value);
		return c.size();
	}

	public static <K, V, C extends Collection<? super V>> C get(Map<K, C> m, K key, C emptyCollection) {
		C c = m.get(key);
		return c == null ? emptyCollection : c;
	}
}