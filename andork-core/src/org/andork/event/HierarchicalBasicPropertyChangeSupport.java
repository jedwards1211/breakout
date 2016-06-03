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

import org.andork.event.HierarchicalBasicPropertyChangeListener.ChangeType;

@SuppressWarnings("serial")
public class HierarchicalBasicPropertyChangeSupport extends BasicPropertyChangeSupport {
	/**
	 *
	 */
	private static final long serialVersionUID = -7285881274645005690L;

	public void fireChildAdded(Object parent, Object... addedChildren) {
		if (listeners != null) {
			for (BasicPropertyChangeListener listener : listeners) {
				if (listener instanceof HierarchicalBasicPropertyChangeListener) {
					((HierarchicalBasicPropertyChangeListener) listener).childrenChanged(parent,
							ChangeType.CHILDREN_ADDED, addedChildren);
				}
			}
		}
	}

	public void fireChildRemoved(Object parent, Object... removedChildren) {
		if (listeners != null) {
			for (BasicPropertyChangeListener listener : listeners) {
				if (listener instanceof HierarchicalBasicPropertyChangeListener) {
					((HierarchicalBasicPropertyChangeListener) listener).childrenChanged(parent,
							ChangeType.CHILDREN_REMOVED, removedChildren);
				}
			}
		}
	}

	public void fireChildrenChanged(Object parent) {
		if (listeners != null) {
			for (BasicPropertyChangeListener listener : listeners) {
				if (listener instanceof HierarchicalBasicPropertyChangeListener) {
					((HierarchicalBasicPropertyChangeListener) listener).childrenChanged(parent,
							ChangeType.ALL_CHILDREN_CHANGED);
				}
			}
		}
	}

	public void fireChildrenChanged(Object parent, ChangeType changeType, Object... children) {
		if (listeners != null) {
			for (BasicPropertyChangeListener listener : listeners) {
				if (listener instanceof HierarchicalBasicPropertyChangeListener) {
					((HierarchicalBasicPropertyChangeListener) listener).childrenChanged(parent, changeType, children);
				}
			}
		}
	}
}
