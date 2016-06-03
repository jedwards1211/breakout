package org.andork.bind2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ListBinder<T> extends Binder<List<T>> implements Binding {
	private final List<Link<T>> modifiableLinks;
	public final List<Link<T>> links;
	private final List<T> values;
	private final List<T> unmodifiableValues;

	public ListBinder(Collection<? extends Binder<? extends T>> inputs) {
		this(inputs.size());
		int i = 0;
		for (Binder<? extends T> input : inputs) {
			links.get(i++).bind(input);
		}
	}

	public ListBinder(int length) {
		modifiableLinks = new ArrayList<>(length);
		links = Collections.unmodifiableList(modifiableLinks);
		values = new ArrayList<>(length);
		unmodifiableValues = Collections.unmodifiableList(values);
		for (int i = 0; i < length; i++) {
			modifiableLinks.add(new Link<>(this));
			values.add(null);
		}
	}

	@Override
	public List<T> get() {
		return unmodifiableValues;
	}

	@Override
	public void update(boolean force) {
		boolean changed = false;

		for (int i = 0; i < links.size(); i++) {
			T oldValue = values.get(i);
			T newValue = links.get(i).get();
			if (!Objects.equals(oldValue, newValue)) {
				changed = true;
				values.set(i, newValue);
			}
		}

		if (changed || force) {
			updateBindings(force);
		}
	}
}
