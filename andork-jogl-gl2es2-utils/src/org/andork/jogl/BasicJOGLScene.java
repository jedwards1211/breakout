package org.andork.jogl;

import static org.andork.math3d.Vecmath.invAffineToTranspose3x3;
import static org.andork.math3d.Vecmath.newMat4f;
import static org.andork.math3d.Vecmath.perspective;

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

import org.andork.math3d.PickXform;
import org.andork.math3d.Vecmath;
import org.andork.util.ArrayUtils;

public class BasicJOGLScene implements GLEventListener
{
	/**
	 * The model matrix.
	 */
	private final float[ ]				m						= newMat4f( );
	
	/**
	 * The normal matrix.
	 */
	private final float[ ]				n						= new float[ 9 ];
	
	/**
	 * The view matrix.
	 */
	private final float[ ]				v						= newMat4f( );
	
	/**
	 * The projection matrix;
	 */
	private final float[ ]				p						= newMat4f( );
	
	private ProjectionCalculator		pCalculator				= new PerspectiveProjectionCalculator( ( float ) Math.PI / 2 , 1f , 1e7f );
	
	private int							width , height;
	
	private final List<JOGLObject>		objects					= new ArrayList<JOGLObject>( );
	private final List<JOGLObject>		unmodifiableObjects		= Collections.unmodifiableList( objects );
	
	private final Queue<JOGLObject>		objectsThatNeedInit		= new LinkedList<JOGLObject>( );
	private final Queue<JOGLObject>		objectsThatNeedDestroy	= new LinkedList<JOGLObject>( );
	private final Queue<JOGLRunnable>	doLaters				= new LinkedList<JOGLRunnable>( );
	
	private static boolean				USE_DEBUG_GL;
	private DebugGL2					debugGL;
	
	private PickXform					pickXform				= new PickXform( );
	
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
	
	public BasicJOGLScene doLater( JOGLRunnable runnable )
	{
		doLaters.add( runnable );
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
		
		pickXform.calculate( p , v );
		
		while( !doLaters.isEmpty( ) )
		{
			try
			{
				doLaters.poll( ).run( gl );
			}
			catch( Exception ex )
			{
				ex.printStackTrace( );
			}
		}
		
		drawObjects( gl );
	}
	
	public void drawObjects( GL2ES2 gl )
	{
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
		
		recalculateProjection( );
	}
	
	public void recalculateProjection( )
	{
		pCalculator.calculate( width , height , p );
	}
	
	public List<JOGLObject> getObjects( )
	{
		return unmodifiableObjects;
	}
	
	public void clear( )
	{
		objects.clear( );
	}
	
	public void getModelXform( float[ ] out )
	{
		System.arraycopy( m , 0 , out , 0 , 16 );
	}
	
	public void setModelXform( float[ ] m )
	{
		if( Vecmath.hasNaNsOrInfinites( m ) )
		{
			throw new IllegalArgumentException( "m must not contain NaN or Infinite values" );
		}
		
		System.arraycopy( m , 0 , this.m , 0 , 16 );
	}
	
	public void getViewXform( float[ ] out )
	{
		System.arraycopy( v , 0 , out , 0 , 16 );
	}
	
	public void setViewXform( float[ ] v )
	{
		if( Vecmath.hasNaNsOrInfinites( v ) )
		{
			throw new IllegalArgumentException( "v must not contain NaN or Infinite values" );
		}
		
		System.arraycopy( v , 0 , this.v , 0 , 16 );
	}
	
	public PickXform pickXform( )
	{
		return pickXform;
	}
	
	public int getWidth( )
	{
		return width;
	}
	
	public int getHeight( )
	{
		return height;
	}
	
	public void setProjectionCalculator( ProjectionCalculator calculator )
	{
		if( pCalculator != calculator )
		{
			pCalculator = calculator;
			recalculateProjection( );
		}
	}
	
	public ProjectionCalculator getProjectionCalculator( )
	{
		return pCalculator;
	}
}
