package org.andork.jogl.basic;

import javax.media.opengl.GL3;

public class GL3BlendModifier implements GL3Modifier
{
	private boolean	prevEnabled;
	
	private int		sfactor	= GL3.GL_SRC_ALPHA;
	private int		dfactor	= GL3.GL_ONE_MINUS_SRC_ALPHA;
	
	public GL3BlendModifier sfactor( int sfactor )
	{
		this.sfactor = sfactor;
		return this;
	}
	
	public GL3BlendModifier dfactor( int dfactor )
	{
		this.dfactor = dfactor;
		return this;
	}
	
	@Override
	public void beforeDraw( GL3 gl , GL3Object object )
	{
		prevEnabled = gl.glIsEnabled( GL3.GL_BLEND );
		
		gl.glEnable( GL3.GL_BLEND );
		gl.glBlendFunc( sfactor , dfactor );
	}
	
	@Override
	public void afterDraw( GL3 gl , GL3Object object )
	{
		if( !prevEnabled )
		{
			gl.glDisable( GL3.GL_BLEND );
		}
	}
	
}
