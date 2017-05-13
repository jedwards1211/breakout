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
package org.andork.task;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.andork.event.HierarchicalBasicPropertyChangeSupport;
import org.andork.func.Lodash.DebounceOptions;

public interface TaskService {
	public void setDebounceOptions(DebounceOptions<Void> options);

	public HierarchicalBasicPropertyChangeSupport.External changeSupport();

	public List<Task<?>> getTasks();

	public boolean hasTasks();

	public <V> Future<V> submit(Task<V> task);

	public default Future<Void> submit(TaskRunnable task) {
		return submit(new Task<Void>() {
			@Override
			protected Void work() throws Exception {
				task.work(this);
				return null;
			}
		});
	}

	public abstract void shutdown();

	public abstract boolean awaitTermination(long timeout, TimeUnit timeUnit) throws InterruptedException;

	public abstract List<Task<?>> shutdownNow();
}
