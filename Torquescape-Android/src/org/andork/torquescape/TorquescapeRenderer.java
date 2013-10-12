package org.andork.torquescape;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.andork.torquescape.model.Zone;
import org.andork.torquescape.model.gen.DefaultTrackGenerator;
import org.andork.torquescape.model.normal.NormalGenerator;
import org.andork.torquescape.model.slice.StandardSlice;
import org.andork.torquescape.model.track.Track;
import org.andork.torquescape.model.track.Track1;
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
	
	private TorquescapeScene initScene( )
	{
		// Set the camera position (View matrix)
		Vecmath.lookAt( viewMatrix , 0 , 0 , 5 , 0f , 0f , 1f , 0f , 1.0f , 0.0f );
		
		Track track = new Track1( );
		
		DefaultTrackGenerator generator = new DefaultTrackGenerator( );
		generator.add( track.getXformFunction( ) , track.getSectionFunction( ) , track.getMeshingFunction( ) , 0 , ( float ) Math.PI * 4 , ( float ) Math.PI / 180 );
		
		float[ ] verts = generator.getVertices( );
		char[ ] indices = generator.getIndices( );
		
		System.out.println( "verts.length: " + verts.length );
		System.out.println( "indices.length: " + indices.length );
		
		NormalGenerator.generateNormals( verts , 3 , 6 , indices , 0 , indices.length );
		
		Zone zone1 = new Zone( );
		zone1.init( verts , indices );
		
		StandardSlice slice1 = new StandardSlice( );
		slice1.setIndices( indices );
		set( slice1.ambientColor , 0.2f , 0 , 0 , 1 );
		set( slice1.diffuseColor , 1 , 0 , 0 , 1 );
		zone1.slices.add( slice1 );
		
		ZoneRenderer zoneRend1 = new ZoneRenderer( zone1 );
		zoneRend1.init( );
		
		TorquescapeScene scene = new TorquescapeScene( );
		scene.zoneRenderers.add( zoneRend1 );
		
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