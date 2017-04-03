package org.andork.collect;

import java.util.Map;

public class PriorityEntry<K extends Comparable<K>, V> implements Map.Entry<K, V>, Comparable<PriorityEntry<K, V>> {
	private final K key;
	private V value;

	public PriorityEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public int compareTo(PriorityEntry<K, V> o) {
		return key.compareTo(o.key);
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		V oldValue = this.value;
		this.value = value;
		return oldValue;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PriorityEntry other = (PriorityEntry) obj;
		return key.equals(other.key) && value.equals(other.value);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PriorityEntry [key=").append(key).append(", value=").append(value).append("]");
		return builder.toString();
	}
}
