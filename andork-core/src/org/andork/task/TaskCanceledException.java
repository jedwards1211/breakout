package org.andork.task;

public class TaskCanceledException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2294509510725014491L;

	public TaskCanceledException() {
	}

	public TaskCanceledException(String message) {
		super(message);
	}

	public TaskCanceledException(Throwable cause) {
		super(cause);
	}

	public TaskCanceledException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaskCanceledException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
