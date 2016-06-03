/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.swing.selector;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;

import org.andork.util.Java7;

/**
 * An {@link ISelector} controlled by a {@link JComboBox}. When the user selects
 * a different item in the combo box, {@link ISelectorListener}s will be
 * notified. When the program sets the selection, the combo box will also be
 * updated to that selection.<br>
 * <br>
 * If there are no available values in this selector, the
 * {@link #setNothingAvailableItem(Object) nothingAvailableItem} will be
 * temporarily displayed in the combo box if it is not null. <br>
 * If there are values available, but the selection is null, the
 * {@link #setNullItem(Object) nullItem} will be temporarily displayed if it is
 * not null.<br>
 * If the selection is not null, but not one of the available values, the
 * {@link #setSelectionNotAvailableItem(Object) selectionNotAvailableItem} will
 * be temporarily displayed if it is not null.<br>
 * These items will not not affect the {@link #getSelection() selection} or the
 * {@link #setAvailableValues(List) available values}.<br>
 * <br>
 * Also, whenever only one value is available, the {@link #comboBox()} will be
 * disabled if {@code disableWhenOnlyOneAvailableItem} is
 * {@link #setDisableWhenOnlyOneAvailableItem(boolean) set} to {@code true}.
 *
 * @author james.a.edwards
 * @param <T>
 *            the selection type.
 */
public class DefaultSelector<T> implements ISelector<T> {
	private boolean disableListeners;

	private Object nullItem;

	private Object nothingAvailableItem;

	private Object selectionNotAvailableItem;
	private JComboBox comboBox;
	private final List<ISelectorListener<T>> listeners = new ArrayList<ISelectorListener<T>>();

	private final List<T> availableValues = new ArrayList<T>();

	private T selection;

	private boolean allowSelectionNotAvailable = false;
	private boolean enabled = true;

	private boolean disableWhenOnlyOneAvailableItem = false;

	public DefaultSelector() {
		this(new JComboBox());
	}

	public DefaultSelector(JComboBox comboBox) {
		this.comboBox = comboBox;
		init();
	}

	public void addAvailableValue(int index, T value) {
		availableValues.add(index, value);
		updateComboBoxAvailableItems();
	}

	public void addAvailableValue(T value) {
		availableValues.add(value);
		updateComboBoxAvailableItems();
	}

	@Override
	public void addSelectorListener(ISelectorListener<T> listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Gets the {@link JComboBox} controlled by this {@code DefaultSelector}.
	 * You should not need to listen directly for ItemEvents or other user input
	 * from the combo box; use an {@link ISelectorListener} or this instead.
	 * Only use this method to put the combo box into a layout.
	 *
	 * @return the combo box controlled by this {@code DefaultSelector}.
	 */
	public JComboBox comboBox() {
		return comboBox;
	}

	/**
	 * Gets the list of values available for user selection.
	 */
	public List<T> getAvailableValues() {
		return Collections.unmodifiableList(availableValues);
	}

	/**
	 * Gets the item displayed in the {@link #comboBox()} when no values are
	 * {@link #getAvailableValues() available}.
	 */
	public Object getNothingAvailableItem() {
		return nothingAvailableItem;
	}

	/**
	 * Gets the item displayed in the {@link #comboBox()} when the selection is
	 * {@link #setSelection(Object) set} to {@code null}.
	 */
	public Object getNullItem() {
		return nullItem;
	}

	/**
	 * @return the currently selected value.
	 */
	@Override
	public T getSelection() {
		return selection;
	}

	/**
	 * Gets the item displayed in the {@link #comboBox()} when the selection is
	 * {@link #setSelection(Object) set} to a value not contained in the list of
	 * {@link #getAvailableValues() available} values. However, if
	 * {@link #setAllowSelectionNotAvailable(boolean)} to {@code true}, this
	 * item will not be used.
	 */
	public Object getSelectionNotAvailableItem() {
		return selectionNotAvailableItem;
	}

	private void init() {
		comboBox.setModel(new BetterComboBoxModel());

		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (!disableListeners && e.getStateChange() == ItemEvent.SELECTED) {
					if (!isTransientItem(e.getItem()) && availableValues.contains(e.getItem())) {
						setSelection((T) e.getItem());
					}
				}
			}
		});

		updateComboBoxAvailableItems();
	}

	/**
	 * Whether this {@link DefaultSelector} currently "allows" the
	 * {@link #getSelection() selection} to be a value not contained in the list
	 * of {@link #getAvailableValues() available} values. If allowed, such a
	 * value will be displayed in the {@link #comboBox()} as normal. Otherwise,
	 * the {@link #getSelectionNotAvailableItem()} will be displayed.
	 */
	public boolean isAllowSelectionNotAvailable() {
		return allowSelectionNotAvailable;
	}

	public boolean isDisableWhenOnlyOneAvailableItem() {
		return disableWhenOnlyOneAvailableItem;
	}

	/**
	 * @return whether this {@link DefaultSelector} is enabled. Even if it is
	 *         enabled, the {@link #comboBox()} will be automatically disabled
	 *         if there is only one available item and
	 *         {@code disableWhenOnlyOneAvailableItem} is
	 *         {@link #setDisableWhenOnlyOneAvailableItem(boolean) set} to
	 *         {@code true}.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	protected boolean isTransientItem(Object o) {
		return o != null && (o == nullItem || o == nothingAvailableItem || o == selectionNotAvailableItem ||
				selection == o && allowSelectionNotAvailable && !availableValues.contains(selection));
	}

	protected void notifySelectionChanged(T oldSelection, T newSelection) {
		for (ISelectorListener<T> listener : listeners) {
			try {
				listener.selectionChanged(this, oldSelection, newSelection);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	protected Object pickItemToSelect() {
		Object newSelectedItem = null;
		if (allowSelectionNotAvailable && selection != null && !availableValues.contains(selection)) {
			newSelectedItem = selection;
		} else if (availableValues.isEmpty()) {
			newSelectedItem = nothingAvailableItem;
		} else if (selection == null) {
			newSelectedItem = nullItem;
		} else if (!availableValues.contains(selection)) {
			newSelectedItem = selectionNotAvailableItem;
		} else {
			newSelectedItem = selection;
			if (nullItem != null) {
				comboBox.removeItem(nullItem);
			}
			if (selectionNotAvailableItem != null) {
				comboBox.removeItem(selectionNotAvailableItem);
			}
			if (nothingAvailableItem != null) {
				comboBox.removeItem(nothingAvailableItem);
			}
		}
		return newSelectedItem;
	}

	public T removeAvailableValue(int index) {
		T result = availableValues.remove(index);
		updateComboBoxAvailableItems();
		return result;
	}

	@Override
	public void removeSelectorListener(ISelectorListener<T> listener) {
		listeners.remove(listener);
	}

	/**
	 * Sets whether to "allow" the {@link #getSelection() selection} to be a
	 * value not contained in the list of {@link #getAvailableValues()
	 * available} values. If allowed, such a value will be displayed in the
	 * {@link #comboBox()} as normal. Otherwise, the
	 * {@link #getSelectionNotAvailableItem()} will be displayed.
	 */
	public void setAllowSelectionNotAvailable(boolean useSelectionNotAvailableItem) {
		if (this.allowSelectionNotAvailable != useSelectionNotAvailableItem) {
			this.allowSelectionNotAvailable = useSelectionNotAvailableItem;
			updateComboBoxSelectedItem();
		}
	}

	public void setAvailableValue(int index, T value) {
		availableValues.set(index, value);
		updateComboBoxAvailableItems();
	}

	/**
	 * Sets the list of values available for user selection. If the selected
	 * value is not in the list, the selection will be cleared.
	 *
	 * @param newAvailableValues
	 *            the new list of available values.
	 */
	public void setAvailableValues(Collection<? extends T> newAvailableValues) {
		if (!availableValues.equals(newAvailableValues)) {
			availableValues.clear();
			availableValues.addAll(newAvailableValues);
			updateComboBoxAvailableItems();
		}
	}

	public <TT extends T> void setAvailableValues(TT... newAvailableValues) {
		setAvailableValues(Arrays.asList(newAvailableValues));
	}

	public void setDisableWhenOnlyOneAvailableItem(boolean disableWhenOnlyOneAvailableItem) {
		if (this.disableWhenOnlyOneAvailableItem != disableWhenOnlyOneAvailableItem) {
			this.disableWhenOnlyOneAvailableItem = disableWhenOnlyOneAvailableItem;
			updateComboBoxEnabled();
		}
	}

	/**
	 * Sets whether this {@link DefaultSelector} is enabled. Even if it is
	 * enabled, the {@link #comboBox()} will be automatically disabled if there
	 * is only one available item and {@code disableWhenOnlyOneAvailableItem} is
	 * {@link #setDisableWhenOnlyOneAvailableItem(boolean) set} to {@code true}.
	 */
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		updateComboBoxEnabled();
	}

	/**
	 * Sets the item displayed in the {@link #comboBox()} when no values are
	 * {@link #getAvailableValues() available}.
	 */
	public void setNothingAvailableItem(Object nothingAvailableItem) {
		if (this.nothingAvailableItem != nothingAvailableItem) {
			this.nothingAvailableItem = nothingAvailableItem;
			updateComboBoxSelectedItem();
		}
	}

	/**
	 * Sets the item displayed in the {@link #comboBox()} when the selection is
	 * {@link #setSelection(Object) set} to {@code null}.
	 */
	public void setNullItem(Object nullItem) {
		if (this.nullItem != nullItem) {
			this.nullItem = nullItem;
			updateComboBoxSelectedItem();
		}
	}

	/**
	 * Sets the selected value.
	 *
	 * @param newSelection
	 *            the new desired selection. Has no effect if it is not in the
	 *            list of available value.
	 */
	@Override
	public void setSelection(T newSelection) {
		if (!Java7.Objects.equals(newSelection, selection)) {
			T oldSelection = selection;
			selection = newSelection;
			updateComboBoxSelectedItem();
			notifySelectionChanged(oldSelection, newSelection);
		}
	}

	/**
	 * Sets the item displayed in the {@link #comboBox()} when the selection is
	 * {@link #setSelection(Object) set} to a value not contained in the list of
	 * {@link #getAvailableValues() available} values. However, if
	 * {@link #setAllowSelectionNotAvailable(boolean)} to {@code true}, this
	 * item will not be used.
	 */
	public void setSelectionNotAvailableItem(Object selectionNotAvailableItem) {
		if (this.selectionNotAvailableItem != selectionNotAvailableItem) {
			this.selectionNotAvailableItem = selectionNotAvailableItem;
			updateComboBoxSelectedItem();
		}
	}

	protected void updateComboBoxAvailableItems() {
		boolean listenersWereDisabled = disableListeners;
		disableListeners = true;
		try {
			comboBox.removeAllItems();
			for (T value : availableValues) {
				comboBox.addItem(value);
			}
			updateComboBoxSelectedItem();
		} finally {
			disableListeners = listenersWereDisabled;
		}
	}

	protected void updateComboBoxEnabled() {
		comboBox.setEnabled(enabled && (availableValues.size() > 1 ||
				availableValues.size() == 1 && !disableWhenOnlyOneAvailableItem));
	}

	protected void updateComboBoxSelectedItem() {
		boolean listenersWereDisabled = disableListeners;
		disableListeners = true;
		try {
			Object oldSelectedItem = comboBox.getSelectedItem();
			Object newSelectedItem = pickItemToSelect();

			if (oldSelectedItem != newSelectedItem) {
				if (isTransientItem(newSelectedItem)) {
					comboBox.insertItemAt(newSelectedItem, 0);
				}
				comboBox.setSelectedItem(newSelectedItem);
				if (isTransientItem(oldSelectedItem)) {
					comboBox.removeItem(oldSelectedItem);
				}
			}

			updateComboBoxEnabled();
		} finally {
			disableListeners = listenersWereDisabled;
		}
	}
}
