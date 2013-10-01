package org.andork.jogl.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.andork.vecmath.FloatArrayVecmath;

public class BasicGL3Scene implements GLEventListener
{
	/**
	 * The model matrix.
	 */
	public final float[ ]			m					= FloatArrayVecmath.newIdentityMatrix( );
	
	/**
	 * The view matrix.
	 */
	public final float[ ]			v					= FloatArrayVecmath.newIdentityMatrix( );
	
	/**
	 * The projection matrix;
	 */
	public final float[ ]			p					= FloatArrayVecmath.newIdentityMatrix( );
	
	private int						width , height;
	
	public float					fov					= ( float ) Math.PI / 2;
	public float					zNear				= 1e-2f;
	public float					zFar				= 1e7f;
	
	private final List<GL3Object>	objects				= new ArrayList<GL3Object>( );
	
	private final Queue<GL3Object>	objectsThatNeedInit	= new LinkedList<GL3Object>( );
	
	public BasicGL3Scene add( GL3Object object )
	{
		objects.add( object );
		return this;
	}
	
	public BasicGL3Scene remove( GL3Object object )
	{
		objects.remove( object );
		return this;
	}
	
	public BasicGL3Scene initLater( GL3Object object )
	{
		objectsThatNeedInit.add( object );
		return this;
	}
	
	@Override
	public void init( GLAutoDrawable drawable )
	{
		GL3 gl = ( GL3 ) drawable.getGL( );
		
		for( GL3Object object : objects )
		{
			object.init( gl );
		}
	}
	
	@Override
	public void dispose( GLAutoDrawable drawable )
	{
		
	}
	
	@Override
	public void display( GLAutoDrawable drawable )
	{
		GL3 gl = ( GL3 ) drawable.getGL( );
		
		gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
		
		while( !objectsThatNeedInit.isEmpty( ) )
		{
			objectsThatNeedInit.poll( ).init( gl );
		}
		
		for( GL3Object object : objects )
		{
			object.draw( gl , m , v , p );
		}
	}
	
	@Override
	public void reshape( GLAutoDrawable drawable , int x , int y , int width , int height )
	{
		this.width = width;
		this.height = height;
		
		GL3 gl = ( GL3 ) drawable.getGL( );
		
		gl.glViewport( 0 , 0 , width , height );
		
		recomputePerspective( );
	}
	
	private void recomputePerspective( )
	{
		FloatArrayVecmath.perspective( p , fov , ( float ) width / height , zNear , zFar );
		p[ 8 ] = -p[ 8 ];
		p[ 9 ] = -p[ 9 ];
		p[ 10 ] = -p[ 10 ];
		p[ 11 ] = -p[ 11 ];
	}
	
}
