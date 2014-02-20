package org.andork.model;

import org.andork.event.HierarchicalBasicPropertyChangeSupport;

public interface HasChangeSupport
{
	public HierarchicalBasicPropertyChangeSupport.External changeSupport( );
}
