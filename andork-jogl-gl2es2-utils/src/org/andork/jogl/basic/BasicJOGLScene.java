package org.andork.jogl.basic;

import static org.andork.vecmath.Vecmath.invAffineToTranspose3x3;
import static org.andork.vecmath.Vecmath.newMat4f;
import static org.andork.vecmath.Vecmath.perspective;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.media.opengl.DebugGL2;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

public class BasicJOGLScene implements GLEventListener
{
	/**
	 * The model matrix.
	 */
	public final float[ ]			m					= newMat4f( );
	
	/**
	 * The normal matrix.
	 */
	private final float[ ]			n					= new float[ 9 ];
	
	/**
	 * The view matrix.
	 */
	public final float[ ]			v					= newMat4f( );
	
	/**
	 * The projection matrix;
	 */
	public final float[ ]			p					= newMat4f( );
	
	private int						width , height;
	
	public float					fov					= ( float ) Math.PI / 2;
	public float					zNear				= 1e-2f;
	public float					zFar				= 1e7f;
	
	private final List<JOGLObject>	objects				= new ArrayList<JOGLObject>( );
	
	private final Queue<JOGLObject>	objectsThatNeedInit	= new LinkedList<JOGLObject>( );
	
	private DebugGL2				debugGL;
	
	public BasicJOGLScene add( JOGLObject object )
	{
		objects.add( object );
		return this;
	}
	
	public BasicJOGLScene remove( JOGLObject object )
	{
		objects.remove( object );
		return this;
	}
	
	public BasicJOGLScene initLater( JOGLObject object )
	{
		objectsThatNeedInit.add( object );
		return this;
	}
	
	public GL2ES2 getGL( GL2ES2 orig )
	{
		if( debugGL == null || debugGL.getDownstreamGL( ) != orig )
		{
			debugGL = new DebugGL2( ( GL2 ) orig );
		}
		return debugGL;
	}
	
	@Override
	public void init( GLAutoDrawable drawable )
	{
		GL2ES2 gl = getGL( ( GL2ES2 ) drawable.getGL( ) );
		
		for( JOGLObject object : objects )
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
		GL2ES2 gl = getGL( ( GL2ES2 ) drawable.getGL( ) );
		
		gl.glClear( GL2ES2.GL_COLOR_BUFFER_BIT | GL2ES2.GL_DEPTH_BUFFER_BIT );
		
		while( !objectsThatNeedInit.isEmpty( ) )
		{
			objectsThatNeedInit.poll( ).init( gl );
		}
		
		invAffineToTranspose3x3( m , n );
		
		for( JOGLObject object : objects )
		{
			object.draw( gl , m , n , v , p );
		}
	}
	
	@Override
	public void reshape( GLAutoDrawable drawable , int x , int y , int width , int height )
	{
		this.width = width;
		this.height = height;
		
		GL2ES2 gl = getGL( ( GL2ES2 ) drawable.getGL( ) );
		
		gl.glViewport( 0 , 0 , width , height );
		
		recomputePerspective( );
	}
	
	private void recomputePerspective( )
	{
		perspective( p , fov , ( float ) width / height , zNear , zFar );
		p[ 8 ] = -p[ 8 ];
		p[ 9 ] = -p[ 9 ];
		p[ 10 ] = -p[ 10 ];
		p[ 11 ] = -p[ 11 ];
	}
	
}
