package org.andork.jogl.basic;

import javax.media.opengl.GL3;

public interface GL3Object
{
	public void init( GL3 gl );
	
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
	public void draw( GL3 gl , float[ ] m , float[ ] n , float[ ] v , float[ ] p );
}
