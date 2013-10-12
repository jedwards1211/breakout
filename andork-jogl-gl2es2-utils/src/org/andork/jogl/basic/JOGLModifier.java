package org.andork.jogl.basic;

import javax.media.opengl.GL2ES2;

public interface JOGLModifier
{
	public void beforeDraw( GL2ES2 gl , JOGLObject object );
	
	public void afterDraw( GL2ES2 gl , JOGLObject object );
}
