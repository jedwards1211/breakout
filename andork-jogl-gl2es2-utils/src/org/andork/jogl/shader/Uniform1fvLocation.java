package org.andork.jogl.shader;

import com.jogamp.opengl.GL2ES2;

public class Uniform1fvLocation extends UniformLocation {
	public Uniform1fvLocation(String name) {
		super(name);
	}

	public void put(GL2ES2 gl, float... value) {
		gl.glUniform1fv(location(), 1, value, 0);
	}

	public void put(GL2ES2 gl, int count, float... value) {
		gl.glUniform1fv(location(), count, value, 0);
	}
}
