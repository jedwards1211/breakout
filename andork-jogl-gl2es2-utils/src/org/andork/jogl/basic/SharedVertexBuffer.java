package org.andork.jogl.basic;

import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

public class SharedVertexBuffer
{
	ByteBuffer	buffer;
	
	int			vbo;
	
	int			usage		= GL.GL_STATIC_DRAW;
	
	boolean		initialized	= false;
	
	public int vbo( )
	{
		return vbo;
	}
	
	public ByteBuffer buffer( )
	{
		return buffer;
	}
	
	public SharedVertexBuffer buffer( ByteBuffer buffer )
	{
		this.buffer = buffer;
		return this;
	}
	
	public int usage( )
	{
		return usage;
	}
	
	public SharedVertexBuffer usage( int usage )
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
			vbo = vbos[ 0 ];
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
		gl.glBindBuffer( GL.GL_ARRAY_BUFFER , vbo );
		gl.glBufferData( GL.GL_ARRAY_BUFFER , buffer.capacity( ) , buffer , usage );
		gl.glBindBuffer( GL.GL_ARRAY_BUFFER , 0 );
	}
	
	public void destroy( GL2ES2 gl )
	{
		if( initialized )
		{
			gl.glDeleteBuffers( 1 , new int[ ] { vbo } , 0 );
			initialized = false;
		}
	}
}
