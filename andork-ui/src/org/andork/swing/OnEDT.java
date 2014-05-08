package org.andork.swing;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

/**
 * Takes the pain out of writing {@link SwingUtilities#invokeAndWait(Runnable)} calls. Upon construction the {@link #run()} method will be called on the EDT,
 * and it may throw any exception, unlike {@link Runnable#run()}.
 * 
 * @author andy.edwards
 * 
 * @param <R>
 */
public abstract class OnEDT
{
	/**
	 * This constructor calls {@link #doRun()} on the EDT immediately so that you can save a few keystrokes.
	 * 
	 * @throws RuntimeInvocationTargetException
	 *             wrapping the exception thrown by {@link #doRun()}, if any
	 * @throws RuntimeInterruptedException
	 *             if the calling thread was interrupted while waiting for {@link SwingUtilities#invokeAndWait(Runnable)} to return.
	 */
	public OnEDT( )
	{
		if( SwingUtilities.isEventDispatchThread( ) )
		{
			callRun( );
		}
		else
		{
			try
			{
				SwingUtilities.invokeAndWait( new Runnable( )
				{
					@Override
					public void run( )
					{
						callRun( );
					}
				} );
			}
			catch( InvocationTargetException e )
			{
				// first cause is the RuntimeInvocationTargetException thrown
				// from run(); second cause is whatever doRun() threw. We want
				// to rewrap the second cause in a
				// RuntimeInvocationTargetException with a stack trace from this
				// method.
				throw new RuntimeInvocationTargetException( e.getCause( ).getCause( ) );
			}
			catch( InterruptedException e )
			{
				throw new RuntimeInterruptedException( e );
			}
		}
	}
	
	private void callRun( )
	{
		try
		{
			run( );
		}
		catch( Throwable t )
		{
			throw new RuntimeInvocationTargetException( t );
		}
	}
	
	public abstract void run( ) throws Throwable;
}
