package org.andork.jogl.util;

import org.andork.jogl.JoglResource;

import com.jogamp.opengl.GL2ES2;

public class ShaderProgram extends com.jogamp.opengl.util.glsl.ShaderProgram implements JoglResource {
	@Override
	public void dispose(GL2ES2 gl) {
		destroy(gl);
	}
}
