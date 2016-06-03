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

/**
 * Interface for listening to an {@link ISelector}.
 *
 * @author james.a.edwards
 */
public interface ISelectorListener<T> {
	/**
	 * Called by an {@link ISelector} this listener is registered on when the
	 * selection changes.
	 *
	 * @param selector
	 *            the {@code ISelector} whose selection changed.
	 * @param oldSelection
	 *            the previous selected value.
	 * @param newSelection
	 *            the new selected value.
	 */
	public void selectionChanged(ISelector<T> selector, T oldSelection, T newSelection);
}
