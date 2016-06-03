package org.andork.tracker.model;

import org.andork.collect.HashSetMultiMap;
import org.andork.collect.MultiMap;
import org.andork.tracker.Computation;
import org.andork.tracker.Dependency;
import org.andork.tracker.Tracker;

public class KeyedDependency<K> {
	final Dependency allDependency = new Dependency();
	final MultiMap<K, Computation> dependents = new HashSetMultiMap<>();

	public void changed() {
		allDependency.changed();
		for (Computation comp : dependents.values()) {
			comp.invalidate();
		}
	}

	public void changed(K key) {
		allDependency.changed();
		for (Computation comp : dependents.get(key)) {
			comp.invalidate();
		}
	}

	public boolean depend() {
		return allDependency.depend();
	}

	public boolean depend(K key) {
		return depend(key, Tracker.currentComputation());
	}

	public boolean depend(K key, Computation comp) {
		if (!Tracker.isActive()) {
			return false;
		}
		if (this.dependents.put(key, comp)) {
			comp.onInvalidate(() -> dependents.remove(key, comp));
			return true;
		}
		return false;
	}

	public boolean hasDependents(K key) {
		return !dependents.get(key).isEmpty();
	}
}
