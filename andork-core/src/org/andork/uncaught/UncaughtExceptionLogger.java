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
