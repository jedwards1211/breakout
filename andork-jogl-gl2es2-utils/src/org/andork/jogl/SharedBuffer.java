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

import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import org.andork.jogl.neu.JoglResource;

public class SharedBuffer implements JoglResource
{
	ByteBuffer	buffer;
	
	int			target		= GL.GL_ARRAY_BUFFER;
	
	int			id;
	
	int			usage		= GL.GL_STATIC_DRAW;
	
	boolean		initialized	= false;
	
	public int id( )
	{
		return id;
	}
	
	public int target( )
	{
		return target;
	}
	
	public SharedBuffer target( int target )
	{
		this.target = target;
		return this;
	}
	
	public SharedBuffer elementArray( )
	{
		return target( GL.GL_ELEMENT_ARRAY_BUFFER );
	}
	
	public ByteBuffer buffer( )
	{
		return buffer;
	}
	
	public SharedBuffer buffer( ByteBuffer buffer )
	{
		this.buffer = buffer;
		return this;
	}
	
	public int usage( )
	{
		return usage;
	}
	
	public SharedBuffer usage( int usage )
	{
		this.usage = usage;
		return this;
	}
	
	public void init( GL2ES2 gl )
	{
		if( !initialized )
		{
			int[ ] vbos = new int[ 1 ];
			gl.glGenBuffers( 1 , vbos , 0 );
			id = vbos[ 0 ];
			initialized = true;
			rebuffer( gl );
		}
	}
	
	public void rebuffer( GL2ES2 gl )
	{
		if( !initialized )
		{
			init( gl );
		}
		buffer.position( 0 );
		gl.glBindBuffer( target , id );
		gl.glBufferData( target , buffer.capacity( ) , buffer , usage );
		gl.glBindBuffer( target , 0 );
	}
	
	public void dispose( GL2ES2 gl )
	{
		if( initialized )
		{
			gl.glDeleteBuffers( 1 , new int[ ] { id } , 0 );
			initialized = false;
		}
	}
}
