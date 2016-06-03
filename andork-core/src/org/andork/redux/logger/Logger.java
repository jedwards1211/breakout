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

	public Logger dateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
		return this;
	}

	@Override
	public ForStore store(Store store) {
		return new ForStore() {
			@Override
			public Dispatcher next(Dispatcher next) {
				return new Dispatcher() {
					@Override
					public Object dispatch(Action action) {
						Object prevState = store.getState();
						Object result = next.dispatch(action);
						Object nextState = store.getState();
						System.out.println(dateFormat.format(new Date()) + "\t" + action.type);
						System.out.println("\tprevState: " + prevState);
						System.out.println("\taction:    " + action);
						System.out.println("\tnextState: " + nextState);
						return result;
					}
				};
			}
		};
	}
}
