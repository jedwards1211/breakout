package org.andork.redux;

public interface Middleware {
	public interface ForStore {
		public Dispatcher next(Dispatcher next);
	}

	public ForStore store(Store store);
}
