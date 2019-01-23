package org.andork.jogl.shader;

import com.jogamp.opengl.GL2ES2;

public class UniformLocation extends GLLocation {
	public UniformLocation(String name) {
		super(name);
	}

	@Override
	public void update(GL2ES2 gl, int program) {
		location = gl.glGetUniformLocation(program, name);
	}
}
