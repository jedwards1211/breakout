package org.andork.uncaught;

import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * An {@link UncaughtExceptionHandler} that prints exception stack traces to a
 * given {@link PrintStream}.
 * 
 * @author andy.edwards
 */
public class UncaughtExceptionPrinter implements UncaughtExceptionHandler {
	private final PrintStream	out;

	public UncaughtExceptionPrinter(PrintStream out) {
		super();
		this.out = out;
	}

	public void uncaughtException(Thread t, Throwable e) {
		e.printStackTrace(out);
	}
}
