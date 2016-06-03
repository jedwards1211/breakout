package org.andork.react;

public final class Atom<T> extends Reactable<T> {
	public Atom() {

	}

	public Atom(T initValue) {
		set(initValue);
	}

	@Override
	protected void onValueChanged(T oldValue, T newValue) {
		invalidateReactions();
	}
}
