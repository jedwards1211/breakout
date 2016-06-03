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
package org.andork.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventListener;

/**
 * An adaptation of {@link PropertyChangeListener} to work around the need for
 * allocating {@link PropertyChangeEvent}s on the heap. All fields of the
 * {@link PropertyChangeEvent} are passed as method parameters, so they are
 * allocated on the stack. This may be significant when property changes are
 * occuring rapidly.<br>
 * <br>
 * Down with the heap!
 *
 * @author james.a.edwards
 */
public interface BasicPropertyChangeListener extends EventListener {
	/**
	 * This method gets called when a property is changed.
	 *
	 * @param source
	 *            the object whose property was changed.
	 * @param property
	 *            the property that was changed
	 * @param oldValue
	 *            the old value of the property (some callers may pass
	 *            {@code null}).
	 * @param newValue
	 *            the new value of the property (some callers may pass
	 *            {@code null}).
	 * @param index
	 *            the index of the property if applicable (if not, should be
	 *            {@code <0}).
	 */
	public void propertyChange(Object source, Object property, Object oldValue, Object newValue, int index);
}
