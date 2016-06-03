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

public class HierarchicalBasicPropertyChangePropagator implements HierarchicalBasicPropertyChangeListener {
	Object parent;
	HierarchicalBasicPropertyChangeSupport parentChangeSupport;

	public HierarchicalBasicPropertyChangePropagator(Object parent,
			HierarchicalBasicPropertyChangeSupport parentChangeSupport) {
		super();
		this.parent = parent;
		this.parentChangeSupport = parentChangeSupport;
	}

	@Override
	public void childrenChanged(Object source, ChangeType changeType, Object... children) {
		parentChangeSupport.fireChildrenChanged(new SourcePath(parent, source), changeType, children);
	}

	@Override
	public void propertyChange(Object source, Object property, Object oldValue, Object newValue, int index) {
		if (index < 0) {
			parentChangeSupport.firePropertyChange(new SourcePath(parent, source), property, oldValue, newValue);
		} else {
			parentChangeSupport.fireIndexedPropertyChange(new SourcePath(parent, source), property, index, oldValue,
					newValue);
		}
	}
}
