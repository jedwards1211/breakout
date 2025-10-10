package org.andork.q;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QDependency {
	private final Set<QAutorun> dependents = new HashSet<>();

	public void depend() {
		QAutorun dependent = QAutorun.depend(this);
		if (dependent != null) {
			dependents.add(dependent);
		}
	}

	public void remove(QAutorun dependent) {
		dependents.remove(dependent);
	}

	public void fireChanged() {
		List<QAutorun> dependents = new ArrayList<>(this.dependents);
		this.dependents.clear();
		for (QAutorun dependent : dependents) {
			dependent.run();
		}
	}
}
