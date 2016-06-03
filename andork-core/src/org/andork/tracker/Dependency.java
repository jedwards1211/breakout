package org.andork.tracker;

import java.util.HashSet;
import java.util.Set;

public class Dependency {
	final Set<Computation> dependents = new HashSet<>();
	
	public boolean depend() {
		return depend(Tracker.currentComputation());
	}

	public boolean depend(Computation comp) {
		if (!Tracker.isActive()) {
			return false;
		}
		if (this.dependents.add(comp)) {
			comp.onInvalidate(() -> dependents.remove(comp));
			return true;
		}
		return false;
	}
	public void changed() {
		for (Computation comp : dependents) {
			comp.invalidate();
		}
	}
	public boolean hasDependents() {
		return !dependents.isEmpty();
	}
}
