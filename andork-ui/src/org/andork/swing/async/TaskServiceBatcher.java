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

import java.util.LinkedList;

import org.andork.event.BasicPropertyChangeListener;
import org.andork.swing.async.Task.State;
import org.andork.util.Batcher;

public abstract class TaskServiceBatcher<M> extends Batcher<M> implements BasicPropertyChangeListener {
	public static abstract class BatcherTask<M> extends Task {
		private LinkedList<M> batch;

		public BatcherTask() {
			super();
		}

		public BatcherTask(String status) {
			super(status);
		}
	}

	TaskService taskService;
	final Object latestLock = new Object();
	BatcherTask<M> latestTask;

	boolean cancelCurrentUponAdd;

	public TaskServiceBatcher(TaskService taskService, boolean cancelCurrentUponAdd) {
		super();
		this.taskService = taskService;
		this.cancelCurrentUponAdd = cancelCurrentUponAdd;
	}

	public abstract BatcherTask<M> createTask(LinkedList<M> batch);

	@Override
	protected void eventQueued(M t) {
		synchronized (latestLock) {
			if (cancelCurrentUponAdd && latestTask != null) {
				latestTask.cancel();
			}
		}
	}

	@Override
	protected void handleLater(LinkedList<M> batch) {
		synchronized (latestLock) {
			latestTask = createTask(batch);
			latestTask.batch = batch;
			latestTask.changeSupport().addPropertyChangeListener(this);
			taskService.submit(latestTask);
		}
	}

	@Override
	public void propertyChange(Object source, Object property, Object oldValue, Object newValue, int index) {
		LinkedList<M> doneBatch = null;
		synchronized (latestLock) {
			BatcherTask<M> task = (BatcherTask<M>) source;
			State state = latestTask.getState();
			if (state == State.CANCELED || state == State.FINISHED || state == State.FAILED) {
				task.changeSupport().removePropertyChangeListener(this);
				doneBatch = task.batch;
			}
		}
		if (doneBatch != null) {
			doneHandling(doneBatch);
		}
	}
}
