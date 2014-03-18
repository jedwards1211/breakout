package org.andork.jogl;

import javax.media.opengl.GL2ES2;

public interface JOGLObject
{
	public void init( GL2ES2 gl );
	
	/**
	 * @param gl
	 * @param m
	 *            the model matrix
	 * @param n
	 *            the normal matrix (transpose inverse of the upper-left 3x3 of {@code m})
	 * @param v
	 *            the view matrix
	 * @param p
	 *            the projection matrix
	 */
	public void draw( GL2ES2 gl , float[ ] m , float[ ] n , float[ ] v , float[ ] p );
	
	public void destroy( GL2ES2 gl );
}
