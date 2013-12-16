package org.andork.jogl.basic;

import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

public class SharedBuffer
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
	
	public void destroy( GL2ES2 gl )
	{
		if( initialized )
		{
			gl.glDeleteBuffers( 1 , new int[ ] { id } , 0 );
			initialized = false;
		}
	}
}
