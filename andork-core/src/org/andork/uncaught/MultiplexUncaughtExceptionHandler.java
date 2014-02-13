package org.andork.uncaught;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;

public class MultiplexUncaughtExceptionHandler implements UncaughtExceptionHandler {
	private final UncaughtExceptionHandler[]	handlers;

	public MultiplexUncaughtExceptionHandler(UncaughtExceptionHandler... handlers) {
		super();
		this.handlers = Arrays.copyOf(handlers, handlers.length);
	}

	public void uncaughtException(Thread t, Throwable e) {
		for (UncaughtExceptionHandler handler : handlers) {
			try {
				handler.uncaughtException(t, e);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}
}
