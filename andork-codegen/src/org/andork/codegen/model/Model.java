package org.andork.codegen.model;

import org.andork.event.HierarchicalBasicPropertyChangeSupport;

public interface Model {
	public HierarchicalBasicPropertyChangeSupport getPropertyChangeSupport();
}
