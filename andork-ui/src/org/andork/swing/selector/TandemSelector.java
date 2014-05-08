package org.andork.swing.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.andork.util.Java7;

/**
 * A {@link ISelector} that combines several {@link ISelector}s, keeps them in
 * sync, and fires a single event when any of them changes.
 * 
 * @author james.a.edwards
 */
public class TandemSelector<T> implements ISelector<T> {
	private final List<ISelector<T>>			selectors			= new ArrayList<ISelector<T>>();
	private final List<ISelectorListener<T>>	listeners			= new ArrayList<ISelectorListener<T>>();

	private T									selection;

	private boolean								disableListeners	= false;

	public TandemSelector(ISelector<T>... selectors) {
		for (ISelector<T> selector : selectors) {
			this.selectors.add(selector);
		}
		init();
	}

	private void init() {
		ISelectorListener<T> selectorListener = new ISelectorListener<T>() {
			public void selectionChanged(ISelector<T> selector, T oldSelection, T newSelection) {
				if (!disableListeners) {
					setSelection(selector.getSelection(), selector);
				}
			}
		};

		for (int i = 0; i < selectors.size(); i++) {
			selectors.get(i).addSelectorListener(selectorListener);
		}
	}

	protected void notifySelectionChanged(T newSelection) {
		for (ISelectorListener<T> listener : listeners) {
			try {
				listener.selectionChanged(this, null, newSelection);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void addSelectorListener(ISelectorListener<T> listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeSelectorListener(ISelectorListener<T> listener) {
		listeners.remove(listener);
	}

	public void setSelection(T newSelection) {
		setSelection(newSelection, null);
	}

	protected void setSelection(T newSelection, ISelector<T> source) {
		if (!Java7.Objects.equals(newSelection, selection)) {
			selection = newSelection;
			disableListeners = true;
			try {
				for (ISelector<T> selector : selectors) {
					if (selector != source) {
						try {
							selector.setSelection(newSelection);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			} finally {
				disableListeners = false;
			}
			notifySelectionChanged(newSelection);
		}
	}

	public T getSelection() {
		return selection;
	}

	public List<ISelector<T>> getAvailableSelectors() {
		return Collections.unmodifiableList(selectors);
	}

	public void setEnabled(boolean enabled) {
		for (ISelector<T> selector : selectors) {
			selector.setEnabled(enabled);
		}
	}

}
