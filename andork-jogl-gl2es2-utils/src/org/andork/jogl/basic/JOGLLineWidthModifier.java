package org.andork.jogl.basic;

import javax.media.opengl.GL2ES2;

public class JOGLLineWidthModifier implements JOGLModifier
{
	private float[ ]	prevLineWidth	= new float[ 1 ];
	
	private float		lineWidth;
	
	public JOGLLineWidthModifier( float lineWidth )
	{
		super( );
		this.lineWidth = lineWidth;
	}
	
	@Override
	public void beforeDraw( GL2ES2 gl , JOGLObject object )
	{
		gl.glGetFloatv( GL2ES2.GL_LINE_WIDTH , prevLineWidth , 0 );
		gl.glLineWidth( lineWidth );
	}
	
	@Override
	public void afterDraw( GL2ES2 gl , JOGLObject object )
	{
		gl.glLineWidth( prevLineWidth[ 0 ] );
	}
	
}
