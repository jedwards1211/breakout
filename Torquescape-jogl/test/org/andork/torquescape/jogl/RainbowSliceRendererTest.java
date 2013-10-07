package org.andork.torquescape.jogl;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;

import org.andork.jogl.basic.BasicGL3Frame;
import org.andork.jogl.basic.BasicGL3Scene;
import org.andork.torquescape.jogl.render.ZoneRenderer;
import org.andork.torquescape.model.Zone;
import org.andork.torquescape.model.gen.DirectZoneGenerator;
import org.andork.torquescape.model.normal.NormalGenerator;
import org.andork.torquescape.model.slice.RainbowSlice;
import org.andork.torquescape.model.track.Track;
import org.andork.torquescape.model.track.Track1;
import org.andork.torquescape.model.vertex.IVertexAttrFn;
import org.andork.torquescape.model.vertex.IVertexVisitor;
import org.andork.torquescape.model.vertex.StandardVertexFn;
import org.andork.vecmath.FloatArrayVecmath;

@SuppressWarnings( "serial" )
public class RainbowSliceRendererTest extends BasicGL3Frame
{
	public static void main( String[ ] args )
	{
		RainbowSliceRendererTest test = new RainbowSliceRendererTest( );
		test.setTitle( "Rainbow Slice Renderer" );
		test.setVisible( true );
	}
	
	@Override
	protected BasicGL3Scene createScene( )
	{
		BasicGL3Scene result = new BasicGL3Scene( )
		{
			
			@Override
			public void display( GLAutoDrawable drawable )
			{
				GL3 gl = ( GL3 ) drawable.getGL( );
				
				gl.glEnable( GL3.GL_DEPTH_TEST );
				gl.glDepthFunc( GL3.GL_LEQUAL );
				gl.glEnable( GL3.GL_CULL_FACE );
				gl.glCullFace( GL3.GL_BACK );
				
				super.display( drawable );
			}
		};
		
		init( result );
		
		return result;
	}
	
	private void init( BasicGL3Scene scene )
	{
		final Track track = new Track1( );
		
		Zone zone = createZoneForRainbowSlice( track , sequence( 0 , ( float ) Math.PI * 4 , ( float ) Math.PI / 360f ) );
		
		RainbowSlice rainbowSlice = new RainbowSlice( );
		rainbowSlice.uOffset = 28;
		rainbowSlice.vOffset = 40;
		rainbowSlice.setIndexBuffer( zone.getIndexBuffer( ) );
		zone.addSlice( rainbowSlice );
		
		ZoneRenderer rend1 = new ZoneRenderer( zone );
		
		scene.add( rend1 );
	}
	
	public static ZoneRenderer createTestRainbowZone( )
	{
		final Track track = new Track1( );
		
		Zone zone = createZoneForRainbowSlice( track , sequence( 0 , ( float ) Math.PI * 4 , ( float ) Math.PI / 360f ) );
		
		RainbowSlice rainbowSlice = new RainbowSlice( );
		rainbowSlice.uOffset = 28;
		rainbowSlice.vOffset = 40;
		rainbowSlice.setIndexBuffer( zone.getIndexBuffer( ) );
		zone.addSlice( rainbowSlice );
		
		ZoneRenderer rend1 = new ZoneRenderer( zone );
		
		return rend1;
	}
	
	public static List<Float> sequence( float start , float end , float step )
	{
		List<Float> result = new ArrayList<Float>( );
		for( float f = start ; f < end ; f += step )
		{
			result.add( f );
		}
		return result;
	}
	
	public static Zone createZoneForRainbowSlice( final Track track , List<Float> params )
	{
		IVertexAttrFn attrFn1 = new IVertexAttrFn( )
		{
			@Override
			public int getBytesPerVertex( )
			{
				return 4;
			}
			
			@Override
			public void eval( float param , int index , int vertexCount , float x , float y , float z , IVertexVisitor visitor )
			{
				visitor.visit( param );
			}
		};
		
		final float step = ( float ) Math.PI / 180;
		
		IVertexAttrFn attrFn2 = new IVertexAttrFn( )
		{
			float[ ]	prev	= new float[ 3 ];
			float[ ]	next	= new float[ 3 ];
			
			@Override
			public int getBytesPerVertex( )
			{
				return 24;
			}
			
			@Override
			public void eval( float param , int index , int vertexCount , float x , float y , float z , IVertexVisitor visitor )
			{
				track.getCoordFn( ).eval( param , index , prev );
				track.getCoordFn( ).eval( param + step , index , next );
				
				next[ 0 ] -= prev[ 0 ];
				next[ 1 ] -= prev[ 1 ];
				next[ 2 ] -= prev[ 2 ];
				
				FloatArrayVecmath.normalize( next , 0 , 3 );
				
				visitor.visit( next[ 0 ] );
				visitor.visit( next[ 1 ] );
				visitor.visit( next[ 2 ] );
				
				if( ( index % 2 ) == 0 )
				{
					track.getCoordFn( ).eval( param , ( index + vertexCount - 1 ) % vertexCount , next );
				}
				else
				{
					track.getCoordFn( ).eval( param , ( index + 1 ) % vertexCount , next );
				}
				
				next[ 0 ] -= prev[ 0 ];
				next[ 1 ] -= prev[ 1 ];
				next[ 2 ] -= prev[ 2 ];
				
				FloatArrayVecmath.normalize( next , 0 , 3 );
				
				visitor.visit( next[ 0 ] );
				visitor.visit( next[ 1 ] );
				visitor.visit( next[ 2 ] );
			}
		};
		
		StandardVertexFn vertexFn = new StandardVertexFn( track.getCoordFn( ) , attrFn1 , attrFn2 );
		
		Zone zone = new Zone( );
		
		int vertexCount = params.size( ) * vertexFn.getVertexCount( 0 );
		
		int indexCount = track.getIndexFn( ).getIndexCount( 0 ) * params.size( );
		
		zone.init( vertexCount , vertexFn.getBytesPerVertex( ) , indexCount );
		
		DirectZoneGenerator zoneGen = DirectZoneGenerator.newInstance( );
		zoneGen.setZone( zone );
		zoneGen.generate( vertexFn , track.getIndexFn( ) , params );
		
		NormalGenerator.generateNormals( zone.getVertBuffer( ) , 12 , vertexFn.getBytesPerVertex( ) , zone.getIndexBuffer( ) , 0 , indexCount );
		
		zone.rebuildMaps( );
		return zone;
	}
}
