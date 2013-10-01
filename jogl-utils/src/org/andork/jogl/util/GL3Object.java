package org.andork.jogl.util;

import javax.media.opengl.GL3;

public interface GL3Object
{
	public void init( GL3 gl );
	
	public void draw( GL3 gl , float[ ] m , float[ ] v , float[ ] p );
}
