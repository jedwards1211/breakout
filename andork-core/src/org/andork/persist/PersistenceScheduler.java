/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.persist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Schedules background save tasks for automatic persistence, so that they
 * happen within a given delay time, but no more often than the given delay
 * time.
 * 
 * @author andy.edwards
 */
public class PersistenceScheduler {
	private long					delay		= 100;

	private Object					lock		= new Object();
	private Map<Object, Runnable>	dirtyMap	= new HashMap<Object, Runnable>();
	private Set<Object>				runningSet	= new HashSet<Object>();

	/**
	 * Sets the time to wait between save tasks on the same dirty object.
	 * 
	 * @param delay
	 *            the delay, in milliseconds
	 */
	public void setDelay(long delay) {
		if (delay < 0) {
			throw new IllegalArgumentException("delay must be >= 0");
		}
		this.delay = delay;
	}

	/**
	 * Schedules a save task on the given executor thread. If no task for the
	 * given dirty object is currently running, the given task will be run as
	 * soon as possible. Otherwise, it will be run within {@link #delay}
	 * milliseconds after the current task finishes.
	 * 
	 * @param executor
	 *            the {@link ScheduledExecutorService} to submit the tasks to.
	 * @param dirtyObj
	 *            a unique {@link HashMap} key for the object that needs to be
	 *            saved.
	 * @param saver
	 *            the {@link Runnable} that will perform the background save
	 *            task.
	 */
	public void save(final ScheduledExecutorService executor, final Object dirtyObj, final Runnable saver) {
		boolean submit = false;

		synchronized (lock) {
			dirtyMap.put(dirtyObj, saver);
			submit = runningSet.add(dirtyObj);
		}

		if (submit) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					Runnable saver;
					synchronized (lock) {
						saver = dirtyMap.remove(dirtyObj);
					}

					if (saver != null) {
						saver.run();
					}

					boolean schedule = false;

					synchronized (lock) {
						if (dirtyMap.containsKey(dirtyObj)) {
							schedule = true;
						} else {
							runningSet.remove(dirtyObj);
						}
					}

					if (schedule) {
						executor.schedule(this, delay, TimeUnit.MILLISECONDS);
					}
				}
			});
		}
	}
}
