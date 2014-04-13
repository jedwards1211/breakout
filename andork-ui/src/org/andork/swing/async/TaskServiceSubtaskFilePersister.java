package org.andork.swing.async;

import java.io.File;
import java.util.LinkedList;

public class TaskServiceSubtaskFilePersister<M> extends SubtaskFilePersister<M>
{
	TaskService	service;
	String		description;
	
	public TaskServiceSubtaskFilePersister( TaskService service , String description ,
			SubtaskStreamBimapperFactory<M, SubtaskStreamBimapper<M>> bimapperFactory , File file )
	{
		super( file , bimapperFactory );
		this.service = service;
		this.description = description;
	}
	
	protected void saveInBackground( final LinkedList<M> batch )
	{
		Task task = new Task( description )
		{
			@Override
			protected void execute( ) throws Exception
			{
				try
				{
					save( batch.getLast( ) , this );
				}
				finally
				{
					TaskServiceSubtaskFilePersister.this.batcher.doneHandling( batch );
				}
			}
		};
		task.setTotal( 1000 );
		service.submit( task );
	}
}
