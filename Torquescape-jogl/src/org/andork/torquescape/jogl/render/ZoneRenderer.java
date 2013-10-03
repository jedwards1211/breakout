package org.andork.torquescape.jogl.render;

import static org.andork.jogl.util.GLUtils.checkGLError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL3;

import org.andork.jogl.basic.GL3Object;
import org.andork.torquescape.model.ColorWaveSlice;
import org.andork.torquescape.model.ISlice;
import org.andork.torquescape.model.RainbowSlice;
import org.andork.torquescape.model.StandardSlice;
import org.andork.torquescape.model.Zone;
import org.andork.util.CollectionUtils;

public class ZoneRenderer implements GL3Object
{
	private static Map<Class<? extends ISlice>, ISliceRendererFactory<? extends ISlice>>	sliceRendererFactories	= CollectionUtils.newHashMap( );
	
	static
	{
		sliceRendererFactories.put( StandardSlice.class , StandardSliceRenderer.FACTORY );
		sliceRendererFactories.put( ColorWaveSlice.class , ColorWaveSliceRenderer.FACTORY );
		sliceRendererFactories.put( RainbowSlice.class , RainbowSliceRenderer.FACTORY );
	}
	
	public Zone																				zone;
	
	public List<ISliceRenderer<?>>															sliceRenderers			= new ArrayList<ISliceRenderer<?>>( );
	
	public int																				vertVbo;
	
	public ZoneRenderer( Zone zone )
	{
		super( );
		this.zone = zone;
		
		for( ISlice slice : zone.getSlices( ) )
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
		
		zone.getVertByteBuffer( ).position( 0 );
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , vertVbo );
		checkGLError( gl , "glBindBuffer" );
		gl.glBufferData( GL3.GL_ARRAY_BUFFER , zone.getVertByteBuffer( ).capacity( ) , zone.getVertByteBuffer( ) , GL3.GL_STATIC_DRAW );
		checkGLError( gl , "glBufferData" );
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , 0 );
		
		for( ISliceRenderer<?> sliceRenderer : sliceRenderers )
		{
			sliceRenderer.init( gl );
		}
	}
	
	@Override
	public void draw( GL3 gl , float[ ] m , float[ ] v , float[ ] p )
	{
		for( ISliceRenderer<?> sliceRenderer : sliceRenderers )
		{
			sliceRenderer.draw( gl , m , v , p );
		}
	}
}
