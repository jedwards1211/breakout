package org.andork.jogl.shader;

import com.jogamp.opengl.GL2ES2;

public abstract class GLLocation {
	protected final String name;
	protected int location;

	public GLLocation(String name) {
		this.name = name;
	}

	public int location() {
		return location;
	}

	public String name() {
		return name;
	}
	
	public abstract void update(GL2ES2 gl, int program);
}
