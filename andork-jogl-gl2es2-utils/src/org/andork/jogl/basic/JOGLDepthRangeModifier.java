package org.andork.jogl.basic;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

public class JOGLDepthRangeModifier implements JOGLModifier
{
	float	prevZnear;
	float	prevZfar;
	float	zNear;
	float	zFar;
	
	public JOGLDepthRangeModifier( float zNear , float zFar )
	{
		super( );
		this.zNear = zNear;
		this.zFar = zFar;
	}
	
	@Override
	public void beforeDraw( GL2ES2 gl , JOGLObject object )
	{
		gl.glDepthRange( zNear , zFar );
	}
	
	@Override
	public void afterDraw( GL2ES2 gl , JOGLObject object )
	{
		gl.glDepthRange( prevZnear , prevZfar );
	}
}
