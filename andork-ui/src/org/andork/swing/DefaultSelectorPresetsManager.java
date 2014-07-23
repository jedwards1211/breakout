/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.swing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.SwingUtilities;

import org.andork.collect.CollectionUtils;
import org.andork.func.Mapper;
import org.andork.swing.selector.DefaultSelector;

/**
 * A {@link DefaultSelector} with an editable combo box that manages presets for
 * some user settings. The way settings are persisted is up to the
 * implementation.<br>
 * <br>
 * 
 * Whenever the user types a name in the combo box, if a preset with that name
 * already exists (case insensitive), it will switch to it. Otherwise, it will
 * create a new preset with that name and save it.<br>
 * <br>
 * 
 * The implementation must specify a {@link #getDefaultPreset() default preset}
 * and an {@link #getUntitledPreset() untitled preset}. Those will be the first
 * two elements of the combo box. When the default preset is selected, if the
 * user changes settings and {@link #saveCurrentPreset()} is called, it will
 * automatically switch to the untitled preset and overwrite it with the current
 * settings.
 * 
 * @author andy.edwards
 * 
 * @param <T>
 *            the preset type.
 */
public abstract class DefaultSelectorPresetsManager<T> {
	private boolean				ignoreChange;

	private DefaultSelector<T>	selector;

	private ChangeHandler		changeHandler;

	public DefaultSelectorPresetsManager() {
		selector = new DefaultSelector<T>();
		selector.addAvailableValue(0, getDefaultPreset());
		selector.addAvailableValue(1, getUntitledPreset());
		selector.getComboBox().setEditable(true);

		selector.setSelection(getDefaultPreset());

//		selector.getComboBox().setUI(new SpecialComboBoxUI());

		changeHandler = new ChangeHandler();
		selector.getComboBox().addItemListener(changeHandler);
	}

	public DefaultSelector<T> getSelector() {
		return selector;
	}

	protected abstract Class<T> getPresetClass();

	protected abstract T getDefaultPreset();

	protected abstract T getUntitledPreset();

	protected abstract void loadPreset(T preset);

	protected abstract T storePreset(T preset);

	protected abstract void serialize(T preset);

	protected abstract String getName(T preset);

	protected abstract T createNewPreset(String name);

	public void saveCurrentPreset() {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new RuntimeException("Must be called from EDT");
		}

		if (ignoreChange) {
			return;
		}

		T selected = selector.getSelection();

		if (selected == getDefaultPreset()) {
			ignoreChange = true;
			try {
				selected = getUntitledPreset();
				selector.setSelection(selected);
			} finally {
				ignoreChange = false;
			}
		}

		if (selected != null) {
			T stored = storePreset(selected);

			if (stored != selected) {
				ignoreChange = true;
				try {
					selected = stored;
					selector.setSelection(selected);
				} finally {
					ignoreChange = false;
				}
			}

			if (selected != getUntitledPreset()) {
				serialize(selected);
			}
		}
	}

	protected int search(String name) {
		List<T> presets = selector.getAvailableValues();

		String lcname = name.toLowerCase();

		if (lcname.equals(getName(getDefaultPreset()).toLowerCase())) {
			return 0;
		} else if (lcname.equals(getName(getUntitledPreset()).toLowerCase())) {
			return 1;
		}

		// find the index of the preset with the same name, or
		// sorted insert index if none exists
		return CollectionUtils.binarySearch(presets, 2, presets.size(), new Mapper<T, String>() {
			@Override
			public String map(T in) {
				return getName(in).toLowerCase();
			}
		}, lcname);
	}

	private class ChangeHandler implements ItemListener {
		@SuppressWarnings("unchecked")
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (ignoreChange || e.getStateChange() == ItemEvent.DESELECTED) {
				return;
			}

			Object item = selector.getComboBox().getSelectedItem();
			if (item != null) {
				T preset = null;

				if (getPresetClass().isInstance(item)) {
					preset = (T) item;
					// the user just selected a different item

					ignoreChange = true;
					try {
						loadPreset(preset);
					} finally {
						ignoreChange = false;
					}
				} else {
					String name = (String) item;
					List<T> presets = selector.getAvailableValues();

					// find the index of the preset with the same name, or
					// sorted insert index if none exists
					int index = search(name);

					if (index >= 0) {
						// the user typed the name of an existing preset into
						// the combo box

						preset = presets.get(index);

						// set the selection to the object, not the name
						// (this will re-notify this listener)
						selector.setSelection(preset);
					} else {
						// the user typed a new preset name into the combo box

						preset = createNewPreset(name);
						preset = storePreset(preset);
						serialize(preset);

						ignoreChange = true;
						try {
							selector.addAvailableValue(-(index + 1), preset);
							selector.setSelection(preset);
						} finally {
							ignoreChange = false;
						}
					}
				}
			}
		}
	}
}
