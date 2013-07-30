package org.andork.torquescape;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.andork.torquescape.model.StandardSlice;
import org.andork.torquescape.model.Zone;
import org.andork.torquescape.model.gen.DefaultTrackGenerator;
import org.andork.torquescape.model.normal.NormalGenerator;
import org.andork.torquescape.model.track.Track;
import org.andork.torquescape.model.track.Track1;
import org.andork.vecmath.FloatArrayVecmath;

public class TorquescapeScene implements GLEventListener
{
	public final List<ZoneRenderer>	zones		= new ArrayList<ZoneRenderer>( );
	
	public IndexedPackedCube		cube		= new IndexedPackedCube( );
	
	public float					mTilt;
	public float					mPan;
	
	float[ ]						mVMatrix	= new float[ 16 ];
	float[ ]						mProjMatrix	= new float[ 16 ];
	float[ ]						mPanMatrix	= new float[ 16 ];
	float[ ]						mTiltMatrix	= new float[ 16 ];
	float[ ]						mMVMatrix	= new float[ 16 ];
	float[ ]						mMVPMatrix	= new float[ 16 ];
	
	public void draw( GL3 gl , float[ ] mvMatrix , float[ ] pMatrix )
	{
		// Redraw background color
		gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
		
		gl.glEnable( GL3.GL_DEPTH_TEST );
		gl.glEnable( GL3.GL_CULL_FACE );
		gl.glCullFace( GL3.GL_BACK );
		
		// Set the camera position (View matrix)
		FloatArrayVecmath.lookAt( mVMatrix , 0 , 0 , 5 , 0f , 0f , 1f , 0f , 1.0f , 0.0f );
		
		// Create a rotation for the triangle
		// long time = SystemClock.uptimeMillis() % 4000L;
		// float angle = 0.090f * ((int) time);
		FloatArrayVecmath.setRotation( mPanMatrix , 0 , 1 , 0 , mPan );
		
		// Create a rotation for the triangle
		// long time = SystemClock.uptimeMillis() % 4000L;
		// float angle = 0.090f * ((int) time);
		FloatArrayVecmath.setRotation( mTiltMatrix , 1 , 0 , 0 , mTilt );
		
		// Calculate the projection and view transformation
		FloatArrayVecmath.mmul( mPanMatrix , mTiltMatrix , mMVMatrix );
		
		// Calculate the projection and view transformation
		FloatArrayVecmath.mmul( mVMatrix , mMVMatrix , mMVMatrix );
		
		FloatArrayVecmath.transpose( mMVMatrix , mMVMatrix );
		
		cube.draw( gl , mMVMatrix , mProjMatrix );
		//
		// for( ZoneRenderer zone : zones )
		// {
		// for( ISliceRenderer<?> sliceRenderer : zone.sliceRenderers )
		// {
		// sliceRenderer.draw( gl , mvMatrix , pMatrix );
		// }
		// }
	}
	
	@Override
	public void init( GLAutoDrawable drawable )
	{
		GL3 gl = ( GL3 ) drawable.getGL( );
		
		cube.init( gl );
		
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
		
		ZoneRenderer rend1 = new ZoneRenderer( zone1 );
		rend1.init( gl );
		
		zones.add( rend1 );
	}
	
	private void set( float[ ] array , float a , float b , float c , float d )
	{
		array[ 0 ] = a;
		array[ 1 ] = b;
		array[ 2 ] = c;
		array[ 3 ] = d;
	}
	
	@Override
	public void dispose( GLAutoDrawable drawable )
	{
		
	}
	
	@Override
	public void display( GLAutoDrawable drawable )
	{
		draw( ( GL3 ) drawable.getGL( ) , mMVMatrix , mProjMatrix );
	}
	
	@Override
	public void reshape( GLAutoDrawable drawable , int x , int y , int width , int height )
	{
		drawable.getGL( ).glViewport( 0 , 0 , width , height );
		
		float ratio = ( float ) width / height;
		
		FloatArrayVecmath.perspective( mProjMatrix , ( float ) Math.PI / 3 , ratio , 0.001f , 100 );
		FloatArrayVecmath.transpose( mProjMatrix , mProjMatrix );
	}
}
