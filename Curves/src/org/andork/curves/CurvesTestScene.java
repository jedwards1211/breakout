package org.andork.curves;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.vecmath.Point2f;

import org.andork.jogl.basic.BasicGL3Object;
import org.andork.jogl.basic.BufferHelper;
import org.andork.jogl.basic.GL3BlendModifier;
import org.andork.jogl.basic.GL3Object;
import org.andork.jogl.util.JOGLOverlay;
import org.andork.jogl.util.SimplePolygon;
import org.andork.math.curve.Point2fType;
import org.andork.math.curve.SmoothRandomWalk;
import org.andork.math.curve.SmoothRandomWalk.RandomPoint2fGenerator;
import org.andork.vecmath.FloatArrayVecmath;

public class CurvesTestScene implements GLEventListener
{
	SimplePolygon					cpPolygon;
	
	final float[ ]					viewFrame			= { -1 , 1 , -1 , 1 };
	
	float[ ]						mvpMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	
	float[ ]						modelMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	float[ ]						viewMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	float[ ]						projMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	
	CurveVisualizer					visualizer			= new CurveVisualizer( 15 , 2 );
	
	int								width;
	int								height;
	
	int								highlightedPoint	= 0;
	float[ ]						transformedPoint	= new float[ 3 ];
	
	SmoothRandomWalk<Point2f>[ ]	pointWalks;
	Point2f							p2f					= new Point2f( );
	
	private long					lastAdvance			= 0;
	
	private JOGLOverlay				overlay				= new JOGLOverlay( );
	
	private List<GL3Object>			objects				= new ArrayList<GL3Object>( );
	
	private BasicGL3Object			obj2;
	
	@Override
	public void init( GLAutoDrawable drawable )
	{
		pointWalks = new SmoothRandomWalk[ visualizer.controlPoints.length / 2 ];
		
		Point2fType pointType = new Point2fType( );
		RandomPoint2fGenerator generator = new RandomPoint2fGenerator( -2 , 2 );
		
		for( int i = 0 ; i < pointWalks.length ; i++ )
		{
			pointWalks[ i ] = new SmoothRandomWalk<>( 3 , 1 , pointType , generator );
			pointWalks[ i ].advance( 0 , p2f );
			visualizer.controlPoints[ i * 2 ] = p2f.x;
			visualizer.controlPoints[ i * 2 + 1 ] = p2f.y;
		}
		
		GL3 gl = ( GL3 ) drawable.getGL( );
		
		int[ ] vbos = new int[ 1 ];
		gl.glGenBuffers( 1 , vbos , 0 );
		
		SimplePolygon.globalInit( gl );
		
		cpPolygon = new SimplePolygon( );
		ByteBuffer buffer = ByteBuffer.allocateDirect( 3 * 5 * 4 );
		buffer.order( ByteOrder.nativeOrder( ) );
		FloatBuffer fb = buffer.asFloatBuffer( );
		
		fb.put( 1 ).put( 1 ).put( 0 );
		fb.put( -1 ).put( 1 ).put( 0 );
		fb.put( -1 ).put( -1 ).put( 0 );
		fb.put( 1 ).put( -1 ).put( 0 );
		fb.put( 1 ).put( 1 ).put( 0 );
		
		fb.position( 0 );
		
		cpPolygon.coords = buffer;
		cpPolygon.color[ 0 ] = 0;
		cpPolygon.color[ 1 ] = 1;
		cpPolygon.vertexCount = 5;
		
		cpPolygon.init( gl );
		
		visualizer.initGL( gl );
		
		// for( int i = 0 ; i < visualizer.numPoints ; i++ )
		// {
		// visualizer.controlPoints[ i * 2 ] = Math.random( ) - 0.5;
		// visualizer.controlPoints[ i * 2 + 1 ] = Math.random( ) - 0.5;
		// }
		
		visualizer.recalculate( );
		
		for( float x = 0 ; x <= 1 ; x += 0.1f )
		{
			for( float y = 1 ; y >= 0 ; y -= 0.1f )
			{
				System.out.format( "%9.2f  " , visualizer.eval( new double[ ] { x , y } ) );
			}
			System.out.println( );
		}
		
		BasicGL3Object obj1 = new BasicGL3Object( );
		obj1.vertexShaderCode( "uniform mat4 m;" +
				"uniform mat4 v;" +
				"uniform mat4 p;" +
				"attribute vec3 a_pos;" +
				"attribute float a_param;" +
				"varying float v_param;" +
				"void main() {" +
				"  v_param = a_param;" +
				"  mat4 mvp = p*v*m;" +
				"  gl_Position = mvp * vec4(a_pos, 1.0);" +
				"}" );
		obj1.fragmentShaderCode( "varying float v_param;" +
				"void main( ) {" +
				"  gl_FragColor = vec4(1.0, 1.0, 1.0, v_param);" +
				"}" );
		obj1.add( obj1.new AttributeVec3fv( ).name( "a_pos" ) ).add( obj1.new Attribute1fv( ).name( "a_param" ) );
		obj1.add( new GL3BlendModifier( ) );
		obj1.addVertexBuffer( new BufferHelper( )
				.put( 0f , 0f , 0f ).put( 0f )
				.put( 1f , 2f , 0f ).put( 0.25f )
				.put( 3f , -2f , 0f ).put( 0.5f )
				.put( -1f , 5f , 0f ).put( 0.75f )
				.put( 4f , 0f , 0f ).put( 1f ).toByteBuffer( ) );
		
		obj1.vertexCount( 5 );
		obj1.drawMode( GL3.GL_LINE_STRIP );
		obj1.transpose( true );
		
		objects.add( obj1 );
		
		obj2 = new BasicGL3Object( );
		obj2.vertexShaderCode( "uniform mat4 m;" +
				"uniform mat4 v;" +
				"uniform mat4 p;" +
				"attribute vec3 a_pos;" +
				"void main() {" +
				"  mat4 mvp = p*v*m;" +
				"  gl_Position = mvp * vec4(a_pos, 1.0);" +
				"}" );
		obj2.fragmentShaderCode( "void main( ) {" +
				"  gl_FragColor = vec4(1.0, 1.0, 1.0, 0.5);" +
				"}" );
		obj2.add( obj2.new AttributeVec3fv( ).name( "a_pos" ) );
		obj2.add( new GL3BlendModifier( ) );
		obj2.addVertexBuffer( 4 * visualizer.controlPoints.length / 2 * 3 );
		
		obj2.vertexCount( visualizer.controlPoints.length / 2 );
		obj2.drawMode( GL3.GL_LINE_STRIP );
		obj2.transpose( true );
		
		objects.add( obj2 );
		
		for( GL3Object object : objects )
		{
			object.init( gl );
		}
	}
	
	@Override
	public void dispose( GLAutoDrawable drawable )
	{
		
	}
	
	public static float reparam( float x , float a1 , float a2 , float b1 , float b2 )
	{
		return b1 + ( x - a1 ) * ( b2 - b1 ) / ( a2 - a1 );
	}
	
	@Override
	public void display( GLAutoDrawable drawable )
	{
		long time = System.currentTimeMillis( );
		if( lastAdvance == 0 )
		{
			lastAdvance = time;
		}
		
		ByteBuffer buffer = obj2.vertexBuffer( 0 );
		buffer.position( 0 );
		
		GL3 gl = ( GL3 ) drawable.getGL( );
		
		if( time - lastAdvance > 15 )
		{
			for( int i = 0 ; i < pointWalks.length ; i++ )
			{
				pointWalks[ i ].advance( ( float ) ( time - lastAdvance ) / 100000f , p2f );
				visualizer.controlPoints[ i * 2 ] = p2f.x;
				visualizer.controlPoints[ i * 2 + 1 ] = p2f.y;
				buffer.putFloat( p2f.x ).putFloat( p2f.y ).putFloat( 0 );
			}
//			obj2.rebufferVertices( gl );
			visualizer.recalculate( );
			
			lastAdvance = time;
		}
		
		FloatArrayVecmath.setIdentity( modelMatrix );
		
		recomputeMVP( );
		
		visualizer.draw( gl , mvpMatrix );
		
		for( int i = 0 ; i < visualizer.numPoints ; i++ )
		{
			float scale = ( i == highlightedPoint ? 4f : 1.5f ) / width * ( viewFrame[ 1 ] - viewFrame[ 0 ] );
			modelMatrix[ 0 ] = modelMatrix[ 5 ] = scale;
			modelMatrix[ 3 ] = ( float ) visualizer.controlPoints[ i * 2 ];
			modelMatrix[ 7 ] = ( float ) visualizer.controlPoints[ i * 2 + 1 ];
			recomputeMVP( );
			
			cpPolygon.draw( gl , mvpMatrix );
		}
		
		FloatArrayVecmath.setIdentity( modelMatrix );
		
		recomputeMVP( );
		
		for( GL3Object object : objects )
		{
			object.draw( gl , modelMatrix , viewMatrix , projMatrix );
		}
	}
	
	private void recomputeMVP( )
	{
		FloatArrayVecmath.mmul( projMatrix , modelMatrix , mvpMatrix );
		FloatArrayVecmath.transpose( mvpMatrix , mvpMatrix );
	}
	
	@Override
	public void reshape( GLAutoDrawable drawable , int x , int y , int width , int height )
	{
		this.width = width;
		this.height = height;
		
		float cx = ( viewFrame[ 0 ] + viewFrame[ 1 ] ) * 0.5f;
		float cy = ( viewFrame[ 2 ] + viewFrame[ 3 ] ) * 0.5f;
		
		viewFrame[ 2 ] = cy + ( viewFrame[ 0 ] - cx ) * height / width;
		viewFrame[ 3 ] = cy + ( viewFrame[ 1 ] - cx ) * height / width;
		
		recomputeOrtho( );
	}
	
	public void recomputeOrtho( )
	{
		FloatArrayVecmath.ortho( projMatrix , viewFrame[ 0 ] , viewFrame[ 1 ] , viewFrame[ 2 ] , viewFrame[ 3 ] , -100 , 100 );
	}
}
