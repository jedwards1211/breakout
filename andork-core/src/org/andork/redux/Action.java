package org.andork.redux;

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
