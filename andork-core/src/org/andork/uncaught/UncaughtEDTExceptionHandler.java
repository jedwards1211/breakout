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
package org.andork.uncaught;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Handles uncaught exceptions on the EventDispatchThread in Java 1.5 and 1.6
 * (on 1.7 EventDispatchThread invokes its registered
 * {@link UncaughtExceptionHandler} itself, but I don't know about versions
 * before 1.5).<br/>
 * <br/>
 * To use, simply call {@link #init()} which sets the
 * {@code sun.awt.exception.handler} System property, registering this class to
 * handle uncaught exceptions on the EDT on 1.5/1.6. It will pass them along to
 * the {@link UncaughtExceptionHandler} registered on the EDT (if any).<br/>
 * <br/>
 *
 * @see EventDispatchThread#handleException(Throwable)
 *
 * @author andy.edwards
 */
public class UncaughtEDTExceptionHandler {
	public static void init() {
		System.setProperty("sun.awt.exception.handler", UncaughtEDTExceptionHandler.class.getName());
	}

	public UncaughtEDTExceptionHandler() {
	}

	public void handle(Throwable t) {
		Thread current = Thread.currentThread();
		UncaughtExceptionHandler handler = current.getUncaughtExceptionHandler();
		if (handler != null) {
			handler.uncaughtException(current, t);
		}
	}
}
