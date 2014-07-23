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
package org.andork.swing.selector;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

/**
 * Gets rid of DefaultComboBoxModel's silly behavior of setting the selection in
 * the constructors and addElement() (but not in insertElementAt(), LOL!)
 * 
 * @author james.a.edwards
 */
@SuppressWarnings("serial")
public class BetterComboBoxModel extends DefaultComboBoxModel {
	/**
	 * Constructs an empty BetterComboBoxModel object.
	 */
	public BetterComboBoxModel() {
		super();
	}

	/**
	 * Constructs a BetterComboBoxModel object initialized with an array of
	 * objects.
	 * 
	 * @param items
	 *            an array of Object objects
	 */
	public BetterComboBoxModel(final Object items[]) {
		super(items);
		setSelectedItem(null);
	}

	/**
	 * Constructs a BetterComboBoxModel object initialized with a vector.
	 * 
	 * @param v
	 *            a Vector object ...
	 */
	public BetterComboBoxModel(Vector<?> v) {
		super(v);
		setSelectedItem(null);
	}

	public void addElement(Object anObject) {
		insertElementAt(anObject, getSize());
	}
}
