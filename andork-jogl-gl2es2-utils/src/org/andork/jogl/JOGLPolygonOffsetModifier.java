package org.andork.jogl;

import javax.media.opengl.GL2ES2;

public class JOGLPolygonOffsetModifier implements JOGLModifier
{
	private boolean	prevEnabled;
	
	private float	factor , units;
	
	public JOGLPolygonOffsetModifier( float factor , float units )
	{
		super( );
		this.factor = factor;
		this.units = units;
	}

	@Override
	public void beforeDraw( GL2ES2 gl , JOGLObject object )
	{
		prevEnabled = gl.glIsEnabled( GL2ES2.GL_POLYGON_OFFSET_FILL );
		gl.glEnable( GL2ES2.GL_POLYGON_OFFSET_FILL );
		gl.glPolygonOffset( factor , units );
	}
	
	@Override
	public void afterDraw( GL2ES2 gl , JOGLObject object )
	{
		if( !prevEnabled )
		{
			gl.glDisable( GL2ES2.GL_POLYGON_OFFSET_FILL );
		}
	}
	
}
