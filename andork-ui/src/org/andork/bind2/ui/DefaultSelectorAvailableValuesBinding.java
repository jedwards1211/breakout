package org.andork.bind2.ui;

import java.util.Collections;
import java.util.List;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;
import org.andork.swing.selector.DefaultSelector;

public class DefaultSelectorAvailableValuesBinding<T> implements Binding {
	public final Link<List<? extends T>>	availableValuesLink	= new Link<List<? extends T>>(this);
	public final DefaultSelector<T>			selector;

	public DefaultSelectorAvailableValuesBinding(DefaultSelector<T> selector) {
		super();
		this.selector = selector;
	}

	public void update(boolean force) {
		List<? extends T> values = availableValuesLink.get();
		if (values == null) {
			values = Collections.emptyList();
		}
		selector.setAvailableValues(values);
	}
}
