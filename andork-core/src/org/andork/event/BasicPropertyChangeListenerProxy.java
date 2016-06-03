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

public class BasicPropertyChangeListenerProxy extends java.util.EventListenerProxy
		implements BasicPropertyChangeListener {

	private Object propertyName;

	/**
	 * Constructor which binds the PropertyChangeListener to a specific
	 * property.
	 *
	 * @param listener
	 *            The listener object
	 * @param propertyName
	 *            The name of the property to listen on.
	 */
	public BasicPropertyChangeListenerProxy(Object propertyName, BasicPropertyChangeListener listener) {
		// XXX - msd NOTE: I changed the order of the arguments so that it's
		// similar to PropertyChangeSupport.addPropertyChangeListener(String,
		// PropertyChangeListener);
		super(listener);
		this.propertyName = propertyName;
	}

	/**
	 * Returns the name of the named property associated with the listener.
	 */
	public Object getPropertyName() {
		return propertyName;
	}

	/**
	 * Forwards the property change event to the listener delegate.
	 *
	 * @param evt
	 *            the property change event
	 */
	@Override
	public void propertyChange(Object source, Object propertyName, Object oldValue, Object newValue, int index) {
		((BasicPropertyChangeListener) getListener()).propertyChange(source, propertyName, oldValue, newValue, index);
	}

}
