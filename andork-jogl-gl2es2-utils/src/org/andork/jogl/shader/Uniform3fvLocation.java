package org.andork.jogl.shader;

import com.jogamp.opengl.GL2ES2;

public class Uniform3fvLocation extends UniformLocation {
	public Uniform3fvLocation(String name) {
		super(name);
	}
	
	public void put(GL2ES2 gl, float... value) {
		gl.glUniform3fv(location(), 1, value, 0);
	}
}
