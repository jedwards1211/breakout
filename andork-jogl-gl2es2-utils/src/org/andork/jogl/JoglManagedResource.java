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

import javax.media.opengl.GL2ES2;

public abstract class JoglManagedResource implements JoglResource
{
	private final JoglResourceManager manager;
	private int useCount;
	private boolean initialized;

	public JoglManagedResource( JoglResourceManager manager )
	{
		this.manager = manager;
	}

	/* (non-Javadoc)
	 * @see org.andork.jogl.neu.JoglResource#dispose(javax.media.opengl.GL2ES2)
	 */
	@Override
	public final void dispose( GL2ES2 gl )
	{
		requireInitialized( );
		initialized = false;
		doDispose( gl );
	}

	protected abstract void doDispose( GL2ES2 gl );

	protected abstract void doInit( GL2ES2 gl );

	protected void doRelease( )
	{

	}

	protected void doUse( )
	{

	}

	/* (non-Javadoc)
	 * @see org.andork.jogl.neu.JoglResource#init(javax.media.opengl.GL2ES2)
	 */
	@Override
	public final void init( GL2ES2 gl )
	{
		requireUninitialized( );
		initialized = true;
		doInit( gl );
	}

	public final boolean isInitialized( )
	{
		return initialized;
	}

	public final boolean isInUse( )
	{
		return useCount > 0;
	}

	public final void removeUser( Object user )
	{
		if( useCount > 0 && --useCount == 0 )
		{
			manager.disposeLater( this );
			doRelease( );
		}
	}

	protected final void requireInitialized( )
	{
		if( !initialized )
		{
			throw new IllegalArgumentException( "not initialized" );
		}
	}

	protected final void requireUninitialized( )
	{
		if( initialized )
		{
			throw new IllegalArgumentException( "already initialized" );
		}
	}

	public final void addUser( Object user )
	{
		if( useCount++ == 0 )
		{
			doUse( );
			manager.initLater( this );
		}
	}
}
