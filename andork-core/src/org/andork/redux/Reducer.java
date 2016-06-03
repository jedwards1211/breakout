package org.andork.redux;

public interface Reducer {
	public Object apply(Object state, Action action);
}
