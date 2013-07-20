package org.andork.torquescape.app;

import java.util.Arrays;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Matrix4f;

public class TorquescapeRenderer
{
	public TorquescapeRenderer( )
	{
		m[ 0 ] = 1;
		m[ 5 ] = 1;
		m[ 10 ] = 1;
		m[ 15 ] = 1;
		
	}
	
	float		param;
	float[ ]	m		= new float[ 16 ];
	float[ ]	mdebug	= new float[ 16 ];
	
	GLU			glu		= null;
	
	public void setup( GL2 gl , int width , int height )
	{
		if( glu == null )
		{
			glu = GLU.createGLU( gl );
		}
		gl.glMatrixMode( GL2.GL_PROJECTION );
		gl.glLoadIdentity( );
		glu.gluPerspective( 60 , ( float ) width / height , 0.01 , 10000 );
	}
	
	public void setModelMatrix( Matrix4f mat )
	{
		m[ 0 ] = mat.getM00( );
		m[ 1 ] = mat.getM01( );
		m[ 2 ] = mat.getM02( );
		m[ 3 ] = mat.getM03( );
		m[ 4 ] = mat.getM10( );
		m[ 5 ] = mat.getM11( );
		m[ 6 ] = mat.getM12( );
		m[ 7 ] = mat.getM13( );
		m[ 8 ] = mat.getM20( );
		m[ 9 ] = mat.getM21( );
		m[ 10 ] = mat.getM22( );
		m[ 11 ] = mat.getM23( );
		m[ 12 ] = mat.getM30( );
		m[ 13 ] = mat.getM31( );
		m[ 14 ] = mat.getM32( );
		m[ 15 ] = mat.getM33( );
	}
	
	public void render( GL2 gl , int width , int height )
	{
		gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT );
		
		gl.glMatrixMode( GL2.GL_MODELVIEW );
		gl.glLoadIdentity( );
		gl.glLoadTransposeMatrixf( m , 0 );
		gl.glGetFloatv( GL2.GL_MODELVIEW_MATRIX , mdebug , 0 );
		System.out.println( Arrays.toString( mdebug ) );
		
		gl.glBegin( GL.GL_TRIANGLES );
		gl.glColor3f( 1 , 0 , 0 );
		gl.glVertex3f( -1 , -1 , -0.1f );
		gl.glColor3f( 0 , 1 , 0 );
		gl.glVertex3f( 0 , 1 , 0 );
		gl.glColor3f( 0 , 0 , 1 );
		gl.glVertex3f( 1 , -1 , 0 );
		gl.glEnd( );
		gl.glFlush( );
	}
	
	public void update( )
	{
		// param += Math.PI / 180;
		// m[ 0 ] = ( float ) Math.cos( param );
		// m[ 1 ] = ( float ) Math.sin( param );
		// m[ 2 ] = 0;
		// m[ 3 ] = 0;
		// m[ 4 ] = -( float ) Math.sin( param );
		// m[ 5 ] = ( float ) Math.cos( param );
		// m[ 6 ] = 0;
		// m[ 7 ] = 0;
		// m[ 15 ] = 1;
	}
	
}
