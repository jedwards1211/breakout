package org.andork.swing.async;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.andork.event.HierarchicalBasicPropertyChangeSupport;

public interface TaskService
{
	public void submit( Task task );
	
	public void cancel( Task task );
	
	public boolean hasTasks( );
	
	public List<Task> getTasks( );
	
	public HierarchicalBasicPropertyChangeSupport.External changeSupport( );
	
	public default void submit( TaskRunnable task )
	{
		submit( new Task( )
		{
			@Override
			protected void execute( ) throws Exception
			{
				task.execute( this );
			}
		} );
	}
	
	public default void invokeAndWait( TaskRunnable task ) throws InterruptedException , ExecutionException
	{
		Task actualTask = new Task( )
		{
			@Override
			protected void execute( ) throws Exception
			{
				task.execute( this );
			}
		};
		
		submit( actualTask );
		actualTask.waitUntilHasFinished( );
	}
	
	public default void onThread( TaskRunnable task )
	{
		try
		{
			invokeAndWait( task );
		}
		catch( Exception ex )
		{
			throw new RuntimeException( ex );
		}
	}
	
	public default <R> R invokeAndGet( TaskSupplier<R> supplier ) throws InterruptedException , ExecutionException
	{
		FutureTask<R> task = new FutureTask<R>( )
		{
			@Override
			protected R doGet( ) throws Exception
			{
				return supplier.get( this );
			}
		};
		
		return task.get( );
	}
	
	public default <R> R fromThread( TaskSupplier<R> supplier )
	{
		try
		{
			return invokeAndGet( supplier );
		}
		catch( Exception ex )
		{
			throw new RuntimeException( ex );
		}
	}
}
