package org.andork.q;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class QAutorun implements AutoCloseable {
	private static final ThreadLocal<QAutorun> currentAutorun = new ThreadLocal<>();

	private final WeakReference<Thread> thread;
	private final Runnable runner;
	private final Set<QDependency> dependencies = new HashSet<>();

	QAutorun(Runnable runner) {
		this.runner = runner;
		this.thread = new WeakReference<>(Thread.currentThread());
		this.run();
	}

	public static QAutorun autorun(Runnable runner) {
		return new QAutorun(runner);
	}

	static QAutorun depend(QDependency dependency) {
		QAutorun current = getCurrent();
		if (current == null) {
			return null;
		}
		if (current.thread.get() != Thread.currentThread()) {
			throw new RuntimeException("depend called on a different thread from autorun!");
		}
		if (current.dependencies.add(dependency)) {
			return current;
		}
		return null;
	}

	public static QAutorun getCurrent() {
		return currentAutorun.get();
	}

	public void run() {
		if (currentAutorun.get() != null) {
			throw new RuntimeException("an " + QAutorun.class.getName() + " is already running on this thread");
		}
		if (thread.get() != Thread.currentThread()) {
			throw new RuntimeException("run not called on the original thread!");
		}
		try {
			dependencies.clear();
			currentAutorun.set(this);
			runner.run();
		} finally {
			currentAutorun.set(null);
		}
	}

	public void close() {
		for (QDependency dependency : dependencies) {
			dependency.remove(this);
		}
		dependencies.clear();
	}
}
