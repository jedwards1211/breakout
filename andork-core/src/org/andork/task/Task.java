package org.andork.task;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
public abstract class Task<R> implements Callable<R> {
	private volatile Thread thread;
	private volatile Task<?> parent;
	private volatile Task<?> subtask;

	private volatile long subtaskProportion;
	private final List<ChangeListener> listeners = new CopyOnWriteArrayList<>();
	private volatile Runnable onceCanceled;
	private volatile String status;

	private volatile boolean indeterminate = false;
	private volatile long completed = 0;
	private volatile long total = 1;

	private volatile boolean canceled = false;
	private volatile long startTime;

	private volatile long lastFireChangedTime = 0;

	private final ChangeListener childChangeListener = e -> fireChanged();

	/**
	 * Runs this task. It must not be running before this call is made.
	 *
	 * @return the result of calling {@link #work()}.
	 * @throws IllegalStateException
	 *             if this task is already running.
	 * @throws Exception
	 *             if {@link #work()} threw it
	 */
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
	public final <R2> R2 callSubtask(long proportion, Task<R2> subtask) throws Exception {
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

	public final void runSubtask(long proportion, TaskRunnable runnable) throws Exception {
		callSubtask(proportion, new Task<Void>() {
			@Override
			protected Void work() throws Exception {
				runnable.work(this);
				return null;
			}
		});
	}

	public final void runSubtasks(TaskRunnable... runnables) throws Exception {
		setIndeterminate(false);
		setTotal(runnables.length);

		for (TaskRunnable runnable : runnables) {
			runSubtask(1, runnable);
		}
	}

	public final <V> V callSubtask(long proportion, TaskCallable<V> callable) throws Exception {
		return callSubtask(proportion, new Task<V>() {
			@Override
			protected V work() throws Exception {
				return callable.work(this);
			}
		});
	}

	/**
	 * Cancels this task and its {@link #getParent() parent} task (if any).
	 * After this {@link #isCanceled()} will return true until {@link #reset()}
	 * is called. This does not interrupt the task; the {@link #work()}
	 * implementation should periodically check {@link #isCanceled()} and abort
	 * if it returns {@code true}.
	 */
	public void cancel() {
		Task<?> parent;
		Runnable onceCanceled;
		synchronized (this) {
			canceled = true;
			parent = this.parent;
			onceCanceled = this.onceCanceled;
			this.onceCanceled = null;
		}
		if (parent != null) {
			parent.cancel();
		}
		if (onceCanceled != null) {
			onceCanceled.run();
		}
		forceFireChanged();
	}

	public void onceCanceled(Runnable r) {
		synchronized (this) {
			if (onceCanceled != null) {
				throw new IllegalStateException("a onceCanceled listener has already been registered");
			}
			onceCanceled = r;
		}
	}

	/**
	 * Called by {@link #run()} (which safely sets the state of this task before
	 * and after).
	 */
	protected abstract R work() throws Exception;

	/**
	 * @return the combined progress of this task and its subtasks. {@code NaN}
	 *         means progress is indeterminate.
	 */
	public double getCombinedProgress() {
		if (indeterminate)
			return Double.NaN;
		Task<?> subtask = this.subtask;
		return subtask != null
				? (completed + subtaskProportion * subtask.getCombinedProgress()) / total
				: getProgress();
	}

	/**
	 * @return the combined status of this task and its subtasks.
	 */
	public String getCombinedStatus() {
		Task<?> subtask = this.subtask;
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

	public Task<?> getDeepestSubtask() {
		Task<?> subtask = this.subtask;
		return subtask == null ? this : subtask.getDeepestSubtask();
	}

	public double getDeepestSubtaskProgress() {
		return getDeepestSubtask().getProgress();
	}

	public String getDeepestSubtaskStatus() {
		return getDeepestSubtask().getStatus();
	}

	public Task<?> getParent() {
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

	public Task<?> getSubtask() {
		return subtask;
	}

	public boolean isCanceled() {
		Task<?> parent = this.parent;
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

	public void setCompleted(long completed) {
		if (isCanceled()) {
			throw new TaskCanceledException();
		}
		this.completed = completed;
		fireChanged();
	}

	public void increment() {
		this.increment(1);
	}

	public void increment(long amount) {
		setCompleted(completed + amount);
	}

	public long getCompleted() {
		return completed;
	}

	public long getTotal() {
		return total;
	}

	public boolean isIndeterminate() {
		return indeterminate;
	}

	public void setTotal(long total) {
		this.total = total;
		fireChanged();
	}

	public void setStatus(String newStatus) {
		status = newStatus;
		fireChanged();
	}

	public void setIndeterminate(boolean indeterminate) {
		this.indeterminate = indeterminate;
		fireChanged();
	}

	public long startTime() {
		return startTime;
	}
	
	public static interface Iteratee<E> {
		public void accept(E elem) throws Exception;
	}
	
	public <E> void forEach(E[] array, Iteratee<E> iteratee) throws Exception {
		setIndeterminate(false);
		setCompleted(0);
		setTotal(array.length);
		for (E elem : array) {
			if (isCanceled()) return;
			iteratee.accept(elem);
			increment();
		}
	}
	
	public <E> void forEach(Collection<E> collection, Iteratee<E> iteratee) throws Exception {
		setIndeterminate(false);
		setCompleted(0);
		setTotal(collection.size());
		for (E elem : collection) {
			if (isCanceled()) return;
			iteratee.accept(elem);
			increment();
		}
	}

	private void setSubtask(long proportion, Task<?> subtask) {
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

			subtask.addChangeListener(childChangeListener);
		}
		forceFireChanged();
	}

	private void clearSubtask() {
		subtask.removeChangeListener(childChangeListener);
		synchronized (this) {
			synchronized (subtask) {
				subtask.parent = null;
				subtask = null;
			}
			completed += subtaskProportion;
		}
		fireChanged();
	}

	private final ChangeEvent changeEvent = new ChangeEvent(this);

	private void fireChanged() {
		long time = System.currentTimeMillis();
		if (time - lastFireChangedTime >= 30) {
			lastFireChangedTime = time;
			for (ChangeListener listener : listeners) {
				listener.stateChanged(changeEvent);
			}
		}
	}

	private void forceFireChanged() {
		lastFireChangedTime = 0; // force fireChanged() to go through
		fireChanged();
	}

	private void start() {
		synchronized (this) {
			if (canceled) {
				throw new IllegalStateException("already canceled");
			}
			if (thread != null) {
				throw new IllegalStateException("already running");
			}
			startTime = System.currentTimeMillis();
			thread = Thread.currentThread();
		}
		forceFireChanged();
	}

	private void stop() {
		boolean isRoot;
		synchronized (this) {
			thread = null;
			onceCanceled = null;
			isRoot = parent == null;
		}
		if (isRoot) {
			forceFireChanged();
		}
	}
}
