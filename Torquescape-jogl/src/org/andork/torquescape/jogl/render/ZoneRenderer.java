package org.andork.torquescape.jogl.render;

import static org.andork.torquescape.jogl.GLUtils.checkGlError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL3;

import org.andork.torquescape.model.ISlice;
import org.andork.torquescape.model.StandardSlice;
import org.andork.torquescape.model.Zone;

public class ZoneRenderer
{
	private static Map<Class<? extends ISlice>, ISliceRendererFactory<?>>	sliceRendererFactories	= new HashMap<Class<? extends ISlice>, ISliceRendererFactory<?>>( );
	
	static
	{
		sliceRendererFactories.put( StandardSlice.class , StandardSliceRenderer.FACTORY );
	}
	
	public Zone																zone;
	
	public List<ISliceRenderer<?>>											sliceRenderers			= new ArrayList<ISliceRenderer<?>>( );
	
	public int																vertVbo;
	
	public ZoneRenderer( Zone zone )
	{
		super( );
		this.zone = zone;
		
		for( ISlice slice : zone.getSlices( ) )
		{
			ISliceRendererFactory<ISlice> rendererFactory = ( org.andork.torquescape.jogl.render.ISliceRendererFactory<ISlice> ) sliceRendererFactories.get( slice.getClass( ) );
			if( rendererFactory != null )
			{
				sliceRenderers.add( rendererFactory.create( this , slice ) );
			}
		}
	}
	
	public void init( GL3 gl )
	{
		int[ ] buffers = new int[ 1 ];
		gl.glGenBuffers( 1 , buffers , 0 );
		vertVbo = buffers[ 0 ];
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , vertVbo );
		checkGlError( gl , "glBindBuffer" );
		gl.glBufferData( GL3.GL_ARRAY_BUFFER , zone.getVertByteBuffer( ).capacity( ) , zone.getVertByteBuffer( ) , GL3.GL_STATIC_DRAW );
		checkGlError( gl , "glBufferData" );
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , 0 );
		
		for( ISliceRenderer<?> sliceRenderer : sliceRenderers )
		{
			sliceRenderer.init( gl );
		}
	}
}
