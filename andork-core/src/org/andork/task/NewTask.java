package org.andork.task;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.andork.func.Lodash;
import org.andork.func.Lodash.DebounceOptions;
import org.andork.func.Lodash.DebouncedRunnable;
import org.andork.util.StringUtils;

/**
 * A task that performs some computation (typically on a background thread) and
 * can notify listeners when its status or progress changes. It may run subtasks
 * within itself, so the progress and status message can be hierarchical.
 *
 * @author Andy Edwards
 *
 * @param <R>
 *            the task result type.
 */
public abstract class NewTask<R> implements Callable<R> {
	public static final ThreadLocal<DebounceOptions<Void>> debounceOptions = new ThreadLocal<>();

	private volatile Thread thread;
	private volatile DebouncedRunnable fireChanged = DebouncedRunnable.noop();
	private volatile NewTask<?> parent;
	private volatile NewTask<?> subtask;

	private volatile int subtaskProportion;
	private final List<ChangeListener> listeners = new CopyOnWriteArrayList<>();
	private volatile String status;

	private volatile boolean indeterminate;
	private volatile int completed;
	private volatile int total;

	private volatile boolean canceled;

	/**
	 * Runs this task. It must not be running before this call is made.
	 *
	 * @return the result of calling {@link #work()}.
	 * @throws IllegalStateException
	 *             if this task is already running.
	 * @throws Exception
	 *             if {@link #work()} threw it
	 */
	@Override
	public final R call() throws Exception {
		start();
		try {
			return work();
		} finally {
			stop();
		}
	}

	/**
	 * {@link #call() Call}s the given subtask, and increments the progress by
	 * {@code proportion} once it finishes.
	 * 
	 * @param proportion
	 *            a number > 0
	 * @param subtask
	 *            the subtask to {@link #call()}
	 * @return the return value of {@code subtask.call()}
	 * 
	 * @throws IllegalArgumentException
	 *             if {@code proportion} is <= 0
	 * @throws IllegalStateException
	 *             if any of the following apply:
	 *             <ul>
	 *             <li>this task is not currently running
	 *             <li>this method is called from a different thread than this
	 *             task is running on
	 *             <li>there is currently a subtask running
	 *             <li>the given subtask is currently running
	 *             </ul>
	 * @throws Exception
	 *             if {@code subtask.call()} threw an exception
	 */
	public final <R2> R2 callSubtask(int proportion, NewTask<R2> subtask) throws Exception {
		if (proportion <= 0) {
			throw new IllegalArgumentException("proportion must be > 0");
		}
		setSubtask(proportion, subtask);

		try {
			return subtask.call();
		} finally {
			clearSubtask();
		}
	}

	/**
	 * Cancels this task and its {@link #getParent() parent} task (if any).
	 * After this {@link #isCanceled()} will return true until {@link #reset()}
	 * is called. This does not interrupt the task; the {@link #work()}
	 * implementation should periodically check {@link #isCanceled()} and abort
	 * if it returns {@code true}.
	 */
	public void cancel() {
		NewTask<?> parent = this.parent;
		if (parent != null) {
			parent.cancel();
		} else {
			fireChanged.run();
		}
	}

	/**
	 * Called by {@link #run()} (which safely sets the state of this task before
	 * and after).
	 */
	public abstract R work() throws Exception;

	/**
	 * @return the combined progress of this task and its subtasks. {@code NaN}
	 *         means progress is indeterminate.
	 */
	public double getCombinedProgress() {
		if (indeterminate)
			return Double.NaN;
		NewTask<?> subtask = this.subtask;
		return subtask != null
				? (completed + subtaskProportion * subtask.getCombinedProgress()) / total
				: getProgress();
	}

	/**
	 * @return the combined status of this task and its subtasks.
	 */
	public String getCombinedStatus() {
		NewTask<?> subtask = this.subtask;
		String status = this.status;
		if (subtask != null) {
			String subtaskStatus = subtask.getCombinedStatus();
			if (!StringUtils.isNullOrEmpty(subtaskStatus)) {
				if (status == null) {
					return subtaskStatus;
				}

				return status + ": " + subtaskStatus;
			}
		}
		if (status == null) {
			return "";
		}
		return status + "...";
	}

	public NewTask<?> getDeepestSubtask() {
		NewTask<?> subtask = this.subtask;
		return subtask == null ? this : subtask.getDeepestSubtask();
	}

	public double getDeepestSubtaskProgress() {
		return getDeepestSubtask().getProgress();
	}

	public String getDeepestSubtaskStatus() {
		return getDeepestSubtask().getStatus();
	}

	public NewTask<?> getParent() {
		return parent;
	}

	/**
	 * @return the progress. {@code NaN} means progress is indeterminate.
	 */
	public double getProgress() {
		return indeterminate ? Double.NaN : (double) completed / total;
	}

	public String getStatus() {
		return status;
	}

	public NewTask<?> getSubtask() {
		return subtask;
	}

	public boolean isCanceled() {
		NewTask<?> parent = this.parent;
		return canceled || (parent != null && parent.isCanceled());
	}

	public boolean isRunning() {
		return thread != null;
	}

	public void addChangeListener(ChangeListener listener) {
		listeners.add(listener);
	}

	public void removeChangeListener(ChangeListener listener) {
		listeners.remove(listener);
	}

	public void setCompleted(int completed) {
		this.completed = completed;
		fireChanged.run();
	}

	public void increment() {
		this.increment(1);
	}

	public void increment(int amount) {
		this.completed += amount;
		fireChanged.run();
	}

	public int getCompleted() {
		return completed;
	}

	public int getTotal() {
		return total;
	}

	public boolean isIndeterminate() {
		return indeterminate;
	}

	public void setTotal(int total) {
		this.total = total;
		fireChanged.run();
	}

	public void setStatus(String newStatus) {
		status = newStatus;
		fireChanged.run();
	}

	public void setIndeterminate(boolean indeterminate) {
		this.indeterminate = indeterminate;
		fireChanged.run();
	}

	/**
	 * Clears the {@link #isCanceled() canceled} flag and
	 * {@linkplain #setCompleted(int) sets completed} to 0.
	 * 
	 * @throws IllegalStateException
	 *             if this task is currently running
	 */
	public final void reset() {
		synchronized (this) {
			if (thread != null) {
				throw new IllegalStateException("task may not be reset while it's running");
			}
			completed = 0;
			canceled = false;
		}
	}

	private void setSubtask(int proportion, NewTask<?> subtask) {
		synchronized (this) {
			if (thread == null) {
				throw new IllegalStateException("a subtask may only be run when this task is running");
			}
			if (Thread.currentThread() != thread) {
				throw new IllegalStateException("a subtask must be run on the same thread as this task");
			}
			if (this.subtask != null) {
				throw new IllegalStateException("the current subtask has not finished");
			}

			synchronized (subtask) {
				if (subtask.thread != null) {
					throw new IllegalStateException("subtask is already running");
				}

				this.subtaskProportion = proportion;
				this.subtask = subtask;
				subtask.parent = this;
			}
		}
		fireChanged.run();
	}

	private void clearSubtask() {
		synchronized (this) {
			synchronized (subtask) {
				subtask.parent = null;
				subtask = null;
			}
			completed += subtaskProportion;
		}
		this.fireChanged.run();
	}

	private void fireChanged() {
		NewTask<?> task = NewTask.this;
		while (task != null) {
			ChangeEvent event = new ChangeEvent(task);
			for (ChangeListener listener : task.listeners) {
				listener.stateChanged(event);
			}
			task = task.subtask;
		}
	}

	private void start() {
		synchronized (this) {
			if (thread != null) {
				throw new IllegalStateException("already running");
			}
			thread = Thread.currentThread();
			if (parent != null) {
				fireChanged = parent.fireChanged;
			} else {
				DebounceOptions<Void> debounceOptions = NewTask.debounceOptions.get();
				if (debounceOptions != null) {
					fireChanged = Lodash.throttle(this::fireChanged, 30, debounceOptions);
				} else {
					fireChanged = DebouncedRunnable.noop();
				}
			}
		}
		fireChanged.run();
	}

	private void stop() {
		boolean isRoot;
		synchronized (this) {
			thread = null;
			isRoot = parent == null;
		}
		if (isRoot) {
			fireChanged.run();
			fireChanged.flush();
		}
	}
}
