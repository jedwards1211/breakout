package org.andork.tracker;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Tracker {
	public static class FlushOptions {
		public boolean finishSynchronously;

		public boolean throwFirstError;

		public FlushOptions() {

		}

		public FlushOptions finishSynchronously(boolean val) {
			finishSynchronously = val;
			return this;
		}

		public FlushOptions throwFirstError(boolean val) {
			throwFirstError = val;
			return this;
		}
	}

	public static interface Runner {
		void checkThread();

		void setTimeout(Runnable r, int delay);
	}

	public static Tracker EDT = new Tracker(new Runner() {
		@Override
		public void checkThread() {
			if (!SwingUtilities.isEventDispatchThread()) {
				throw new TrackerException("Must be called from EDT");
			}
		}

		@Override
		public void setTimeout(Runnable r, int delay) {
			if (delay <= 0) {
				SwingUtilities.invokeLater(r);
			} else {
				Timer timer = new Timer(delay, e -> r.run());
				timer.setRepeats(false);
				timer.start();
			}
		}
	});

	static final ThreadLocal<Computation> currentComputation = new ThreadLocal<>();

	public static Computation currentComputation() {
		return currentComputation.get();
	}

	public static boolean isActive() {
		return currentComputation.get() != null;
	}

	static void setCurrentComputation(Computation comp) {
		currentComputation.set(comp);
	}

	final Runner runner;
	final List<Computation> pendingComputations = new ArrayList<>();
	final List<Runnable> afterFlushCallbacks = new ArrayList<>();
	boolean inCompute = false;

	boolean willFlush = false;

	boolean inFlush = false;

	boolean throwFirstError = false;

	Tracker(Runner runner) {
		this.runner = runner;
	}

	void addPendingComputation(Computation comp) {
		pendingComputations.add(comp);
	}

	public void afterFlush(Runnable r) {
		runner.checkThread();

		afterFlushCallbacks.add(r);
		requireFlush();
	}

	public Computation autorun(ComputeFunction r) throws Exception {
		runner.checkThread();

		Computation comp = new Computation(this, r);
		comp.start();

		if (isActive()) {
			onInvalidate(() -> comp.stop());
		}
		return comp;
	}

	public Computation autorun(Runnable r) {
		try {
			return autorun(comp -> r.run());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void flush() {
		flush(null);
	}

	public void flush(FlushOptions options) {
		runFlush(new FlushOptions().finishSynchronously(true)
				.throwFirstError(options == null ? false : options.throwFirstError));
	}

	boolean inCompute() {
		return inCompute;
	}

	public void nonreactive(Runnable r) {
		runner.checkThread();

		Computation previous = currentComputation();
		setCurrentComputation(null);
		try {
			r.run();
		} finally {
			setCurrentComputation(previous);
		}
	}

	public void onInvalidate(Runnable r) {
		if (!isActive()) {
			throw new Error("Tracker.onInvalidate requires a currentComputation");
		}
		currentComputation().onInvalidate(r);
	}

	void requireFlush() {
		runner.checkThread();

		if (!willFlush) {
			runner.setTimeout(this::runFlush, 0);
			willFlush = true;
		}
	}

	void runFlush() {
		runFlush(null);
	}

	void runFlush(FlushOptions options) {
		runner.checkThread();

		if (inFlush) {
			throw new TrackerException("Can't call Tracker.flush while flushing");
		}
		if (inCompute) {
			throw new TrackerException("Can't flush inside Tracker.autorun");
		}

		if (options == null) {
			options = new FlushOptions();
		}

		inFlush = true;
		willFlush = true;
		throwFirstError = options.throwFirstError;

		int recomputedCount = 0;
		boolean finishedTry = false;
		try {
			while (!pendingComputations.isEmpty() || !afterFlushCallbacks.isEmpty()) {
				// recompute all pending computations
				while (!pendingComputations.isEmpty()) {
					Computation comp = pendingComputations.remove(0);
					comp.recompute();
					if (comp.needsRecompute()) {
						pendingComputations.add(0, comp);
					}
					if (!options.finishSynchronously && ++recomputedCount > 1000) {
						finishedTry = true;
						return;
					}
				}

				if (!afterFlushCallbacks.isEmpty()) {
					// call one afterFlush callback, which may
					// invalidate more computations
					Runnable func = afterFlushCallbacks.remove(0);
					try {
						func.run();
					} catch (Exception e) {
						throwOrLog("afterFlush", e);
					}
				}
			}
			finishedTry = true;
		} finally {
			if (!finishedTry) {
				// we're erroring due to throwFirstError being true. // 506
				inFlush = false; // needed before calling `Tracker.flush()`
									// again
				// finish flushing
				runFlush(new FlushOptions().finishSynchronously(options.finishSynchronously).throwFirstError(false));
			}
			willFlush = false;
			inFlush = false;
			if (!pendingComputations.isEmpty() || !afterFlushCallbacks.isEmpty()) {
				// We're yielding because we ran a bunch of computations and we
				// aren't required to finish synchronously, so we'd like to give
				// the event loop a chance. We should flush again soon.
				if (options.finishSynchronously) {
					throw new TrackerException("still have work to do?");
				}
				runner.setTimeout(this::requireFlush, 10);
			}
		}
	}

	void setInCompute(boolean inCompute) {
		this.inCompute = inCompute;
	}

	void throwOrLog(String from, Exception e) {
		if (throwFirstError) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new TrackerException(from, e);
		}
		e.printStackTrace();
	}
}
