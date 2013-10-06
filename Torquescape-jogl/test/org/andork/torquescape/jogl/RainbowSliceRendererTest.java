package org.andork.torquescape.jogl;

import java.util.Arrays;

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
		
		int paramCount = ( int ) ( Math.PI * 4 / step );
		
		int vertexCount = paramCount * vertexFn.getVertexCount( 0 );
		
		int indexCount = track.getIndexFn( ).getIndexCount( 0 ) * paramCount;
		
		zone.init( vertexCount , vertexFn.getBytesPerVertex( ) , indexCount );
		
		Float[ ] params = new Float[ paramCount ];
		for( int i = 0 ; i < paramCount ; i++ )
		{
			params[ i ] = i * step;
		}
		
		DirectZoneGenerator zoneGen = DirectZoneGenerator.newInstance( );
		zoneGen.setZone( zone );
		zoneGen.generate( vertexFn , track.getIndexFn( ) , Arrays.asList( params ) );
		
		NormalGenerator.generateNormals( zone.getVertBuffer( ) , 12 , vertexFn.getBytesPerVertex( ) , zone.getIndexBuffer( ) , 0 , indexCount );
		
		zone.rebuildMaps( );
		
		RainbowSlice rainbowSlice = new RainbowSlice( );
		rainbowSlice.uOffset = 28;
		rainbowSlice.vOffset = 40;
		rainbowSlice.setIndexBuffer( zone.getIndexBuffer( ) );
		zone.addSlice( rainbowSlice );
		
		ZoneRenderer rend1 = new ZoneRenderer( zone );
		
		scene.add( rend1 );
	}
}
