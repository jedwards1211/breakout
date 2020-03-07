package org.andork.jogl;

import com.jogamp.opengl.GL3;

public interface JoglFilter {
	/**
	 * APplies
	 * 
	 * @param gl
	 * @param width       the width of the viewport
	 * @param height      the height of the viewport
	 * @param framebuffer the gl framebuffer object
	 * @param texture     the gl texture object
	 * @param s           the s coordinate <= 1 corresponding to x = width
	 * @param t           the t coordinate <= 1 corresponding to y = height
	 */
	void apply(GL3 gl, int width, int height, int framebuffer, int texture, float s, float t);
}
