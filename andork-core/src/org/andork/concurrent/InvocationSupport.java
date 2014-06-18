package org.andork.concurrent;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * Allows you to {@link #invokeLater(Runnable)} and {@link #invokeAndWait(Runnable)} on another thread, just like with the AWT {@link EventQueue}.
 * 
 * @author Andy
 */
public class InvocationSupport
{
	private final LinkedList<InvocationEvent>	queue		= new LinkedList<InvocationEvent>( );
	private final External						external	= new External( );
	
	public External external( )
	{
		return external;
	}
	
	public void pumpEvents( long duration , TimeUnit timeUnit )
	{
		duration = timeUnit.toNanos( duration );
		long startTime = System.nanoTime( );
		
		do
		{
			InvocationEvent event;
			synchronized( queue )
			{
				event = queue.poll( );
				if( event == null )
				{
					break;
				}
			}
			try
			{
				event.target.run( );
				synchronized( event )
				{
					event.hasRun = true;
					event.notify( );
				}
			}
			catch( Throwable t )
			{
				synchronized( event )
				{
					event.exception = t;
					event.hasRun = true;
					event.notify( );
				}
			}
		} while( System.nanoTime( ) < startTime + duration );
	}
	
	/**
	 * Called when the queue was empty before an {@link InvocationEvent} was added. This allows the implementation to make sure
	 * {@link #pumpEvents(long, TimeUnit)} will get called soon. This method will be called on whatever thread calls {@link External#invokeLater(Runnable)} or
	 * {@link External#invokeAndWait(Runnable)}, so it must be thread-safe.
	 */
	protected void onEnqueuedWhenEmpty( )
	{
	}
	
	protected void enqueue( InvocationEvent event )
	{
		boolean wasEmpty;
		synchronized( queue )
		{
			wasEmpty = queue.isEmpty( );
			queue.add( event );
		}
		if( wasEmpty )
		{
			onEnqueuedWhenEmpty( );
		}
	}
	
	public class External
	{
		public void invokeLater( Runnable r )
		{
			enqueue( new InvocationEvent( r ) );
		}
		
		public void invokeAndWait( Runnable r ) throws InvocationTargetException , InterruptedException
		{
			InvocationEvent event = new InvocationEvent( r );
			
			enqueue( event );
			
			synchronized( event )
			{
				while( !event.hasRun )
				{
					event.wait( );
				}
			}
			
			if( event.exception != null )
			{
				throw new InvocationTargetException( event.exception );
			}
		}
	}
	
	private class InvocationEvent
	{
		boolean		hasRun;
		Runnable	target;
		Throwable	exception;
		
		private InvocationEvent( Runnable target )
		{
			super( );
			this.target = target;
		}
	}
}
