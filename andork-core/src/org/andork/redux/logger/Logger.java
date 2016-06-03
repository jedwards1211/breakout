package org.andork.redux.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

import org.andork.redux.Action;
import org.andork.redux.Dispatcher;
import org.andork.redux.Middleware;
import org.andork.redux.Store;

public class Logger implements Middleware {
	private Consumer<String> log = System.out::println;
	private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
	private boolean logState = true;

	public Logger dateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
		return this;
	}

	public Logger log(Consumer<String> log) {
		this.log = log;
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
						Date date = new Date();
						Object prevState = store.getState();
						Object result = next.dispatch(action);
						Object nextState = store.getState();
						log.accept(dateFormat.format(date) + " " + action.type);
						if (logState) {
							System.out.println("  prevState: " + prevState);
						}
						log.accept("  action:    " + action);
						if (logState) {
							log.accept("  nextState: " + nextState);
						}
						return result;
					}
				};
			}
		};
	}
}
