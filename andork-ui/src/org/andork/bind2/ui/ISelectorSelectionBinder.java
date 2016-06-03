package org.andork.bind2.ui;

import org.andork.bind2.Binder;
import org.andork.swing.selector.ISelector;
import org.andork.swing.selector.ISelectorListener;

public class ISelectorSelectionBinder<T> extends Binder<T> implements ISelectorListener<T> {
	private ISelector<T> selector;

	public ISelectorSelectionBinder() {
	}

	public ISelectorSelectionBinder(ISelector<T> selector) {
		this();
		bind(selector);
	}

	public void bind(ISelector<T> selector) {
		if (this.selector != selector) {
			if (this.selector != null) {
				this.selector.removeSelectorListener(this);
			}
			this.selector = selector;
			if (selector != null) {
				selector.addSelectorListener(this);
			}
		}
	}

	@Override
	public T get() {
		return selector.getSelection();
	}

	@Override
	public void selectionChanged(ISelector<T> selector, T oldSelection, T newSelection) {
		updateBindings(false);
	}
}
