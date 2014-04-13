package org.andork.swing.async;

import org.andork.func.StreamBimapper;

public abstract class SubtaskStreamBimapper<T> implements StreamBimapper<T>
{
	private Subtask	subtask;
	
	public SubtaskStreamBimapper( Subtask subtask )
	{
		if( subtask == null )
		{
			throw new IllegalArgumentException( "subtask must be non-null" );
		}
		this.subtask = subtask;
	}
	
	public Subtask subtask( )
	{
		return subtask;
	}
}
