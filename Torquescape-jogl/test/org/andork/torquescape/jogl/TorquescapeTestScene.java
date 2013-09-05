package org.andork.torquescape.jogl;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.andork.torquescape.jogl.render.ISliceRenderer;
import org.andork.torquescape.jogl.render.ZoneRenderer;
import org.andork.torquescape.model.StandardSlice;
import org.andork.torquescape.model.Zone;
import org.andork.torquescape.model.gen.DefaultTrackGenerator;
import org.andork.torquescape.model.normal.NormalGenerator;
import org.andork.torquescape.model.track.Track;
import org.andork.torquescape.model.track.Track1;
import org.andork.util.ArrayUtils;
import org.andork.vecmath.FloatArrayVecmath;

public class TorquescapeTestScene implements GLEventListener
{
	public final List<ZoneRenderer>	zones				= new ArrayList<ZoneRenderer>( );
	
	public IndexedPackedCube		cube				= new IndexedPackedCube( );
	
	public float					tilt;
	public float					pan;
	
	float[ ]						cameraMatrix		= FloatArrayVecmath.newIdentityMatrix( );
	
	float[ ]						modelMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	float[ ]						viewMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	float[ ]						projMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	float[ ]						panMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	float[ ]						tiltMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	float[ ]						modelViewMatrix		= FloatArrayVecmath.newIdentityMatrix( );
	float[ ]						modelViewProjMatrix	= FloatArrayVecmath.newIdentityMatrix( );
	
	public void draw( GL3 gl , float[ ] mvMatrix , float[ ] pMatrix )
	{
		// Redraw background color
		gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
		
		gl.glEnable( GL3.GL_DEPTH_TEST );
		gl.glDepthFunc( GL3.GL_LEQUAL );
		gl.glEnable( GL3.GL_CULL_FACE );
		gl.glCullFace( GL3.GL_BACK );
		
		FloatArrayVecmath.invAffine( cameraMatrix , modelMatrix );
		FloatArrayVecmath.mmul( viewMatrix , modelMatrix , modelViewMatrix );
		
		FloatArrayVecmath.transpose( modelViewMatrix , modelViewMatrix );
		
		for( ZoneRenderer zone : zones )
		{
			for( ISliceRenderer<?> sliceRenderer : zone.sliceRenderers )
			{
				sliceRenderer.draw( gl , mvMatrix , pMatrix );
			}
		}
	}
	
	@Override
	public void init( GLAutoDrawable drawable )
	{
		// Set the camera position (View matrix)
		FloatArrayVecmath.lookAt( viewMatrix , 0 , 0 , 5 , 0f , 0f , 1f , 0f , 1.0f , 0.0f );
		
		GL3 gl = ( GL3 ) drawable.getGL( );
		
		cube.init( gl );
		
		Track track = new Track1( );
		
		DefaultTrackGenerator generator = new DefaultTrackGenerator( );
		generator.add( track.getSectionFn( ) , track.getMeshingFn( ) , 0 , ( float ) Math.PI * 4 , ( float ) Math.PI / 180 );
		
		float[ ] verts = generator.getVertices( );
		char[ ] indices = generator.getIndices( );
		
		System.out.println( "verts.length: " + verts.length );
		System.out.println( "indices.length: " + indices.length );
		
		System.out.println( ArrayUtils.prettyPrintAsNumbers( indices , 6 , 0 , 1000 , 3 , "%5d" ) );
		
		NormalGenerator.generateNormals( verts , 3 , 6 , indices , 0 , indices.length );
		
		Zone zone1 = new Zone( );
		zone1.init( verts , verts.length / 3 , indices );
		
		StandardSlice slice1 = new StandardSlice( );
		slice1.setIndices( indices );
		set( slice1.ambientColor , 0.1f , 0 , 0 , 1 );
		set( slice1.diffuseColor , 1 , 0 , 0 , 1 );
		zone1.addSlice( slice1 );
		
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
		draw( ( GL3 ) drawable.getGL( ) , modelViewMatrix , projMatrix );
	}
	
	@Override
	public void reshape( GLAutoDrawable drawable , int x , int y , int width , int height )
	{
		drawable.getGL( ).glViewport( 0 , 0 , width , height );
		
		float ratio = ( float ) width / height;
		
		FloatArrayVecmath.perspective( projMatrix , ( float ) Math.PI / 2 , ratio , 0.01f , 10000 );
		projMatrix[ 8 ] = -projMatrix[ 8 ];
		projMatrix[ 9 ] = -projMatrix[ 9 ];
		projMatrix[ 10 ] = -projMatrix[ 10 ];
		projMatrix[ 11 ] = -projMatrix[ 11 ];
		FloatArrayVecmath.transpose( projMatrix , projMatrix );
	}
}
