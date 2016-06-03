package org.andork.redux;

public interface Store extends Dispatcher {
	public Object getState();

	public Runnable subscribe(Runnable callback);
}
