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
package org.andork.jogl.neu;

import javax.media.opengl.GL2ES2;

public abstract class JoglManagedResource implements JoglResource
{
	private final JoglResourceManager	manager;
	private int							useCount;
	
	public JoglManagedResource( JoglResourceManager manager )
	{
		this.manager = manager;
	}
	
	public void use( )
	{
		if( useCount++ == 0 )
		{
			manager.initLater( this );
		}
	}
	
	public void release( )
	{
		if( useCount > 0 && --useCount == 0 )
		{
			manager.disposeLater( this );
		}
	}
	
	/* (non-Javadoc)
	 * @see org.andork.jogl.neu.JoglResource#init(javax.media.opengl.GL2ES2)
	 */
	@Override
	public abstract void init( GL2ES2 gl );
	
	/* (non-Javadoc)
	 * @see org.andork.jogl.neu.JoglResource#dispose(javax.media.opengl.GL2ES2)
	 */
	@Override
	public abstract void dispose( GL2ES2 gl );
}
