package org.andork.redux.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.andork.redux.Action;
import org.andork.redux.Dispatcher;
import org.andork.redux.Middleware;
import org.andork.redux.Store;

public class Logger implements Middleware {
	private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
	private boolean logState = true;

	public Logger dateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
		return this;
	}

	public Logger logState(boolean logState) {
		this.logState = logState;
		return this;
	}

	@Override
	public ForStore store(Store<?> store) {
		return new ForStore() {
			@Override
			public Dispatcher next(Dispatcher next) {
				return new Dispatcher() {
					@Override
					public Object dispatch(Action action) {
						System.out.println(dateFormat.format(new Date()) + "\t" + action.type);
						if (logState) {
							System.out.println("\tprevState: " + store.getState());
						}
						System.out.println("\taction:    " + action);
						Object result = next.dispatch(action);
						if (logState) {
							System.out.println("\tnextState: " + store.getState());
						}
						return result;
					}
				};
			}
		};
	}
}
