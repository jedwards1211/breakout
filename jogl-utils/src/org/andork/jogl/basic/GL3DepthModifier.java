package org.andork.jogl.basic;

import javax.media.opengl.GL3;

public class GL3DepthModifier implements GL3Modifier
{
	private boolean	prevDepthTestEnabled;
	
	@Override
	public void beforeDraw( GL3 gl , GL3Object object )
	{
		prevDepthTestEnabled = gl.glIsEnabled( GL3.GL_DEPTH_TEST );
		
		gl.glEnable( GL3.GL_DEPTH_TEST );
	}
	
	@Override
	public void afterDraw( GL3 gl , GL3Object object )
	{
		if( !prevDepthTestEnabled )
		{
			gl.glDisable( GL3.GL_DEPTH_TEST );
		}
	}
	
}
