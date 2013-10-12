package org.andork.curves;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.vecmath.Point2f;

import org.andork.jogl.util.SimplePolygon;
import org.andork.math.curve.FloatHolderType;
import org.andork.math.curve.Point2fType;
import org.andork.math.curve.SmoothRandomWalk;
import org.andork.math.curve.SmoothRandomWalk.RandomFloatHolderGenerator;
import org.andork.math.curve.SmoothRandomWalk.RandomPoint2fGenerator;
import org.andork.vecmath.Vecmath;
import org.omg.CORBA.FloatHolder;

public class CurvesTestScene implements GLEventListener
{
	SimplePolygon						cpPolygon;
	
	final float[ ]						viewFrame			= { -1 , 1 , -1 , 1 };
	
	float[ ]							mvpMatrix			= Vecmath.newMat4f( );
	
	float[ ]							modelMatrix			= Vecmath.newMat4f( );
	float[ ]							viewMatrix			= Vecmath.newMat4f( );
	float[ ]							projMatrix			= Vecmath.newMat4f( );
	
	CurveVisualizer						visualizer			= new CurveVisualizer( 7 , 2 );
	
	int									width;
	int									height;
	
	int									highlightedPoint	= 0;
	float[ ]							transformedPoint	= new float[ 3 ];
	
	SmoothRandomWalk<Point2f>[ ]		pointWalks;
	Point2f								p2f					= new Point2f( );
	
	SmoothRandomWalk<FloatHolder>[ ]	coefWalks;
	FloatHolder							fh					= new FloatHolder( );
	
	private long						lastAdvance			= 0;
	
	@Override
	public void init( GLAutoDrawable drawable )
	{
		pointWalks = new SmoothRandomWalk[ visualizer.controlPoints.length / 2 ];
		
		Point2fType pointType = new Point2fType( );
		RandomPoint2fGenerator generator = new RandomPoint2fGenerator( -2 , 2 );
		
		for( int i = 0 ; i < pointWalks.length ; i++ )
		{
			pointWalks[ i ] = new SmoothRandomWalk<Point2f>( 3 , 1 , pointType , generator );
			pointWalks[ i ].advance( 0 , p2f );
			visualizer.controlPoints[ i * 2 ] = p2f.x;
			visualizer.controlPoints[ i * 2 + 1 ] = p2f.y;
		}
		
		coefWalks = new SmoothRandomWalk[ visualizer.controlPoints.length / 2 ];
		
		FloatHolderType floatHolderType = new FloatHolderType( );
		RandomFloatHolderGenerator floatHolderGenerator = new RandomFloatHolderGenerator( -1 , 1 );
		
		for( int i = 0 ; i < coefWalks.length ; i++ )
		{
			coefWalks[ i ] = new SmoothRandomWalk<FloatHolder>( 3 , 1 , floatHolderType , floatHolderGenerator );
			coefWalks[ i ].advance( 0 , fh );
			visualizer.setCoefficient( i , fh.value );
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
		
		// visualizer.recalculate( );
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
		
		GL3 gl = ( GL3 ) drawable.getGL( );
		
		if( time - lastAdvance > 15 )
		{
			for( int i = 0 ; i < pointWalks.length ; i++ )
			{
				pointWalks[ i ].advance( ( float ) ( time - lastAdvance ) / 100000f , p2f );
				visualizer.controlPoints[ i * 2 ] = p2f.x;
				visualizer.controlPoints[ i * 2 + 1 ] = p2f.y;
			}
			visualizer.recalculate( );
			
			// for( int i = 0 ; i < coefWalks.length ; i++ )
			// {
			// coefWalks[ i ].advance( ( float ) ( time - lastAdvance ) / 1000f , fh );
			// visualizer.setCoefficient( i , fh.value );
			// }
			visualizer.printCoefficients( );
			
			lastAdvance = time;
		}
		
		Vecmath.setIdentity( modelMatrix );
		
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
		
		Vecmath.setIdentity( modelMatrix );
		
		recomputeMVP( );
	}
	
	private void recomputeMVP( )
	{
		Vecmath.mmul( projMatrix , modelMatrix , mvpMatrix );
		Vecmath.transpose( mvpMatrix , mvpMatrix );
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
		Vecmath.ortho( projMatrix , viewFrame[ 0 ] , viewFrame[ 1 ] , viewFrame[ 2 ] , viewFrame[ 3 ] , -100 , 100 );
	}
}
