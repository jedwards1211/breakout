package org.andork.bind2;

public class BinderHolder<T> extends CachingBinder<T> implements Binding {
	public final Link<T> binderLink = new Link<T>(this);

	@Override
	public void update(boolean force) {
		set(binderLink.get(), force);
	}
}
