package org.andork.jogl;

import javax.media.opengl.GL2ES2;

public interface JOGLResource
{
	
	public abstract void init( GL2ES2 gl );
	
	public abstract void destroy( GL2ES2 gl );
	
}