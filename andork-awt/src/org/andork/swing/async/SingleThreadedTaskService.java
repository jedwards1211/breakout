package org.andork.swing.async;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.andork.event.BasicPropertyChangeSupport.External;
import org.andork.event.HierarchicalBasicPropertyChangePropagator;
import org.andork.event.HierarchicalBasicPropertyChangeSupport;

public class SingleThreadedTaskService implements TaskService
{
	private final HierarchicalBasicPropertyChangeSupport	propertyChangeSupport	= new HierarchicalBasicPropertyChangeSupport( );
	private final HierarchicalBasicPropertyChangePropagator	propagator				= new HierarchicalBasicPropertyChangePropagator( this , propertyChangeSupport );
	
	private final Object									lock					= new Object( );
	
	private Thread											thread;
	private final List<Task>								taskQueue				= new ArrayList<Task>( );
	private int												keepAliveTime			= 1000;
	
	private void fireTaskAdded( final Task task )
	{
		SwingUtilities.invokeLater( new Runnable( )
		{
			@Override
			public void run( )
			{
				propertyChangeSupport.fireChildAdded( SingleThreadedTaskService.this , task );
			}
		} );
	}
	
	private void fireTaskRemoved( final Task task )
	{
		SwingUtilities.invokeLater( new Runnable( )
		{
			@Override
			public void run( )
			{
				propertyChangeSupport.fireChildRemoved( SingleThreadedTaskService.this , task );
			}
		} );
	}
	
	@Override
	public void submit( Task task )
	{
		synchronized( lock )
		{
			task.setService( this );
			taskQueue.add( task );
			task.changeSupport( ).addPropertyChangeListener( propagator );
			lock.notifyAll( );
			
			if( thread == null )
			{
				thread = new Thread( new Runner( ) );
				thread.setName( getClass( ).getSimpleName( ) + " thread" );
				thread.start( );
			}
		}
		fireTaskAdded( task );
	}
	
	@Override
	public void cancel( Task task )
	{
		Task removed = null;
		synchronized( lock )
		{
			Task.State state = task.getState( );
			if( state != Task.State.CANCELED && state != Task.State.CANCELING )
			{
				task.cancel( );
			}
			int index = taskQueue.indexOf( task );
			if( index == 0 )
			{
				thread.interrupt( );
			}
			else if( index > 0 )
			{
				removed = taskQueue.remove( index );
			}
		}
		if( removed != null )
		{
			fireTaskRemoved( removed );
		}
	}
	
	@Override
	public External changeSupport( )
	{
		return propertyChangeSupport.external( );
	}
	
	private class Runner implements Runnable
	{
		@Override
		public void run( )
		{
			while( true )
			{
				try
				{
					Task task;
					
					synchronized( lock )
					{
						if( taskQueue.isEmpty( ) )
						{
							lock.wait( keepAliveTime );
						}
						if( taskQueue.isEmpty( ) )
						{
							thread = null;
							return;
						}
						task = taskQueue.get( 0 );
					}
					
					try
					{
						task.run( );
					}
					catch( Throwable t )
					{
						t.printStackTrace( );
					}
					
					synchronized( lock )
					{
						task.changeSupport( ).removePropertyChangeListener( propagator );
						taskQueue.remove( 0 );
						lock.notifyAll( );
					}
					
					fireTaskRemoved( task );
				}
				catch( Throwable t )
				{
					t.printStackTrace( );
				}
			}
		}
	}
	
	public boolean hasTasks( )
	{
		synchronized( lock )
		{
			return !taskQueue.isEmpty( );
		}
	}
	
	@Override
	public List<Task> getTasks( )
	{
		synchronized( lock )
		{
			return new ArrayList<Task>( taskQueue );
		}
	}
}