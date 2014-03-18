package org.andork.torquescape.jogl.render;

import static org.andork.jogl.util.JOGLUtils.checkGLError;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2ES2;

import org.andork.collect.CollectionUtils;
import org.andork.jogl.JOGLObject;
import org.andork.torquescape.model.ISlice;
import org.andork.torquescape.model.Zone;
import org.andork.torquescape.model.slice.ColorWaveSlice;
import org.andork.torquescape.model.slice.RainbowSlice;
import org.andork.torquescape.model.slice.StandardSlice;

public class ZoneRenderer implements JOGLObject
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
	
	public Map<String, Integer>																vertVbos				= CollectionUtils.newHashMap( );
	
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
	
	public void init( GL2ES2 gl )
	{
		int[ ] buffers = new int[ zone.vertBuffers.size( ) ];
		
		gl.glGenBuffers( zone.vertBuffers.size( ) , buffers , 0 );
		
		int k = 0;
		for( String key : zone.vertBuffers.keySet( ) )
		{
			int vbo = buffers[ k++ ];
			if( key.equals( Zone.PRIMARY_VERT_BUFFER_KEY ) )
			{
				vertVbo = vbo;
			}
			vertVbos.put( key , vbo );
			
			ByteBuffer buffer = zone.vertBuffers.get( key );
			buffer.position( 0 );
			
			gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , vbo );
			checkGLError( gl , "glBindBuffer" );
			gl.glBufferData( GL2ES2.GL_ARRAY_BUFFER , buffer.capacity( ) , buffer , GL2ES2.GL_STATIC_DRAW );
			checkGLError( gl , "glBufferData" );
		}
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , 0 );
		
		for( ISliceRenderer<?> sliceRenderer : sliceRenderers )
		{
			sliceRenderer.init( gl );
		}
	}
	
	@Override
	public void draw( GL2ES2 gl , float[ ] m , float[ ] n , float[ ] v , float[ ] p )
	{
		for( ISliceRenderer<?> sliceRenderer : sliceRenderers )
		{
			sliceRenderer.draw( gl , m , n , v, p );
		}
	}

	@Override
	public void destroy( GL2ES2 gl )
	{
		
	}
}
