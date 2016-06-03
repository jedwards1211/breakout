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

import java.util.List;

import javax.swing.JComboBox;

import org.andork.util.Java7;

public class JComboBoxUtils {
	public static void addItemIfMissing(JComboBox comboBox, Object item) {
		if (indexOf(comboBox, item) < 0) {
			comboBox.addItem(item);
		}
	}

	public static int indexOf(JComboBox comboBox, Object item) {
		for (int i = 0; i < comboBox.getItemCount(); i++) {
			if (Java7.Objects.equals(comboBox.getItemAt(i), item)) {
				return i;
			}
		}
		return -1;
	}

	private static int indexOf(List<?> items, Object item) {
		int i = 0;
		for (Object o : items) {
			if (Java7.Objects.equals(item, o)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public static void setItemsWithoutChangingSelectionIfPossible(JComboBox comboBox, List<?> newItems) {
		for (int i = comboBox.getItemCount() - 1; i >= 0; i--) {
			if (comboBox.getSelectedIndex() != i) {
				comboBox.removeItemAt(i);
			}
		}

		int selIndex = -1;
		if (comboBox.getSelectedItem() != null) {
			selIndex = indexOf(newItems, comboBox.getSelectedItem());
		}
		if (selIndex < 0 && comboBox.getItemCount() == 1) {
			comboBox.removeItemAt(0);
		}

		for (int i = 0; i < newItems.size(); i++) {
			if (i < selIndex) {
				comboBox.insertItemAt(newItems.get(i), i);
			} else if (i > selIndex) {
				comboBox.addItem(newItems.get(i));
			}
		}

		if (selIndex < 0) {
			comboBox.setSelectedItem(null);
		}
	}

	public static void setSelectionOrClearIfMissing(JComboBox comboBox, Object desiredSelection) {
		comboBox.setSelectedItem(indexOf(comboBox, desiredSelection) >= 0 ? desiredSelection : null);
	}
}
