package org.andork.jogl;

import javax.media.opengl.GL2ES2;

public class JOGLBlendModifier implements JOGLModifier
{
	private boolean	prevEnabled;
	
	private int		sfactor	= GL2ES2.GL_SRC_ALPHA;
	private int		dfactor	= GL2ES2.GL_ONE_MINUS_SRC_ALPHA;
	
	public JOGLBlendModifier sfactor( int sfactor )
	{
		this.sfactor = sfactor;
		return this;
	}
	
	public JOGLBlendModifier dfactor( int dfactor )
	{
		this.dfactor = dfactor;
		return this;
	}
	
	@Override
	public void beforeDraw( GL2ES2 gl , JOGLObject object )
	{
		prevEnabled = gl.glIsEnabled( GL2ES2.GL_BLEND );
		
		gl.glEnable( GL2ES2.GL_BLEND );
		gl.glBlendFunc( sfactor , dfactor );
	}
	
	@Override
	public void afterDraw( GL2ES2 gl , JOGLObject object )
	{
		if( !prevEnabled )
		{
			gl.glDisable( GL2ES2.GL_BLEND );
		}
	}
	
}
