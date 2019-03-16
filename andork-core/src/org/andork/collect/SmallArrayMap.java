package org.andork.collect;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SmallArrayMap<K, V> extends AbstractMap<K, V> {
	private SmallArraySet<Entry<K, V>> entrySet;

	public SmallArrayMap(int capacity) {
		entrySet = new SmallArraySet<Map.Entry<K,V>>(capacity);
	}
	
	@Override
	public V put(K key, V value) {
		for (Entry<K, V> entry : entrySet) {
			if (Objects.equals(key, entry.getKey())) {
				return entry.setValue(value);
			}
		}
		entrySet.add(new SmallArrayEntry(key, value));
		return null;
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return entrySet;
	}
	
	private class SmallArrayEntry implements Map.Entry<K, V> {
		private K key;
		private V value;
		
		public SmallArrayEntry(K key, V value) {
			super();
			this.key = key;
			this.value = value;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(key);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Entry)) return false;
			return Objects.equals(key, ((Entry<K, V>) obj).getKey());
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
			V prevValue = this.value;
			this.value = value;
			return prevValue;
		}
		
	}
}
