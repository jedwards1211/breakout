package org.andork.event;

public interface HierarchicalBasicPropertyChangeListener extends BasicPropertyChangeListener {
	public static enum ChangeType {
		CHILD_ADDED,
		CHILD_REMOVED,
		ALL_CHILDREN_CHANGED;
	}

	public void childrenChanged(Object source, ChangeType changeType, Object child);
}
