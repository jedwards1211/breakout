package org.andork.tracker;

public class TrackerException extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = 1237856416997002306L;

	public TrackerException() {
		super();
	}

	public TrackerException(String message) {
		super(message);
	}

	public TrackerException(String message, Throwable cause) {
		super(message, cause);
	}

	public TrackerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TrackerException(Throwable cause) {
		super(cause);
	}
}
