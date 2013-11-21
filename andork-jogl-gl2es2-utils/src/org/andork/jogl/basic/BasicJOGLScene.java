package org.andork.jogl.basic;

import static org.andork.vecmath.Vecmath.invAffineToTranspose3x3;
import static org.andork.vecmath.Vecmath.newMat4f;
import static org.andork.vecmath.Vecmath.perspective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.media.opengl.DebugGL2;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.andork.vecmath.Vecmath;

public class BasicJOGLScene implements GLEventListener
{
	/**
	 * The model matrix.
	 */
	public final float[ ]			m						= newMat4f( );
	
	/**
	 * The normal matrix.
	 */
	private final float[ ]			n						= new float[ 9 ];
	
	/**
	 * The view matrix.
	 */
	public final float[ ]			v						= newMat4f( );
	
	/**
	 * The projection matrix;
	 */
	public final float[ ]			p						= newMat4f( );
	
	private int						width , height;
	
	public float					fov						= ( float ) Math.PI / 2;
	public float					zNear					= 1f;
	public float					zFar					= 1e7f;
	
	private final List<JOGLObject>	objects					= new ArrayList<JOGLObject>( );
	
	private final Queue<JOGLObject>	objectsThatNeedInit		= new LinkedList<JOGLObject>( );
	private final Queue<JOGLObject>	objectsThatNeedDestroy	= new LinkedList<JOGLObject>( );
	
	private static boolean			USE_DEBUG_GL;
	private DebugGL2				debugGL;
	
	private boolean					orthoMode;
	
	public float[ ]					orthoFrame				= { -1 , 1 , -1 , 1 , -100 , 100 };
	
	final float[ ]					lastOrthoView			= newMat4f( );
	final float[ ]					lastPerspectiveView		= newMat4f( );
	
	static
	{
		USE_DEBUG_GL = System.getProperty( "useDebugGL" ) != null;
	}
	
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
	
	public BasicJOGLScene destroyLater( JOGLObject object )
	{
		objectsThatNeedDestroy.add( object );
		return this;
	}
	
	public GL2ES2 getGL( GL2ES2 orig )
	{
		if( !USE_DEBUG_GL )
		{
			return orig;
		}
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
		
		while( !objectsThatNeedDestroy.isEmpty( ) )
		{
			objectsThatNeedDestroy.poll( ).destroy( gl );
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
		
		recomputeProjection( );
	}
	
	public void setOrthoMode( boolean ortho )
	{
		if( this.orthoMode != ortho )
		{
			if( this.orthoMode )
			{
				Vecmath.setf( lastOrthoView , v );
				Vecmath.setf( v , lastPerspectiveView );
			}
			else
			{
				Vecmath.setf( lastPerspectiveView , v );
				Vecmath.setf( v , lastOrthoView );
			}
			
			this.orthoMode = ortho;
			recomputeProjection( );
		}
	}
	
	public void recomputeProjection( )
	{
		if( orthoMode )
		{
			Vecmath.ortho( p , orthoFrame[ 0 ] , orthoFrame[ 1 ] , orthoFrame[ 2 ] , orthoFrame[ 3 ] , orthoFrame[ 4 ] , orthoFrame[ 5 ] );
		}
		else
		{
			perspective( p , fov , ( float ) width / height , zNear , zFar );
		}
//		p[ 2 ] = -p[ 2 ];
//		p[ 6 ] = -p[ 6 ];
//		p[ 10 ] = -p[ 10 ];
//		p[ 14 ] = -p[ 14 ];
	}
	
	public List<JOGLObject> getObjects( )
	{
		return Collections.unmodifiableList( objects );
	}
	
	public void clear( )
	{
		objects.clear( );
	}
	
}
