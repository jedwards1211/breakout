package org.andork.redux;

/**
 * A command that specifies a desired state change. It gets dispatched to a
 * {@link Store} and then processed by the store's {@link Reducer}.
 * 
 * @author andy
 */
public class Action {
	public final String type;

	public Action(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Action{type: " + type + "}";
	}
}
