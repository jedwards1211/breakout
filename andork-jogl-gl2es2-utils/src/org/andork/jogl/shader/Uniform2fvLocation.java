package org.andork.jogl.shader;

import com.jogamp.opengl.GL2ES2;

public class Uniform2fvLocation extends UniformLocation {
	public Uniform2fvLocation(String name) {
		super(name);
	}
	
	public void put(GL2ES2 gl, float... value) {
		gl.glUniform2fv(location(), 1, value, 0);
	}
}
