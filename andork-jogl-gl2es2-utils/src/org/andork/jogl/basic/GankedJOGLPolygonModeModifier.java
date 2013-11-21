package org.andork.jogl.basic;

import javax.media.opengl.GL2ES2;

public class GankedJOGLPolygonModeModifier implements JOGLModifier
{
	private int		cullFace = GL2ES2.GL_NONE;
	
	@Override
	public void beforeDraw( GL2ES2 gl , JOGLObject object )
	{
//		gl.glCullFace( cullFace );
		gl.glDisable( GL2ES2.GL_CULL_FACE );
	}
	
	@Override
	public void afterDraw( GL2ES2 gl , JOGLObject object )
	{
		gl.glEnable( GL2ES2.GL_CULL_FACE );
	}
	
}
