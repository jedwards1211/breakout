package org.andork.jogl.shader;

import com.jogamp.opengl.GL2ES2;

public class Uniform1ivLocation extends UniformLocation {
	public Uniform1ivLocation(String name) {
		super(name);
	}

	public void put(GL2ES2 gl, int... value) {
		gl.glUniform1iv(location(), 1, value, 0);
	}
}