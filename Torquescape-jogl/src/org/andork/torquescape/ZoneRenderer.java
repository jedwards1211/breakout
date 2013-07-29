package org.andork.torquescape;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL3;

import org.andork.torquescape.model.Zone;

public class ZoneRenderer
{
	Zone					zone;
	
	List<ISliceRenderer>	sliceRenderers	= new ArrayList<ISliceRenderer>( );
	
	int						vertVBO;
	
	public ZoneRenderer( Zone zone )
	{
		super( );
		this.zone = zone;
	}
	
	public void init( GL3 gl )
	{
//		int[ ] buffers = new int[ 1 ];
//		gl.glGenBuffers( 1 , buffers , 0 );
//		vertVBO = buffers[ 0 ];
//		
//		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , vertVBO );
//		gl.glBufferData( GL3.GL_ARRAY_BUFFER , zone.vertBuffer.capacity( ) * 4 , zone.vertBuffer , GL3.GL_STATIC_DRAW );
	}
}
