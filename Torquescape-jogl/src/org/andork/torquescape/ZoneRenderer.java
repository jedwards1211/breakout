package org.andork.torquescape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL3;

import org.andork.torquescape.model.ISlice;
import org.andork.torquescape.model.StandardSlice;
import org.andork.torquescape.model.Zone;

import static org.andork.torquescape.GLUtils.*;

public class ZoneRenderer
{
	private static Map<Class<? extends ISlice>, ISliceRendererFactory<?>>	sliceRendererFactories	= new HashMap<Class<? extends ISlice>, ISliceRendererFactory<?>>( );
	
	static
	{
		sliceRendererFactories.put( StandardSlice.class , StandardSliceRenderer.FACTORY );
	}
	
	Zone																	zone;
	
	List<ISliceRenderer<?>>													sliceRenderers			= new ArrayList<ISliceRenderer<?>>( );
	
	int																		vertVbo;
	
	public ZoneRenderer( Zone zone )
	{
		super( );
		this.zone = zone;
		
		for( ISlice slice : zone.slices )
		{
			ISliceRendererFactory<ISlice> rendererFactory = ( ISliceRendererFactory<ISlice> ) sliceRendererFactories.get( slice.getClass( ) );
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
		gl.glBufferData( GL3.GL_ARRAY_BUFFER , zone.vertBuffer.capacity( ) * 4 , zone.vertBuffer , GL3.GL_STATIC_DRAW );
		checkGlError( gl , "glBufferData" );
		
		for( ISliceRenderer<?> sliceRenderer : sliceRenderers )
		{
			sliceRenderer.init( gl );
		}
	}
}
