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

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.andork.event.HierarchicalBasicPropertyChangeSupport;

public interface TaskService {
	public void cancel(Task task);

	public HierarchicalBasicPropertyChangeSupport.External changeSupport();

	public List<Task> getTasks();

	public boolean hasTasks();

	public void submit(Task task);

	public default void submit(TaskRunnable task) {
		submit(new Task() {
			@Override
			protected void execute() throws Exception {
				task.execute(this);
			}
		});
	}

	public abstract void shutdown();

	public abstract boolean awaitTermination(long timeout, TimeUnit timeUnit) throws InterruptedException;

	public abstract List<Task> shutdownNow();
}
