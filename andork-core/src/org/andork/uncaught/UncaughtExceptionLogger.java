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

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

/**
 * An {@link UncaughtExceptionHandler} that logs uncaught exceptions to a given
 * {@link Logger} at a given {@link Level}.
 * 
 * @author andy.edwards
 */
public class UncaughtExceptionLogger implements UncaughtExceptionHandler {
	private final Logger	logger;
	private final Level		level;

	public static void main(String[] args) throws SecurityException, IOException {
		Logger logger = Logger.getLogger("Test");
		
		Thread.setDefaultUncaughtExceptionHandler(
				new MultiplexUncaughtExceptionHandler(
						new UncaughtExceptionPrinter(System.err),
						new GroupingUncaughtExceptionHandler(
								new UncaughtExceptionLogger(logger, Level.SEVERE), 1000)));

		UncaughtEDTExceptionHandler.init();

		for (int i = 0; i < 100; i++) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					throw new RuntimeException("Test 1");
				}
			});
		}

		throw new RuntimeException("Test 2");
	}

	public UncaughtExceptionLogger(Logger logger, Level level) {
		super();
		this.logger = logger;
		this.level = level;
	}

	public void uncaughtException(Thread t, Throwable e) {
		logger.log(level, "Uncaught exception: ", e);
	}
}
