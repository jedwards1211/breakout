package org.andork.jogl.util;

import javax.media.opengl.GL3;

public interface GL3Modifier
{
	public void beforeDraw( GL3 gl , GL3Object object );
	
	public void afterDraw( GL3 gl , GL3Object object );
}
