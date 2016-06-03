package org.andork.bind2;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Supplier;

import org.andork.collect.WeakOrderedCollection;

/**
 * Binder is a source for a value that can change, and it will notify all
 * {@link Binding}s {{@link #addBinding(Binding) added} to it whenever that
 * value changes (by calling {@link Binding#update(boolean)}. What the value is
 * and when it changes depend on the implementations.<br>
 * <br>
 * Some {@code Binder}s are {@code Binding}s themselves, and derive their values
 * from the values of one or more source {@code Binder}s. {@link FunctionBinder}
 * is an example.<br>
 * <br>
 * {@link Binder}s are good for "binding" components of a view to properties of
 * a model, so that the view is properly updated whenever the model changes. A
 * typical way to do this is to have a {@link DefaultBinder} in which you set
 * the model, a {@code Binder/Binding}s bound to that for each property, and
 * finally {@code Binding}s bound to the property binders that update the view
 * components with the property binder values. Then if you change the model by
 * {@linkplain DefaultBinder#set(Object) setting} the model binder's value, it
 * will automatically perform all the necessary view updates.<br>
 * <br>
 * {@link Binder}s are also good for making sure that when one value depends on
 * many others, you won't forget to update the dependent value when any of the
 * other values change. Even multiple layers of dependent values can be handled
 * with ease. The same robustness could be accomplished without {@code Binder}s,
 * by creating an update method for each value in the chain that calls the
 * dependent update methods, but such an approach might have more boilerplate
 * code, and it is less likely that other programmers would remember to preserve
 * a well-defined system when maintaining/extending it.
 *
 * @author andy.edwards
 *
 * @param <T>
 *            the value type.
 */
public abstract class Binder<T> implements Supplier<T> {
	private Collection<Binding> bindings;
	private boolean updating;

	public Binder() {
		this(new LinkedList<>());
	}

	protected Binder(Collection<Binding> bindings) {
		this.bindings = bindings;
	}

	public final void addBinding(Binding binding) {
		bindings.add(binding);
		binding.update(false);
	}

	public Binder<T> convertToWeakReferencing() {
		WeakOrderedCollection<Binding> newBindings = WeakOrderedCollection.newLinkedCollection();
		newBindings.addAll(bindings);
		bindings = newBindings;
		return this;
	}

	@Override
	public abstract T get();

	public final boolean isUpdating() {
		return updating;
	}

	public final void removeBinding(Binding binding) {
		bindings.remove(binding);
		binding.update(false);
	}

	public final void updateBindings(boolean force) {
		if (!updating) {
			updating = true;
			try {
				for (Binding binding : bindings) {
					binding.update(force);
				}
			} finally {
				updating = false;
			}
		}
	}
}
