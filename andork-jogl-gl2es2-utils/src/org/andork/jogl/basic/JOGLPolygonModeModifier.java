package org.andork.jogl.basic;

import javax.media.opengl.GL2ES2;

public class JOGLPolygonModeModifier implements JOGLModifier
{
	private int	cullFace	= GL2ES2.GL_NONE;
	
	public JOGLPolygonModeModifier( int cullFace )
	{
		super( );
		this.cullFace = cullFace;
	}

	@Override
	public void beforeDraw( GL2ES2 gl , JOGLObject object )
	{
		gl.glEnable( GL2ES2.GL_CULL_FACE );
		gl.glCullFace( cullFace );
	}
	
	@Override
	public void afterDraw( GL2ES2 gl , JOGLObject object )
	{
		gl.glDisable( GL2ES2.GL_CULL_FACE );
	}
	
}
