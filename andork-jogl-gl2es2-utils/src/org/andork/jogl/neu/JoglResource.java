package org.andork.jogl.neu;

import javax.media.opengl.GL2ES2;

public interface JoglResource
{
	
	public abstract void init( GL2ES2 gl );
	
	public abstract void dispose( GL2ES2 gl );
	
}