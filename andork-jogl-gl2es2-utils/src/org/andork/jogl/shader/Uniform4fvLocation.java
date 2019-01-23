package org.andork.jogl.shader;

import com.jogamp.opengl.GL2ES2;

public class Uniform4fvLocation extends UniformLocation {
	public Uniform4fvLocation(String name) {
		super(name);
	}
	
	public void put(GL2ES2 gl, float... value) {
		gl.glUniform4fv(location(), 1, value, 0);
	}
}
