package org.andork.jogl.shader;

import com.jogamp.opengl.GL2ES2;

public class UniformMatrix3fvLocation extends UniformLocation {
	public UniformMatrix3fvLocation(String name) {
		super(name);
	}
	
	public void put(GL2ES2 gl, float... value) {
		gl.glUniformMatrix3fv(location(), 1, false, value, 0);
	}
}
