package org.andork.jogl.basic;

import javax.media.opengl.GL2ES2;

public class JOGLDepthModifier implements JOGLModifier
{
	private boolean	prevDepthTestEnabled;
	
	@Override
	public void beforeDraw( GL2ES2 gl , JOGLObject object )
	{
		prevDepthTestEnabled = gl.glIsEnabled( GL2ES2.GL_DEPTH_TEST );
		
		gl.glEnable( GL2ES2.GL_DEPTH_TEST );
	}
	
	@Override
	public void afterDraw( GL2ES2 gl , JOGLObject object )
	{
		if( !prevDepthTestEnabled )
		{
			gl.glDisable( GL2ES2.GL_DEPTH_TEST );
		}
	}
	
}
