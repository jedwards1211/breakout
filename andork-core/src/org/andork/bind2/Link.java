package org.andork.bind2;

/**
 * Contains the boilerplate code for changing the {@link Binder} a
 * {@link Binding} is bound to.
 *
 * @author andy.edwards
 * @param <T>
 *            the value type.
 */
public class Link<T> {
	private Binder<? extends T> binder;
	private final Binding binding;

	public Link(Binding binding) {
		super();
		this.binding = binding;
	}

	public <B extends Binder<? extends T>> B bind(B binder) {
		if (this.binder != binder) {
			if (this.binder != null) {
				this.binder.removeBinding(binding);
			}
			this.binder = binder;
			if (binder != null) {
				binder.addBinding(binding);
			}
		}
		return binder;
	}

	public Binder<? extends T> binder() {
		return binder;
	}

	public T get() {
		return binder == null ? null : binder.get();
	}

	public void unbind() {
		bind(null);
	}
}
