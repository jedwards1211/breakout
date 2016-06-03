package org.andork.tracker.model;

import java.util.Map;
import java.util.Objects;

import org.andork.tracker.Dependency;

public class TrackableMap<K, V> {
	final Map<K, V> m;
	final Dependency sizeDep = new Dependency();
	final KeyedDependency<K> entryDeps = new KeyedDependency<>();

	public TrackableMap(Map<K, V> map) {
		m = Objects.requireNonNull(map);
	}

	public void clear() {
		if (m.size() > 0) {
			sizeDep.changed();
		}
		m.clear();
	}

	public boolean containsKey(K key) {
		entryDeps.depend(key);
		return m.containsKey(key);
	}

	@Override
	public boolean equals(Object o) {
		entryDeps.depend();
		return m.equals(o);
	}

	public V get(K key) {
		entryDeps.depend(key);
		return m.get(key);
	}

	@Override
	public int hashCode() {
		entryDeps.depend();
		return m.hashCode();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public void put(K key, V value) {
		int oldSize = m.size();
		if (m.put(key, value) != value) {
			entryDeps.changed(key);
		}
		if (m.size() != oldSize) {
			sizeDep.changed();
		}
	}

	public void putAll(Map<K, V> map) {
		int oldSize = m.size();
		for (Map.Entry<K, V> entry : map.entrySet()) {
			if (m.put(entry.getKey(), entry.getValue()) != entry.getValue()) {
				entryDeps.changed(entry.getKey());
			}
		}
		if (m.size() != oldSize) {
			sizeDep.changed();
		}
	}

	public void remove(K key) {
		int oldSize = m.size();
		m.remove(key);
		if (oldSize != m.size()) {
			entryDeps.changed(key);
			sizeDep.changed();
		}
	}

	public void remove(K key, V value) {
		if (m.remove(key, value)) {
			entryDeps.changed(key);
			sizeDep.changed();
		}
	}

	public int size() {
		sizeDep.depend();
		return m.size();
	}
}
