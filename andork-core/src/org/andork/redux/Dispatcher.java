package org.andork.redux;

/**
 * Anything that dispatches {@link Action}s.
 *
 * @author andy
 */
public interface Dispatcher {
	/**
	 * Dispatches an {@link Action} to a {@link Store}. The store will update to
	 * a new state computed using the {@link Reducer} on its current state and
	 * the action.
	 *
	 * @param action
	 *            the action to dispatch
	 * @return whatever the implementation wants
	 */
	public Object dispatch(Action action);
}
