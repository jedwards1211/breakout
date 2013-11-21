package org.andork.jogl.basic.test;

import java.nio.ByteBuffer;
import java.util.Random;

import javax.media.opengl.GL2ES2;
import javax.vecmath.Point3f;

import org.andork.jogl.basic.BasicJOGLObject;
import org.andork.jogl.basic.BasicJOGLObject.BasicVertexShader;
import org.andork.jogl.basic.BasicJOGLObject.DepthFragmentShader;
import org.andork.jogl.basic.BasicJOGLScene;
import org.andork.jogl.basic.BasicJOGLSetup;
import org.andork.jogl.basic.BufferHelper;
import org.andork.jogl.basic.JOGLDepthModifier;
import org.andork.math.curve.BSpline3f;
import org.andork.math.curve.FastFloatBSplineEvaluator;
import org.andork.math.curve.FloatArrayBSpline;
import org.andork.math.curve.FloatBSplineEvaluator;
import org.andork.math.curve.BSpline3f.Evaluator;
import org.andork.math.curve.BSplines;
import org.andork.util.IterableUtils;

@SuppressWarnings( "serial" )
public class SplineTestFrame extends BasicJOGLSetup
{
	@Override
	protected void init( )
	{
		super.init( );
		navigator.setMoveFactor( 0.2f );
	}
	
	@Override
	protected BasicJOGLScene createScene( )
	{
		BasicJOGLScene scene = new BasicJOGLScene( );
		
		int degree = 3;
		int numControlPoints = 100;
		
		float[ ] knots = BSplines.createUniformKnots( degree , numControlPoints );
		Point3f[ ] controlPoints = new Point3f[ numControlPoints ];
		
		FloatArrayBSpline spline = new FloatArrayBSpline( );
		spline.degree = degree;
		spline.dimension = 3;
		spline.points = new float[ numControlPoints * spline.dimension ];
		spline.knots = knots;
		
		Random rand = new Random( );
		
		for( int i = 0 ; i < numControlPoints ; i++ )
		{
			float x = ( rand.nextFloat( ) - 0.5f ) * 20;
			float y = ( rand.nextFloat( ) - 0.5f ) * 20;
			float z = ( rand.nextFloat( ) - 0.5f ) * 20;
			
			controlPoints[ i ] = new Point3f( x , y , z );
			
			spline.points[ i * 3 ] = x;
			spline.points[ i * 3 + 1 ] = y;
			spline.points[ i * 3 + 2 ] = z;
		}
		
		FastFloatBSplineEvaluator evaluator = new FastFloatBSplineEvaluator( ).bspline( spline );
		
		BasicJOGLObject obj = new BasicJOGLObject( );
		
		BufferHelper bh = new BufferHelper( );
		
		float[ ] out = new float[ spline.dimension ];
		
		for( float f = 0 ; f <= 1f ; f += 0.0001f )
		{
			evaluator.eval( f , out );
			for( float coord : out )
			{
				bh.put( coord );
			}
		}
		
		ByteBuffer vertexBuffer = bh.toByteBuffer( );
		obj.addVertexBuffer( vertexBuffer );
		obj.vertexCount( vertexBuffer.capacity( ) / 12 );
		obj.drawMode( GL2ES2.GL_LINE_STRIP );
		obj.vertexShaderCode( new BasicVertexShader( ).posDim( 3 ).passPosToFragmentShader( true ).toString( ) );
		obj.add( obj.new Attribute3fv( ).name( "a_pos" ) );
		obj.fragmentShaderCode( new DepthFragmentShader( ).radius( 9 ).toString( ) );
		obj.add( new JOGLDepthModifier( ) );
		obj.transpose( false );
		
		scene.add( obj );
		
		return scene;
	}
	
	public static void main( String[ ] args )
	{
		SplineTestFrame frame = new SplineTestFrame( );
		frame.glWindow.setVisible( true );
		frame.waitUntilClosed( );
	}
}
