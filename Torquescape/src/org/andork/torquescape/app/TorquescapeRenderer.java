package org.andork.torquescape.app;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class TorquescapeRenderer
{
	float		param;
	float[ ]	m	= new float[ 16 ];
	
	public void setup( GL2 gl2 , int width , int height )
	{
		
	}
	
	public void render( GL2 gl , int width , int height )
	{
		gl.glMatrixMode( GL2.GL_MODELVIEW );
		gl.glLoadMatrixf( m , 0 );
		
		gl.glBegin( GL.GL_TRIANGLES );
		gl.glColor3f( 1 , 0 , 0 );
		gl.glVertex2f( -1 , -1 );
		gl.glColor3f( 0 , 1 , 0 );
		gl.glVertex2f( 0 , 1 );
		gl.glColor3f( 0 , 0 , 1 );
		gl.glVertex2f( 1 , -1 );
		gl.glEnd( );
	}
	
	public void update( )
	{
		param += Math.PI / 180;
		m[ 0 ] = ( float ) Math.cos( param );
		m[ 1 ] = ( float ) Math.sin( param );
		m[ 2 ] = 0;
		m[ 3 ] = 0;
		m[ 4 ] = -( float ) Math.sin( param );
		m[ 5 ] = ( float ) Math.cos( param );
		m[ 6 ] = 0;
		m[ 7 ] = 0;
		m[ 15 ] = 1;
	}
	
}
