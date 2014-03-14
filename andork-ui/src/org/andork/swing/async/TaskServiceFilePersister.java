package org.andork.swing.async;

import java.io.File;
import java.util.LinkedList;

import org.andork.func.Bimapper;
import org.andork.util.AbstractFilePersister;

public class TaskServiceFilePersister<M> extends AbstractFilePersister<M>
{
	TaskService	service;
	String		description;
	
	public TaskServiceFilePersister( TaskService service , String description , Bimapper<M, String> format , File file )
	{
		super( file , format );
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
					save( batch.getLast( ) );
				}
				finally
				{
					TaskServiceFilePersister.this.batcher.doneHandling( batch );
				}
			}
		};
		service.submit( task );
	}
}
