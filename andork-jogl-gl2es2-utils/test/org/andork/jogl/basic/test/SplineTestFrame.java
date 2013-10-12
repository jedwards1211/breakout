package org.andork.jogl.basic.test;

import java.nio.ByteBuffer;
import java.util.Random;

import javax.media.opengl.GL2ES2;
import javax.vecmath.Point3f;

import org.andork.jogl.basic.BasicJOGLSetup;
import org.andork.jogl.basic.BasicJOGLObject;
import org.andork.jogl.basic.BasicJOGLScene;
import org.andork.jogl.basic.BufferHelper;
import org.andork.jogl.basic.JOGLDepthModifier;
import org.andork.jogl.basic.BasicJOGLObject.BasicVertexShader;
import org.andork.jogl.basic.BasicJOGLObject.DepthFragmentShader;
import org.andork.math.curve.BSpline3f;
import org.andork.math.curve.BSpline3f.Evaluator;
import org.andork.math.curve.BSplines;
import org.andork.util.IterableUtils;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

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
		
		BasicJOGLObject obj = new BasicJOGLObject( );
		
		BufferHelper bh = new BufferHelper( );
		
		for( Point3f p : BSpline3f.iterable( spline , evaluator , IterableUtils.range( 0f , 1f , true , 0.0001f ) ) )
		{
			bh.put( p.x , p.y , p.z );
		}
		
		ByteBuffer vertexBuffer = bh.toByteBuffer( );
		obj.addVertexBuffer( vertexBuffer );
		obj.vertexCount( vertexBuffer.capacity( ) / 12 );
		obj.drawMode( GL2ES2.GL_LINE_STRIP );
		obj.vertexShaderCode( new BasicVertexShader( ).posDim( 3 ).passPosToFragmentShader( true ).toString( ) );
		obj.add( obj.new Attribute3fv( ).name( "a_pos" ) );
		obj.fragmentShaderCode( new DepthFragmentShader( ).radius( 9 ).toString( ) );
		obj.add( new JOGLDepthModifier( ) );
		obj.transpose( true );
		
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
