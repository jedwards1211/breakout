package org.andork.swing;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import javax.swing.SwingUtilities;

import org.andork.generic.Ref;
import org.omg.CORBA.ObjectHolder;

/**
 * Takes the pain out of writing {@link SwingUtilities#invokeAndWait(Runnable)} calls. Upon construction {@link #run()} method will be called on the EDT, and it
 * may return a value or throw any exception, unlike {@link Runnable#run()}.
 * 
 * @author andy.edwards
 * 
 * @param <R>
 */
public abstract class FromEDT<R>
{
	private R	result;
	
	/**
	 * This constructor calls {@link #run()} on the EDT immediately so that you can save a few keystrokes.
	 * 
	 * @throws RuntimeInvocationTargetException
	 *             wrapping the exception thrown by {@link #run()}, if any
	 * @throws RuntimeInterruptedException
	 *             if the calling thread was interrupted while waiting for {@link SwingUtilities#invokeAndWait(Runnable)} to return.
	 */
	public FromEDT( )
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
			result = run( );
		}
		catch( Throwable t )
		{
			throw new RuntimeInvocationTargetException( t );
		}
	}
	
	/**
	 * You must implement this method; {@link #run()} will call it. This is a separate method so that it can have a return value and throws clause.
	 */
	public abstract R run( ) throws Throwable;
	
	/**
	 * @return the value returned by {@link #run()}.
	 */
	public final R result( )
	{
		return result;
	}
	
	public static <R> R fromEDT( Callable<R> c )
	{
		Ref<R> result = new Ref<>( );
		try
		{
			OnEDT.onEDT( ( ) -> result.value = c.call( ) );
		}
		catch( Exception ex )
		{
			
		}
		return result.value;
	}
}
