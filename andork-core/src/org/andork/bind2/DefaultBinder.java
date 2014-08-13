package org.andork.bind2;

public class DefaultBinder<T> extends CachingBinder<T> {
	public DefaultBinder() {
	}

	public DefaultBinder(T initValue) {
		super(initValue);
	}

	@Override
	public void set(T newValue, boolean forceUpdates) {
		super.set(newValue, forceUpdates);
	}

	@Override
	public void set(T newValue) {
		super.set(newValue);
	}
}
