package org.andork.torquescape;

import static org.andork.vecmath.Vecmath.normalize;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.andork.torquescape.model.Zone;
import org.andork.torquescape.model.gen.DefaultTrackGenerator;
import org.andork.torquescape.model.gen.DirectZoneGenerator;
import org.andork.torquescape.model.normal.NormalGenerator;
import org.andork.torquescape.model.slice.RainbowSlice;
import org.andork.torquescape.model.slice.StandardSlice;
import org.andork.torquescape.model.track.Track;
import org.andork.torquescape.model.track.Track1;
import org.andork.torquescape.model.vertex.IVertexAttrFn;
import org.andork.torquescape.model.vertex.IVertexVisitor;
import org.andork.torquescape.model.vertex.StandardVertexFn;
import org.andork.vecmath.Vecmath;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

public class TorquescapeRenderer implements Renderer
{
	float[ ]			cameraMatrix		= Vecmath.newMat4f( );
	
	float[ ]			modelMatrix			= Vecmath.newMat4f( );
	float[ ]			viewMatrix			= Vecmath.newMat4f( );
	float[ ]			projMatrix			= Vecmath.newMat4f( );
	float[ ]			modelViewMatrix		= Vecmath.newMat4f( );
	float[ ]			modelViewProjMatrix	= Vecmath.newMat4f( );
	
	TorquescapeScene	scene;
	
	float				mPan				= 0;
	float				mTilt				= 0;
	
	public void onSurfaceCreated( GL10 unused , EGLConfig config )
	{
		// Set the background frame color
		GLES20.glClearColor( 0f , 0f , 0f , 1.0f );
		scene = initScene( );
	}
	
	public TorquescapeScene getScene( )
	{
		return scene;
	}
	
	public void setScene( TorquescapeScene scene )
	{
		this.scene = scene;
	}
	
	public void onDrawFrame( GL10 unused )
	{
		// Redraw background color
		GLES20.glClear( GLES20.GL_COLOR_BUFFER_BIT );
		
		GLES20.glEnable( GLES20.GL_DEPTH_TEST );
		GLES20.glEnable( GLES20.GL_CULL_FACE );
		GLES20.glCullFace( GLES20.GL_BACK );
		
		Vecmath.invAffine( cameraMatrix , modelMatrix );
		Vecmath.mmul( viewMatrix , modelMatrix , modelViewMatrix );
		
		Vecmath.transpose( modelViewMatrix , modelViewMatrix );
		
		if( scene != null )
		{
			scene.draw( modelViewMatrix , projMatrix );
		}
	}
	
	public void onSurfaceChanged( GL10 unused , int width , int height )
	{
		GLES20.glViewport( 0 , 0 , width , height );
		
		float ratio = ( float ) width / height;
		
		// this projection matrix is applied to object coordinates
		// in the onDrawFrame() method
		Matrix.perspectiveM( projMatrix , 0 , 90 , ratio , 0.01f , 10000 );
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
				
				normalize( next , 0 , 3 );
				
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
				
				normalize( next , 0 , 3 );
				
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
	
	private TorquescapeScene initScene( )
	{
		// Set the camera position (View matrix)
		Vecmath.lookAt( viewMatrix , 0 , 0 , 5 , 0f , 0f , 1f , 0f , 1.0f , 0.0f );
		
		TorquescapeScene scene = new TorquescapeScene( );
		scene.zoneRenderers.add( createTestRainbowZone( ) );
		
		return scene;
	}
	
	private void set( float[ ] array , float a , float b , float c , float d )
	{
		array[ 0 ] = a;
		array[ 1 ] = b;
		array[ 2 ] = c;
		array[ 3 ] = d;
	}
}