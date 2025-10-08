package org.andork.q;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.SwingUtilities;

public class QAutorun {
	private static final ThreadLocal<QAutorun> currentAutorun = new ThreadLocal<>();
	private final List<Runnable> cleanup = new ArrayList<>();

	private volatile boolean enqueued = false;
	private final Consumer<Runnable> enqueue;
	private final Runnable run;

	QAutorun(Runnable run, Consumer<Runnable> enqueue) {
		this.run = run;
		this.enqueue = enqueue;
		this.enqueueRun();
	}

	public static QAutorun autorunOnEDT(Runnable run) {
		return new QAutorun(run, SwingUtilities::invokeLater);
	}

	public static void depend(Function<Runnable, Runnable> subscribe) {
		QAutorun current = currentAutorun.get();
		if (current == null)
			return;

		Runnable unsubscribe = subscribe.apply(current::enqueueRun);
		current.cleanup.add(() -> unsubscribe.run());
	}

	protected void enqueueRun() {
		if (enqueued) {
			return;
		}
		enqueued = true;
		for (Runnable r : cleanup) {
			r.run();
		}
		cleanup.clear();
		enqueue.accept(this::run);
	}

	protected void run() {
		if (currentAutorun.get() != null) {
			throw new RuntimeException("another " + QAutorun.class.getName() + " is already running on this thread");
		}
		this.enqueued = false;
		currentAutorun.set(this);

		try {
			run.run();
		} finally {
			currentAutorun.set(null);
		}
	}
}
