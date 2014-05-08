package org.andork.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class InvertibleHashSetMultiMap<K, V> implements InvertibleSetMultiMap<K, V> {
	private final HashSetMultiMap<K, V>	kv;
	private final HashSetMultiMap<V, K>	vk;

	public InvertibleHashSetMultiMap() {
		kv = new HashSetMultiMap<K, V>();
		vk = new HashSetMultiMap<V, K>();
	}
	
	public static <K, V> InvertibleHashSetMultiMap<K, V> newInstance() {
		return new InvertibleHashSetMultiMap<K, V>();
	}

	private InvertibleHashSetMultiMap(HashSetMultiMap<K, V> kv, HashSetMultiMap<V, K> vk) {
		this.kv = kv;
		this.vk = vk;
	}

	public InvertibleHashSetMultiMap<V, K> inverse() {
		return new InvertibleHashSetMultiMap<V, K>(vk, kv);
	}

	public boolean put(K key, V value) {
		return kv.put(key, value) && vk.put(value, key);
	}

	public boolean putAll(K key, Collection<? extends V> values) {
		boolean result = false;
		for (V value : values) {
			result |= put(key, value);
		}
		return result;
	}

	public boolean putAll(Map<? extends K, ? extends V> m) {
		boolean result = false;
		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			result |= put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public <K2 extends K, V2 extends V> boolean putAll(MultiMap<K2, V2> m) {
		boolean result = false;
		for (K2 key : m.keySet()) {
			result |= putAll(key, m.get(key));
		}
		return result;
	}

	public boolean remove(K key, V value) {
		return kv.remove(key, value) && vk.remove(value, key);
	}


	public boolean removeAll(K key, Collection<? extends V> values) {
		boolean result = false;
		for (V value : values) {
			result |= remove(key, value);
		}
		return result;
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
		return kv.entrySet();
	}
	
	public Set<V> values() {
		return vk.keySet();
	}

	public Set<V> get(K key) {
		return kv.get(key);
	}

	public V getOnlyValue(K key) {
		return kv.getOnlyValue(key);
	}

	public boolean containsKey(K key) {
		return kv.containsKey(key);
	}

	public boolean contains(K key, V value) {
		return kv.contains(key, value);
	}

	public void clear() {
		kv.clear();
		vk.clear();
	}
}
