package org.andork.swing.async;

import java.util.List;

import org.andork.event.HierarchicalBasicPropertyChangeSupport;

public interface TaskService
{
	public void submit( Task task );
	
	public void cancel( Task task );
	
	public boolean hasTasks( );
	
	public List<Task> getTasks( );
	
	public HierarchicalBasicPropertyChangeSupport.External changeSupport( );
}
