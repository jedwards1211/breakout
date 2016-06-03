/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.swing.async;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;

import org.andork.event.BasicPropertyChangeSupport;
import org.andork.event.HierarchicalBasicPropertyChangeSupport;
import org.andork.model.HasChangeSupport;
import org.andork.util.ArrayUtils;
import org.andork.util.Java7;

public abstract class Task implements HasChangeSupport {
	public static enum Property {
		STATE, SERVICE, STATUS, INDETERMINATE, COMPLETED, TOTAL;
	}

	public static enum State {
		NOT_SUBMITTED, WAITING, RUNNING, CANCELING, CANCELED, FINISHED, FAILED;

		public boolean hasEnded() {
			return this == CANCELED || this == FINISHED || this == FAILED;
		}
	}

	private final long creationTimestamp;

	private final Object lock = new Object();
	private State state = State.NOT_SUBMITTED;
	private TaskService service;
	private Throwable throwable;

	private String status;

	private boolean indeterminate = true;

	private int completed;
	private int total = 1000;

	private final BasicPropertyChangeSupport propertyChangeSupport = new BasicPropertyChangeSupport();

	public Task() {
		creationTimestamp = System.nanoTime();
	}

	public Task(String status) {
		this();
		setStatus(status);
	}

	protected void afterReset() {
	}

	public final void cancel() {
		if (!isCancelable()) {
			throw new UnsupportedOperationException("task is not cancelable");
		}
		State oldState;
		State newState;
		TaskService service;
		synchronized (lock) {
			if (state == State.CANCELED || state == State.CANCELING || state == State.FINISHED
					|| state == State.FAILED) {
				return;
			}
			oldState = state;
			newState = state = state == State.RUNNING ? State.CANCELING : State.CANCELED;
			service = this.service;

			lock.notifyAll();
		}
		service.cancel(this);
		firePropertyChange(Property.STATE, oldState, newState);
	}

	public boolean canRunInParallelWith(Task other) {
		return false;
	}

	@Override
	public HierarchicalBasicPropertyChangeSupport.External changeSupport() {
		return propertyChangeSupport.external();
	}

	private void checkState(State required) {
		if (state != required) {
			throw new IllegalStateException("Operation not allowed unless state is " + required
					+ "; state is currently " + state);
		}
	}

	private void checkState(State... required) {
		if (ArrayUtils.indexOf(required, state) < 0) {
			throw new IllegalStateException("Operation not allowed unless state is "
					+ ArrayUtils.cat(required, " or ") + "; state is currently " + state);
		}
	}

	@Override
	public final boolean equals(Object o) {
		return super.equals(o);
	}

	protected abstract void execute() throws Exception;

	private final void firePropertyChange(final Property property, final Object oldValue, final Object newValue) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				propertyChangeSupport.firePropertyChange(Task.this, property, oldValue, newValue);
			}
		});
	}

	public int getCompleted() {
		return completed;
	}

	public long getCreationTimestamp() {
		return creationTimestamp;
	}

	public final State getState() {
		synchronized (lock) {
			return state;
		}
	}

	public String getStatus() {
		return status;
	}

	public Throwable getThrowable() {
		synchronized (lock) {
			return throwable;
		}
	}

	public int getTotal() {
		return total;
	}

	public final boolean hasEnded() {
		return getState().hasEnded();
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	public boolean isCancelable() {
		return false;
	}

	public final boolean isCanceled() {
		return getState() == State.CANCELED;
	}

	public final boolean isCanceling() {
		return getState() == State.CANCELING;
	}

	public boolean isIndeterminate() {
		return indeterminate;
	}

	public final void reset() {
		State oldState;
		synchronized (lock) {
			checkState(State.CANCELED, State.FINISHED, State.FAILED);
			oldState = state;
			state = State.NOT_SUBMITTED;
			service = null;

			lock.notifyAll();
		}
		firePropertyChange(Property.STATE, oldState, State.NOT_SUBMITTED);
		afterReset();
	}

	public final void run() {
		try {
			setRunning();
			execute();
			setCanceledOrFinished();
		} catch (Throwable e) {
			synchronized (lock) {
				if (state == State.CANCELING) {
					setCanceledOrFinished();
				} else {
					setFailed(e);
				}
			}
		}
	}

	private void setCanceledOrFinished() {
		State oldState;
		State newState;
		synchronized (lock) {
			oldState = state;
			switch (state) {
			case CANCELING:
				state = State.CANCELED;
				break;
			case RUNNING:
				state = State.FINISHED;
				break;
			default:
				throw new IllegalStateException("Operation not allowed unless state == CANCELED or FINISHED");
			}
			newState = state;

			lock.notifyAll();
		}
		firePropertyChange(Property.STATE, oldState, newState);
	}

	public void setCompleted(int completed) {
		if (completed < 0) {
			throw new IllegalArgumentException("completed must be >= 0");
		}
		Integer oldValue = null;
		synchronized (lock) {
			if (this.completed != completed) {
				oldValue = this.completed;
				this.completed = completed;
			}
		}
		if (oldValue != null) {
			firePropertyChange(Property.COMPLETED, oldValue, completed);
		}
	}

	private void setFailed(Throwable t) {
		State oldState;
		synchronized (lock) {
			oldState = state;
			state = State.FAILED;
			throwable = t;

			lock.notifyAll();
		}
		t.printStackTrace();
		firePropertyChange(Property.STATE, oldState, State.FAILED);
	}

	public void setIndeterminate(boolean indefinite) {
		Boolean oldValue = null;
		synchronized (lock) {
			if (indeterminate != indefinite) {
				oldValue = indeterminate;
				indeterminate = indefinite;
			}
		}
		if (oldValue != null) {
			firePropertyChange(Property.INDETERMINATE, oldValue, indefinite);
		}
	}

	private void setRunning() {
		synchronized (lock) {
			checkState(State.WAITING);
			state = State.RUNNING;

			lock.notifyAll();
		}
		firePropertyChange(Property.STATE, State.WAITING, State.RUNNING);
	}

	public final void setService(TaskService service) {
		if (service == null) {
			throw new IllegalArgumentException("service must be non-null");
		}

		synchronized (lock) {
			if (this.service != null) {
				throw new IllegalStateException("Task is still registered with a service");
			}

			checkState(State.NOT_SUBMITTED);
			state = State.WAITING;
			this.service = service;

			lock.notifyAll();
		}

		firePropertyChange(Property.STATE, State.NOT_SUBMITTED, State.WAITING);
	}

	public void setStatus(String status) {
		String oldValue = null;
		synchronized (lock) {
			if (!Java7.Objects.equals(this.status, status)) {
				oldValue = this.status;
				this.status = status;
			}
		}
		if (oldValue != null) {
			firePropertyChange(Property.STATUS, oldValue, status);
		}
	}

	public void setTotal(int total) {
		if (total < 0) {
			throw new IllegalArgumentException("total must be >= 0");
		}

		Integer oldValue = null;
		synchronized (lock) {
			if (this.total != total) {
				oldValue = this.total;
				this.total = total;
			}
		}
		if (oldValue != null) {
			firePropertyChange(Property.TOTAL, oldValue, total);
		}
	}

	public final void waitUntilHasEnded() throws InterruptedException {
		synchronized (lock) {
			while (!hasEnded()) {
				lock.wait();
			}
		}
	}

	public final void waitUntilHasFinished() throws InterruptedException, ExecutionException {
		waitUntilHasEnded();
		if (throwable != null) {
			throw new ExecutionException(throwable);
		}
	}
}
