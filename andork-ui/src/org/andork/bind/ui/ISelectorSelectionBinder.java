package org.andork.bind.ui;

import org.andork.bind.Binder;
import org.andork.swing.selector.ISelector;
import org.andork.swing.selector.ISelectorListener;
import org.andork.util.Java7;

public class ISelectorSelectionBinder<T> extends Binder<T> implements ISelectorListener<T> {
	Binder<T>		upstream;
	ISelector<T>	selector;

	public ISelectorSelectionBinder(ISelector<T> selector) {
		this.selector = selector;
		if (this.selector != null) {
			this.selector.addSelectorListener(this);
		}
	}

	public static <T> ISelectorSelectionBinder<T> bind(ISelector<T> selector, Binder<T> upstream) {
		return new ISelectorSelectionBinder<T>(selector).bind(upstream);
	}

	public ISelectorSelectionBinder<T> bind(Binder<T> upstream) {
		if (this.upstream != upstream) {
			if (this.upstream != null) {
				unbind(this.upstream, this);
			}
			this.upstream = upstream;
			if (this.upstream != null) {
				bind(this.upstream, this);
			}

			update(false);
		}
		return this;
	}

	boolean	updating;

	@Override
	public T get() {
		return selector == null ? null : selector.getSelection();
	}

	@Override
	public void set(T newValue) {
		if (selector != null) {
			selector.setSelection(newValue);
		}
	}

	@Override
	public void update(boolean force) {
		updating = true;
		try {
			T newValue = upstream == null ? null : upstream.get();
			if (force || !Java7.Objects.equals(get(), newValue)) {
				set(newValue);
			}
		} finally {
			updating = false;
		}
	}

	@Override
	public void selectionChanged(ISelector<T> selector, T oldSelection, T newSelection) {
		if (!updating && upstream != null && selector == this.selector) {
			upstream.set(get());
		}
	}
}
