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


/**
 * An interface for some UI element that allows the user to select a value. The
 * selection can be changed by {@link #setSelection(T)} or by user action. When
 * the selection is changed all registered {@link ISelectorListener}s will be
 * notified. <br>
 * <br>
 * For example, in the rollout strategy panel, one {@code ISelector} is a
 * {@link DefaultSelector}, which contains a single combo box with a list of the
 * user's positions. The other {@code ISelector} is an
 * {@link OptionPositionBuilder}, which allows the user to pick the action,
 * expiration date, strike price, and contract type and sets its selection to an
 * {@link IPosition} constructed from those values. These two selectors are
 * combined by a {@link TandemSelector}, which keeps them in sync with each
 * other and provides a single point to listen for selection changes.
 * 
 * @author james.a.edwards
 */
public interface ISelector<T> {
	/**
	 * Sets the selected position. If the new selection is different from the
	 * current selection, the {@link ISelectorListener}s will be notified.
	 * 
	 * @param newSelection
	 *            the new selection.
	 */
	public void setSelection(T newSelection);

	/**
	 * Sets whether the selector component is enabled for user interaction. Even
	 * if it is not enabled, the program will still be able to
	 * {@link #setSelection(Object)}.
	 */
	public void setEnabled(boolean enabled);

	/**
	 * Gets the selected position.
	 * 
	 * @return the selected position.
	 */
	public T getSelection();

	public void addSelectorListener(ISelectorListener<T> listener);

	public void removeSelectorListener(ISelectorListener<T> listener);
}