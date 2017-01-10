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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.andork.event.BasicPropertyChangeSupport.External;
import org.andork.event.HierarchicalBasicPropertyChangePropagator;
import org.andork.event.HierarchicalBasicPropertyChangeSupport;

public class SingleThreadedTaskService implements TaskService {
	private final ExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	private final HierarchicalBasicPropertyChangeSupport propertyChangeSupport = new HierarchicalBasicPropertyChangeSupport();

	private final HierarchicalBasicPropertyChangePropagator propagator = new HierarchicalBasicPropertyChangePropagator(
			this, propertyChangeSupport);

	private final CopyOnWriteArrayList<Task> tasks = new CopyOnWriteArrayList<>();

	@Override
	public void cancel(Task task) {
		if (!tasks.remove(task)) {
			return;
		}
		Task.State state = task.getState();
		if (state != Task.State.CANCELED && state != Task.State.CANCELING) {
			task.cancel();
		}
		fireTaskRemoved(task);
	}

	@Override
	public External changeSupport() {
		return propertyChangeSupport.external();
	}

	private void fireTaskAdded(final Task task) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				propertyChangeSupport.fireChildAdded(SingleThreadedTaskService.this, task);
			}
		});
	}

	private void fireTaskRemoved(final Task task) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				propertyChangeSupport.fireChildRemoved(SingleThreadedTaskService.this, task);
			}
		});
	}

	@Override
	public List<Task> getTasks() {
		return Collections.unmodifiableList(tasks);
	}

	@Override
	public boolean hasTasks() {
		return !tasks.isEmpty();
	}

	@Override
	public void submit(Task task) {
		task.setService(this);
		task.changeSupport().addPropertyChangeListener(propagator);
		tasks.add(task);
		executor.submit(() -> {
			try {
				if (!task.isCanceled()) {
					task.execute();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				task.changeSupport().removePropertyChangeListener(propagator);
				tasks.remove(task);
				fireTaskRemoved(task);
			}
		});
		fireTaskAdded(task);
	}

	@Override
	public void shutdown() {
		executor.shutdown();
	}

	@Override
	public List<Task> shutdownNow() {
		List<Task> tasks = new ArrayList<>(this.tasks);
		for (Task task : tasks) {
			if (task.isCancelable()) {
				task.cancel();
			}
		}
		executor.shutdownNow();
		return tasks;
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return executor.awaitTermination(timeout, unit);
	}
}
