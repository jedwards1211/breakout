package org.andork.jogl.shader;

import com.jogamp.opengl.GL2ES2;

public class AttribLocation extends GLLocation {
	public AttribLocation(String name) {
		super(name);
	}

	@Override
	public void update(GL2ES2 gl, int program) {
		location = gl.glGetAttribLocation(program, name);
	}
}
