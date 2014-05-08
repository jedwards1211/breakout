package org.andork.event;

public interface HierarchicalBasicPropertyChangeListener extends BasicPropertyChangeListener {
	public static enum ChangeType {
		CHILDREN_ADDED,
		CHILDREN_REMOVED,
		ALL_CHILDREN_CHANGED;
	}

	public void childrenChanged(Object source, ChangeType changeType, Object... children);
}
