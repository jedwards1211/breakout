package org.andork.redux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.function.UnaryOperator;

public class Redux {
	public static UnaryOperator<Store> applyMiddleware(Middleware... middleware) {
		return store -> {
			Dispatcher dispatcher = store;
			for (ListIterator<Middleware> i = Arrays.asList(middleware).listIterator(middleware.length); i
					.hasPrevious();) {
				dispatcher = i.previous().store(store).next(dispatcher);
			}
			final Dispatcher finalDispatcher = dispatcher;
			return new Store() {
				@Override
				public Object dispatch(Action action) {
					return finalDispatcher.dispatch(action);
				}

				@Override
				public Object getState() {
					return store.getState();
				}

				@Override
				public Runnable subscribe(Runnable callback) {
					return store.subscribe(callback);
				}
			};
		};
	}

	public static Store createStore(Reducer reducer, Object initialState) {
		return new Store() {
			Object state = initialState;
			final List<Runnable> listeners = new ArrayList<>();

			@Override
			public Object dispatch(Action action) {
				Object nextState = reducer.apply(state, action);
				if (nextState != state) {
					state = nextState;
					for (Runnable r : listeners) {
						r.run();
					}
				}
				return nextState;
			}

			@Override
			public Object getState() {
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
