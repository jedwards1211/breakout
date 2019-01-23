package org.andork.jogl.shader;

import com.jogamp.opengl.GL2ES2;

public class UniformMatrix4fvLocation extends UniformLocation {
	public UniformMatrix4fvLocation(String name) {
		super(name);
	}
	
	public void put(GL2ES2 gl, float... value) {
		gl.glUniformMatrix4fv(location(), 1, false, value, 0);
	}
}
