package org.andork.bind2.ui;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;
import org.andork.swing.selector.ISelector;

public class ISelectorSelectionBinding<T> implements Binding {
	public final Link<T> valueLink = new Link<T>(this);
	public final ISelector<T> selector;

	public ISelectorSelectionBinding(ISelector<T> selector) {
		super();
		this.selector = selector;
	}

	@Override
	public void update(boolean force) {
		selector.setSelection(valueLink.get());
	}
}
