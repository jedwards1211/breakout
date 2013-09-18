import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.andork.jogl.util.SimplePolygon;
import org.andork.vecmath.FloatArrayVecmath;

public class CurvesTestScene implements GLEventListener
{
	SimplePolygon	cpPolygon;
	
	final float[ ]	viewFrame			= { -1 , 1 , -1 , 1 };
	
	float[ ]		mvpMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	
	float[ ]		modelMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	float[ ]		viewMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	float[ ]		projMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	
	CurveVisualizer	visualizer			= new CurveVisualizer( 7 , 2 );
	
	int				width;
	int				height;
	
	int				highlightedPoint	= 0;
	float[ ]		transformedPoint	= new float[ 3 ];
	
	@Override
	public void init( GLAutoDrawable drawable )
	{
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
		
		for( int i = 0 ; i < visualizer.numPoints ; i++ )
		{
			visualizer.controlPoints[ i * 2 ] = Math.random( ) - 0.5;
			visualizer.controlPoints[ i * 2 + 1 ] = Math.random( ) - 0.5;
		}
		
		visualizer.recalculate( );
		
		for( float x = 0 ; x <= 1 ; x += 0.1f )
		{
			for( float y = 1 ; y >= 0 ; y -= 0.1f )
			{
				System.out.format( "%9.2f  " , visualizer.eval( new double[ ] { x , y } ) );
			}
			System.out.println( );
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
		GL3 gl = ( GL3 ) drawable.getGL( );
		
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
