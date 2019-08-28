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
package org.andork.bind.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import org.andork.bind.Binder;
import org.andork.util.Java7;

public class JComboBoxItemBinder<T> extends Binder<T> implements ItemListener {
	public static <T> JComboBoxItemBinder<T> bind(JComboBox<T> comboBox, Binder<T> upstream) {
		return new JComboBoxItemBinder<T>(comboBox).bind(upstream);
	}

	Binder<T> upstream;

	JComboBox<T> comboBox;

	boolean updating;

	public JComboBoxItemBinder(JComboBox<T> comboBox) {
		this.comboBox = comboBox;
		if (this.comboBox != null) {
			this.comboBox.addItemListener(this);
		}
	}

	public JComboBoxItemBinder<T> bind(Binder<T> upstream) {
		if (this.upstream != upstream) {
			if (this.upstream != null) {
				unbind0(this.upstream, this);
			}
			this.upstream = upstream;
			if (this.upstream != null) {
				bind0(this.upstream, this);
			}

			update(false);
		}
		return this;
	}

	@Override
	public T get() {
		return comboBox == null ? null : (T) comboBox.getSelectedItem();
	}

	@Override
	public void set(T newValue) {
		if (comboBox != null) {
			comboBox.setSelectedItem(newValue);
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
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			if (!updating && upstream != null && e.getSource() == this.comboBox) {
				upstream.set(get());
			}
		}
	}
}
