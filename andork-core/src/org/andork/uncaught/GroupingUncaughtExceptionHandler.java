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
package org.andork.uncaught;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An {@link UncaughtExceptionHandler} that groups uncaught exceptions with the
 * same class, localized message, and stack trace, and doesn't report more than
 * twice for a given group in a given time interval. When it receives an
 * uncaught exception, if it hasn't seen a matching exception since the last
 * report, it passes the exception onto the downstream
 * {@code UncaughtExceptionHandler}; otherwise, it increments the count for that
 * exception and schedules a timer to report (via Exceptions sent to the
 * downstream {@code UncaughtExceptionHandler} how many of each type of
 * exception occured since the last report.
 * 
 * @author andy.edwards
 */
public class GroupingUncaughtExceptionHandler implements UncaughtExceptionHandler {
	private final Object					lock		= new Object();
	private long							lastDump;
	private long							nextDump;
	private final Map<Trace, Integer>		delayedMap	= new LinkedHashMap<Trace, Integer>();

	private final UncaughtExceptionHandler	downstream;

	private final java.util.Timer			timer;

	private final long						dumpInterval;
	
	public GroupingUncaughtExceptionHandler(UncaughtExceptionHandler downstream, long dumpInterval) {
		super();
		this.downstream = downstream;
		this.dumpInterval = dumpInterval;
		timer = new Timer(true);
	}

	private static class Trace {
		int					hashCode;
		Throwable			throwable;
		StackTraceElement[]	trace;

		public Trace(Throwable throwable) {
			this.throwable = throwable;
			trace = throwable.getStackTrace();
			hashCode = throwable.getClass().hashCode() ^ (29 * Arrays.hashCode(trace));
		}

		public boolean equals(Object o) {
			if (o instanceof Trace) {
				Trace t = (Trace) o;
				return throwable.getClass().equals(t.throwable.getClass()) &&
						throwable.getLocalizedMessage().equals(t.throwable.getLocalizedMessage()) &&
						Arrays.equals(trace, t.trace);
			}
			return false;
		}

		public int hashCode() {
			return hashCode;
		}
	}

	public void uncaughtException(Thread t, Throwable e) {
		synchronized (lock) {

			Trace trace = null;
			try {
				trace = new Trace(e);
			} catch (Exception ex) {
				downstream.uncaughtException(Thread.currentThread(), new Exception("Failed to hash Throwable", e));
				return;
			}

			Integer prevCount = delayedMap.get(trace);
			if (prevCount == null) {
				downstream.uncaughtException(t, e);
				delayedMap.put(trace, 0);
			} else {
				delayedMap.put(trace, prevCount + 1);

				long currentTime = System.currentTimeMillis();

				if (prevCount == 1) {
					if (nextDump <= lastDump) {
						nextDump = Math.max(lastDump + dumpInterval, currentTime + 1);

						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								dumpGroups();
							}
						}, Math.max(nextDump - System.currentTimeMillis(), 1));
					}
				}
			}
		}
	}

	protected void dumpGroups() {
		synchronized (lock) {
			for (Map.Entry<Trace, Integer> entry : delayedMap.entrySet()) {
				Trace groupTrace = entry.getKey();
				int groupCount = entry.getValue();

				if (groupCount > 0) {
					downstream.uncaughtException(Thread.currentThread(),
							new Exception("There were " + groupCount +
									" uncaught exceptions of the following form since last notice",
									groupTrace.throwable));
				}
			}

			delayedMap.clear();
			lastDump = System.currentTimeMillis();
		}
	}
}
