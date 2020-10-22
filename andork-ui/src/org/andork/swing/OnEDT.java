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
package org.andork.swing;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.andork.func.ExceptionRunnable;
import org.andork.func.RuntimeInvocationTargetException;

/**
 * Takes the pain out of writing {@link SwingUtilities#invokeAndWait(Runnable)}
 * calls. Upon construction the {@link #run()} method will be called on the EDT,
 * and it may throw any exception, unlike {@link Runnable#run()}.
 *
 * @author andy.edwards
 *
 * @param <R>
 */
public abstract class OnEDT {
	public static void onEDT(ExceptionRunnable r) {
		if (SwingUtilities.isEventDispatchThread()) {
			try {
				r.run();
			}
			catch (Throwable t) {
				throw new RuntimeInvocationTargetException(t);
			}
			return;
		}
		try {
			SwingUtilities.invokeAndWait(() -> {
				try {
					r.run();
				}
				catch (Throwable t) {
					throw new RuntimeInvocationTargetException(t);
				}
			});
		}
		catch (InvocationTargetException e) {
			if (e.getCause() instanceof RuntimeInvocationTargetException) {
				throw (RuntimeInvocationTargetException) e.getCause();
			}
			else {
				throw new RuntimeInvocationTargetException(e.getCause());
			}
		}
		catch (InterruptedException e) {
			throw new RuntimeInterruptedException(e);
		}
	}

	/**
	 * This constructor calls {@link #doRun()} on the EDT immediately so that you
	 * can save a few keystrokes.
	 *
	 * @throws RuntimeInvocationTargetException wrapping the exception thrown by
	 *                                          {@link #doRun()}, if any
	 * @throws RuntimeInterruptedException      if the calling thread was
	 *                                          interrupted while waiting for
	 *                                          {@link SwingUtilities#invokeAndWait(Runnable)}
	 *                                          to return.
	 */
	public OnEDT() {
		if (SwingUtilities.isEventDispatchThread()) {
			callRun();
		}
		else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						callRun();
					}
				});
			}
			catch (InvocationTargetException e) {
				// first cause is the RuntimeInvocationTargetException thrown
				// from run(); second cause is whatever doRun() threw. We want
				// to rewrap the second cause in a
				// RuntimeInvocationTargetException with a stack trace from this
				// method.
				throw new RuntimeInvocationTargetException(e.getCause().getCause());
			}
			catch (InterruptedException e) {
				throw new RuntimeInterruptedException(e);
			}
		}
	}

	private void callRun() {
		try {
			run();
		}
		catch (Throwable t) {
			throw new RuntimeInvocationTargetException(t);
		}
	}

	public abstract void run() throws Throwable;
}
