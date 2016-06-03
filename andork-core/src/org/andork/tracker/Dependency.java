package org.andork.tracker;

import java.util.HashSet;
import java.util.Set;

public class Dependency {
	final Set<Computation> dependents = new HashSet<>();

	public void changed() {
		for (Computation comp : dependents) {
			comp.invalidate();
		}
	}

	public boolean depend() {
		return depend(Tracker.currentComputation());
	}

	public boolean depend(Computation comp) {
		if (!Tracker.isActive()) {
			return false;
		}
		if (dependents.add(comp)) {
			comp.onInvalidate(() -> dependents.remove(comp));
			return true;
		}
		return false;
	}

	public boolean hasDependents() {
		return !dependents.isEmpty();
	}
}
