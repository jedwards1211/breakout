package org.andork.redux;

/**
 * A direct ripoff of Redux, a JS predictable state container. Stores
 * application state and allows {@linkplain #subscribe(Runnable) subscribing} to
 * state updates.
 *
 * Best practice is to use a single {@link Store} for an entire application.
 * Centralized state management makes the app easier to understand and debug.
 *
 * The state can be changed by {@linkplain #dispatch(Action) dispatching}
 * {@link Action}s, which the store takes and passes to its {@link Reducer}
 * along with the current state.
 *
 * @author andy
 *
 * @param <S>
 *            the type of the application state.
 */
public interface Store<S> extends Dispatcher {
	/**
	 * @return the current state stored in this Store.
	 */
	public S getState();

	/**
	 * Subscribes for state change notifications.
	 * 
	 * @param callback
	 *            a {@link Runnable} to {@link Runnable#run() run} when the
	 *            state changes.
	 * @return a {@link Runnable} that unsubscribes the given {@code callback}
	 *         when run.
	 */
	public Runnable subscribe(Runnable callback);
}
