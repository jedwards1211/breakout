package org.andork.jogl.util;

import java.nio.ByteBuffer;
import java.util.Random;

import javax.media.opengl.GL3;
import javax.vecmath.Point3f;

import org.andork.jogl.util.BasicGL3Object.BasicVertexShader;
import org.andork.jogl.util.BasicGL3Object.FlatFragmentShader;
import org.andork.math.curve.BSpline3f;
import org.andork.math.curve.BSpline3f.Evaluator;
import org.andork.math.curve.BSplines;
import org.andork.util.IterableUtils;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

@SuppressWarnings( "serial" )
public class SplineTestFrame extends BasicGL3Frame
{
	
	@Override
	protected void init( )
	{
		super.init( );
		
		navigator.setMoveFactor( 0.2f );
		
		AnimatorBase animator = new FPSAnimator( glCanvas , 60 );
		animator.start( );
	}
	
	@Override
	protected BasicGL3Scene createScene( )
	{
		BasicGL3Scene scene = new BasicGL3Scene( );
		
		int degree = 3;
		int numControlPoints = 100;
		
		float[ ] knots = BSplines.createUniformKnots( degree , numControlPoints );
		Point3f[ ] controlPoints = new Point3f[ numControlPoints ];
		
		Random rand = new Random( );
		
		for( int i = 0 ; i < numControlPoints ; i++ )
		{
			float x = ( rand.nextFloat( ) - 0.5f ) * 20;
			float y = ( rand.nextFloat( ) - 0.5f ) * 20;
			float z = ( rand.nextFloat( ) - 0.5f ) * 20;
			
			controlPoints[ i ] = new Point3f( x , y , z );
		}
		
		BSpline3f spline = new BSpline3f( degree , knots , controlPoints );
		Evaluator evaluator = new Evaluator( degree );
		
		BasicGL3Object obj = new BasicGL3Object( );
		
		BufferHelper bh = new BufferHelper( );
		
		for( Point3f p : BSpline3f.iterable( spline , evaluator , IterableUtils.range( 0f , 1f , true , 0.0001f ) ) )
		{
			bh.put( p.x , p.y , p.z );
		}
		
		ByteBuffer vertexBuffer = bh.toByteBuffer( );
		obj.addVertexBuffer( vertexBuffer );
		obj.vertexCount( vertexBuffer.capacity( ) / 12 );
		obj.drawMode( GL3.GL_LINE_STRIP );
		obj.vertexShaderCode( new BasicVertexShader( ).posDim( 3 ).toString( ) );
		obj.add( obj.new AttributeVec3fv( ).name( "a_pos" ) );
		obj.fragmentShaderCode( new FlatFragmentShader( ).toString( ) );
		obj.transpose( true );
		
		scene.add( obj );
		
		return scene;
	}
	
	public static void main( String[ ] args )
	{
		SplineTestFrame frame = new SplineTestFrame( );
		frame.setVisible( true );
	}
}
