/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
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
public abstract class FromJogl<R>
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
	public FromJogl( GLAutoDrawable drawable )
	{
		drawable.invoke( true , new GLRunnable( )
		{
			@Override
			public boolean run( GLAutoDrawable drawable )
			{
				callRun( drawable );
				return true;
			}
		} );
	}
	
	private void callRun( GLAutoDrawable drawable )
	{
		try
		{
			result = run( drawable );
		}
		catch( Throwable t )
		{
			throw new RuntimeException( t );
		}
	}
	
	/**
	 * You must implement this method; {@link #run()} will call it. This is a separate method so that it can have a return value and throws clause.
	 */
	public abstract R run( GLAutoDrawable drawable ) throws Throwable;
	
	/**
	 * @return the value returned by {@link #run()}.
	 */
	public final R result( )
	{
		return result;
	}
}
