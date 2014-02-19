package org.andork.model;

import org.andork.event.HierarchicalBasicPropertyChangeSupport;

public interface Model
{
	public HierarchicalBasicPropertyChangeSupport.External changeSupport( );
}
