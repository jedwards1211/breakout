package org.andork.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractSetMultiMap<K, V> implements SetMultiMap<K, V> {
	protected final Map<K, Set<V>>	kv;

	public AbstractSetMultiMap() {
		kv = createKeyMap();
	}

	protected abstract Map<K, Set<V>> createKeyMap();

	protected abstract Set<V> createValueSet();

	public boolean put(K key, V value) {
		Set<V> values = kv.get(key);

		if (values == null) {
			values = createValueSet();
			kv.put(key, values);
		}
		return values.add(value);
	}

	public boolean putAll(Map<? extends K, ? extends V> m) {
		boolean result = false;
		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			result |= put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public boolean putAll(K key, Collection<? extends V> values) {
		Set<V> m_values = kv.get(key);

		if (m_values == null) {
			m_values = createValueSet();
			kv.put(key, m_values);
		}
		return m_values.addAll(values);
	}

	public <K2 extends K, V2 extends V> boolean putAll(MultiMap<K2, V2> m) {
		boolean result = false;
		for (K2 key : m.keySet()) {
			result |= putAll(key, m.get(key));
		}
		return result;
	}

	public boolean remove(K key, V value) {
		Set<V> values = kv.get(key);

		if (values != null) {
			boolean result = values.remove(value);
			if (values.isEmpty()) {
				kv.remove(key);
			}
			return result;
		}
		return false;
	}

	public boolean removeAll(K key, Collection<? extends V> values) {
		Set<V> m_values = kv.get(key);

		if (m_values != null) {
			boolean result = m_values.removeAll(values);
			if (m_values.isEmpty()) {
				kv.remove(key);
			}
			return result;
		}
		return false;
	}

	public boolean removeAll(Map<? extends K, ? extends V> m) {
		boolean result = false;
		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			result |= remove(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public <K2 extends K, V2 extends V> boolean removeAll(MultiMap<K2, V2> m) {
		boolean result = false;
		for (K2 key : m.keySet()) {
			result |= removeAll(key, m.get(key));
		}
		return result;
	}

	public Set<K> keySet() {
		return Collections.unmodifiableSet(kv.keySet());
	}

	public Set<Map.Entry<K, V>> entrySet() {
		Set<Map.Entry<K, V>> result = new LinkedHashSet<Map.Entry<K,V>>();
		for (Map.Entry<K, Set<V>> entry : kv.entrySet()) {
			for (V value : entry.getValue()) {
				result.add(new Entry(entry.getKey(), value));
			}
		}
		return result;
	}

	public Collection<V> values() {
		Set<V> result = createValueSet();
		for (Set<V> values : kv.values()) {
			result.addAll(values);
		}
		return result;
	}

	public Set<V> get(K key) {
		Set<V> result = kv.get(key);
		return result == null ? createValueSet() : Collections.unmodifiableSet(result);
	}

	public V getOnlyValue(K key) {
		Set<V> result = kv.get(key);
		if (result == null || result.size() == 0) {
			return null;
		}
		if (result.size() > 1) {
			throw new IllegalArgumentException("must have more than one value for key: " + key);
		}
		return result.iterator().next();
	}

	public boolean containsKey(K key) {
		return kv.containsKey(key);
	}

	public boolean contains(K key, V value) {
		return get(key).contains(value);
	}

	public void clear() {
		kv.clear();
	}

	class Entry implements Map.Entry<K, V> {
		private K	key;
		private V	value;

		public Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(V value) {
			V oldValue = this.value;
			remove(this.key, this.value);
			this.value = value;
			put(this.key, this.value);
			return oldValue;
		}

		public int hashCode() {
			return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
		}

		public String toString() {
			return getKey() + "=" + getValue();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Map.Entry))
				return false;
			Map.Entry<?, ?> e = (Map.Entry<?, ?>) obj;
			Object k1 = getKey();
			Object k2 = e.getKey();
			if (k1 == k2 || (k1 != null && k1.equals(k2))) {
				Object v1 = getValue();
				Object v2 = e.getValue();
				if (v1 == v2 || (v1 != null && v1.equals(v2)))
					return true;
			}
			return false;
		}
	}
}