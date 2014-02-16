package org.andork.event;

import org.andork.event.HierarchicalBasicPropertyChangeListener.ChangeType;

@SuppressWarnings("serial")
public class HierarchicalBasicPropertyChangeSupport extends BasicPropertyChangeSupport {
	public void fireChildAdded(Object parent, Object addedChild) {
		for (BasicPropertyChangeListener listener : listeners) {
			if (listener instanceof HierarchicalBasicPropertyChangeListener) {
				((HierarchicalBasicPropertyChangeListener) listener).childrenChanged(parent, ChangeType.CHILD_ADDED, addedChild);
			}
		}
	}

	public void fireChildRemoved(Object parent, Object removedChild) {
		for (BasicPropertyChangeListener listener : listeners) {
			if (listener instanceof HierarchicalBasicPropertyChangeListener) {
				((HierarchicalBasicPropertyChangeListener) listener).childrenChanged(parent, ChangeType.CHILD_REMOVED, removedChild);
			}
		}
	}

	public void fireChildrenChanged(Object parent) {
		for (BasicPropertyChangeListener listener : listeners) {
			if (listener instanceof HierarchicalBasicPropertyChangeListener) {
				((HierarchicalBasicPropertyChangeListener) listener).childrenChanged(parent, ChangeType.ALL_CHILDREN_CHANGED, null);
			}
		}
	}

	public void fireChildrenChanged(Object parent, ChangeType changeType, Object child) {
		for (BasicPropertyChangeListener listener : listeners) {
			if (listener instanceof HierarchicalBasicPropertyChangeListener) {
				((HierarchicalBasicPropertyChangeListener) listener).childrenChanged(parent, changeType, child);
			}
		}
	}
}
