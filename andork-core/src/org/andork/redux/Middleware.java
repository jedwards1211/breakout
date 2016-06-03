package org.andork.redux;

/**
 * Intercepts {@link Action}s dispatched to a {@link Store} and can perform side
 * effects and control further processing of each action. Middleware are
 * composed in a chain with the store at the end, so that each action passes
 * through the chain of middleware to the store (though any middleware may
 * interrupt the chain).
 *
 * @author andy
 */
public interface Middleware {
	public interface ForStore {
		/**
		 * @param next
		 *            the next {@link Dispatcher} (from the next middleware, or
		 *            the {@link Store} at the end of the chain)
		 * @return the {@link Dispatcher} from this middleware that handles
		 *         actions
		 */
		public Dispatcher next(Dispatcher next);
	}

	/**
	 * @return the middleware logic bound to the given {@link Store}.
	 */
	public ForStore store(Store<?> store);
}
