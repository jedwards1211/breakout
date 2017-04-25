package org.andork.jogl.uniform;

import com.jogamp.opengl.GL2ES2;

public abstract interface Uniform {
	public abstract void put(GL2ES2 gl, int location);
}
