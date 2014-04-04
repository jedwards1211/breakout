package org.andork.event;

import org.andork.event.HierarchicalBasicPropertyChangeListener.ChangeType;

@SuppressWarnings("serial")
public class HierarchicalBasicPropertyChangeSupport extends BasicPropertyChangeSupport {
	public void fireChildAdded(Object parent, Object... addedChildren) {
		for (BasicPropertyChangeListener listener : listeners) {
			if (listener instanceof HierarchicalBasicPropertyChangeListener) {
				((HierarchicalBasicPropertyChangeListener) listener).childrenChanged(parent, ChangeType.CHILDREN_ADDED, addedChildren);
			}
		}
	}

	public void fireChildRemoved(Object parent, Object... removedChildren) {
		for (BasicPropertyChangeListener listener : listeners) {
			if (listener instanceof HierarchicalBasicPropertyChangeListener) {
				((HierarchicalBasicPropertyChangeListener) listener).childrenChanged(parent, ChangeType.CHILDREN_REMOVED, removedChildren);
			}
		}
	}

	public void fireChildrenChanged(Object parent) {
		for (BasicPropertyChangeListener listener : listeners) {
			if (listener instanceof HierarchicalBasicPropertyChangeListener) {
				((HierarchicalBasicPropertyChangeListener) listener).childrenChanged(parent, ChangeType.ALL_CHILDREN_CHANGED);
			}
		}
	}

	public void fireChildrenChanged(Object parent, ChangeType changeType, Object... children) {
		for (BasicPropertyChangeListener listener : listeners) {
			if (listener instanceof HierarchicalBasicPropertyChangeListener) {
				((HierarchicalBasicPropertyChangeListener) listener).childrenChanged(parent, changeType, children);
			}
		}
	}
}
