package org.andork.jogl;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLRunnable;
import javax.swing.SwingUtilities;

/**
 * Takes the pain out of writing {@link SwingUtilities#invokeAndWait(Runnable)} calls. Upon construction {@link #run()} method will be called on the EDT, and it
 * may return a value or throw any exception, unlike {@link Runnable#run()}.
 * 
 * @author andy.edwards
 * 
 * @param <R>
 */
public abstract class OnJogl
{
	/**
	 * This constructor calls {@link #run()} on the EDT immediately so that you can save a few keystrokes.
	 * 
	 * @throws RuntimeInvocationTargetException
	 *             wrapping the exception thrown by {@link #run()}, if any
	 * @throws RuntimeInterruptedException
	 *             if the calling thread was interrupted while waiting for {@link SwingUtilities#invokeAndWait(Runnable)} to return.
	 */
	public OnJogl( GLAutoDrawable drawable )
	{
		drawable.invoke( true , new GLRunnable( )
		{
			@Override
			public boolean run( GLAutoDrawable drawable )
			{
				OnJogl.this.callRun( drawable );
				return true;
			}
		} );
	}
	
	private final void callRun( GLAutoDrawable drawable )
	{
		try
		{
			run( drawable );
		}
		catch( Throwable t )
		{
			throw new RuntimeException( t );
		}
	}
	
	/**
	 * You must implement this method; {@link #run()} will call it. This is a separate method so that it can have a return value and throws clause.
	 */
	public abstract void run( GLAutoDrawable drawable ) throws Throwable;
}
