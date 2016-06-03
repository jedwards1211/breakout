package org.andork.redux;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Redux {
	/**
	 * Wraps a store to pass {@linkplain Store#dispatch(Action) dispatched}
	 * {@link Action}s through a chain of {@link Middleware}s that can perform
	 * side effects like logging, debugging, and even dispatching other actions,
	 * before the state is updated via the {@link Reducer}.
	 *
	 * @param middleware
	 *            a {@link List} of {@link Middleware} to apply.
	 * @param store
	 *            the {@link Store} to wrap.
	 * @return a {@link Store} whose {@link Store#dispatch(Action) dispatch}
	 *         method calls the applied middleware.
	 */
	public static <S> Store<S> applyMiddleware(List<Middleware> middleware, Store<S> store) {
		Dispatcher dispatcher = store;
		for (ListIterator<Middleware> i = middleware.listIterator(middleware.size()); i.hasPrevious();) {
			dispatcher = i.previous().store(store).next(dispatcher);
		}
		final Dispatcher finalDispatcher = dispatcher;

		return new Store<S>() {
			@Override
			public Object dispatch(Action action) {
				try {
					return finalDispatcher.dispatch(action);
				} catch (Exception e) {
					e.printStackTrace();
					return store.getState();
				}
			}

			@Override
			public S getState() {
				return store.getState();
			}

			@Override
			public Runnable subscribe(Runnable callback) {
				return store.subscribe(callback);
			}
		};
	}

	/**
	 * Creates a {@link Store}.
	 *
	 * @param reducer
	 *            the {@link Reducer} to compute state updates for dispatched
	 *            {@link Action}s.
	 * @param initialState
	 *            the initial state for the store.
	 * @return a {@link Store} implementation.
	 */
	public static <S> Store<S> createStore(Reducer<S> reducer, S initialState) {
		return new Store<S>() {
			S state = initialState;
			final List<Runnable> listeners = new ArrayList<>();

			@Override
			public Object dispatch(Action action) {
				try {
					S nextState = reducer.apply(state, action);
					if (nextState != state) {
						state = nextState;
						for (Runnable r : listeners) {
							r.run();
						}
					}
					return nextState;
				} catch (Exception e) {
					e.printStackTrace();
					return getState();
				}
			}

			@Override
			public S getState() {
				return state;
			}

			@Override
			public Runnable subscribe(Runnable callback) {
				listeners.add(callback);
				return () -> listeners.remove(callback);
			}
		};
	}
}
