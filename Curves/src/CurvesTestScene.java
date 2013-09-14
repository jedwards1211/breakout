import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.andork.jogl.util.SimplePolygon;
import org.andork.vecmath.FloatArrayVecmath;

public class CurvesTestScene implements GLEventListener
{
	SimplePolygon	cpPolygon;
	
	float[ ]		scaleMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	float[ ]		transMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	float[ ]		mvpMatrix			= FloatArrayVecmath.newIdentityMatrix( );
	
	CurveVisualizer	visualizer			= new CurveVisualizer( 6 , 2 );
	
	int				width;
	int				height;
	
	int				highlightedPoint	= 0;
	
	public void draw( GL3 gl , float[ ] mvMatrix , float[ ] pMatrix )
	{
	}
	
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
			visualizer.controlPoints[ i * 2 ] = Math.random( ) * 1.5 - .75;
			visualizer.controlPoints[ i * 2 + 1 ] = 0.25 + Math.random( ) * 1.5 - .75;
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
		
		FloatArrayVecmath.setIdentity( mvpMatrix );
		
		visualizer.draw( gl , mvpMatrix );
		// mvpMatrix[ 0 ] = 0.1f
		// mvpMatrix[ 5 ] = 0.1f;
		// mvpMatrix[ 12 ] = 0;
		// cpPolygon.draw( gl , mvpMatrix );
		
		// mvpMatrix[ 12 ] = 0.3f;
		// cpPolygon.draw( gl , mvpMatrix );
		for( int i = 0 ; i < visualizer.numPoints ; i++ )
		{
			FloatArrayVecmath.setIdentity( scaleMatrix );
			FloatArrayVecmath.setIdentity( transMatrix );
			FloatArrayVecmath.setIdentity( mvpMatrix );
			scaleMatrix[ 0 ] = ( float ) 3 / width;
			scaleMatrix[ 5 ] = ( float ) 3 / height;
			transMatrix[ 3 ] = ( float ) visualizer.controlPoints[ i * 2 ];
			transMatrix[ 7 ] = ( float ) visualizer.controlPoints[ i * 2 + 1 ];
			
			FloatArrayVecmath.mmulAffine( transMatrix , scaleMatrix , mvpMatrix );
			FloatArrayVecmath.transpose( mvpMatrix , mvpMatrix );
			
			cpPolygon.draw( gl , mvpMatrix );
			
			if( i == highlightedPoint )
			{
				mvpMatrix[ 0 ] = ( float ) 5 / width;
				mvpMatrix[ 5 ] = ( float ) 5 / height;
				cpPolygon.draw( gl , mvpMatrix );
			}
		}
	}
	
	@Override
	public void reshape( GLAutoDrawable drawable , int x , int y , int width , int height )
	{
		this.width = width;
		this.height = height;
	}
}
