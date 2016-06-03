package org.andork.tracker;

import java.util.ArrayList;
import java.util.List;

public class Computation implements Runnable {
	Tracker tracker;
	ComputeFunction func;
	boolean stopped = false;
	boolean invalidated = false;
	boolean firstRun = true;

	boolean recomputing = false;

	final List<Runnable> onInvalidateCallbacks = new ArrayList<Runnable>();
	final List<Runnable> onStopCallbacks = new ArrayList<Runnable>();

	Computation(Tracker tracker, ComputeFunction r) {
		this.tracker = tracker;
		func = r;
	}
	
	void start() throws Exception {
		boolean errored = true;
		try {
			compute();
			errored = false;
		} finally {
			firstRun = false;
			if (errored) {
				stop();
			}
		}
	}

	public void invalidate() {
		if (invalidated) {
		    // if we're currently in _recompute(), don't enqueue                                                              // 271
		    // ourselves, since we'll rerun immediately anyway.  
			return;
		}
		invalidated = true;
		if (!recomputing && !stopped) {
			tracker.requireFlush();
			tracker.addPendingComputation(this);
		}
	    // callbacks can't add callbacks, because                                                                         // 280
	    // self.invalidated === true.
		for (Runnable callback : onInvalidateCallbacks) {
			tracker.nonreactive(callback);
		}
		onInvalidateCallbacks.clear();
	}

	public void stop() {
		if (stopped) {
			return;
		}
		stopped = true;
		invalidate();
		for (Runnable callback : onStopCallbacks) {
			tracker.nonreactive(callback);
		}
		onStopCallbacks.clear();
	}

	public void onInvalidate(Runnable r) {
		if (invalidated) {
			tracker.nonreactive(r);
		} else {
			onInvalidateCallbacks.add(r);
		}
	}

	public void onStop(Runnable r) {
		if (stopped) {
			tracker.nonreactive(r);
		} else {
			onStopCallbacks.add(r);
		}
	}

	void compute() throws Exception {
		invalidated = false;
		Computation previous = Tracker.currentComputation();
		Tracker.setCurrentComputation(this);

		boolean previousInCompute = tracker.inCompute();
		tracker.setInCompute(true);
		try {
			func.run(this);
		} finally {
			Tracker.setCurrentComputation(previous);
			tracker.setInCompute(previousInCompute);
		}
	}

	boolean needsRecompute() {
		return invalidated && !stopped;
	}

	void recompute() {
		recomputing = true;
		try {
			if (needsRecompute()) {
				try {
					compute();
				} catch (Exception e) {
					tracker.throwOrLog("recompute", e);
				}
			}
		} finally {
			recomputing = false;
		}
	}

	public void flush() {
		if (recomputing) {
			return;
		}
		recompute();
	}

	@Override
	public void run() {
		invalidate();
		flush();
	}
}
