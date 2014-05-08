package org.andork.event;

public class HierarchicalBasicPropertyChangePropagator implements HierarchicalBasicPropertyChangeListener {
	Object									parent;
	HierarchicalBasicPropertyChangeSupport	parentChangeSupport;

	public HierarchicalBasicPropertyChangePropagator(Object parent, HierarchicalBasicPropertyChangeSupport parentChangeSupport) {
		super();
		this.parent = parent;
		this.parentChangeSupport = parentChangeSupport;
	}

	@Override
	public void propertyChange(Object source, Object property, Object oldValue, Object newValue, int index) {
		if (index < 0) {
			parentChangeSupport.firePropertyChange(new SourcePath(parent, source), property, oldValue, newValue);
		} else {
			parentChangeSupport.fireIndexedPropertyChange(new SourcePath(parent, source), property, index, oldValue, newValue);
		}
	}

	@Override
	public void childrenChanged(Object source, ChangeType changeType, Object... children) {
		parentChangeSupport.fireChildrenChanged(new SourcePath(parent, source), changeType, children);
	}
}
