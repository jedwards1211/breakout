package org.andork.redux;

/**
 * A pure function that takes current state and an action and returns a new
 * state.
 *
 * @author andy
 *
 * @param <S>
 *            the state type
 */
public interface Reducer<S> {
	/**
	 * @param state
	 *            the current state
	 * @param action
	 *            an {@link Action}
	 * @return the new state, modified as desired for the given action
	 */
	public S apply(S state, Action action);
}
