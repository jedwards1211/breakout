package org.andork.bind2;

import java.util.Objects;

public class CachingBinder<T> extends Binder<T> {
	T value;

	public CachingBinder() {

	}

	public CachingBinder(T initValue) {
		value = initValue;
	}

	@Override
	public T get() {
		return value;
	}

	protected void set(T newValue) {
		set(newValue, false);
	}

	protected void set(T newValue, boolean forceUpdates) {
		if (!Objects.equals(value, newValue) || forceUpdates) {
			value = newValue;
			updateBindings(forceUpdates);
		}
	}
}
