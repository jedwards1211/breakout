package org.andork.jogl.uniform;

import com.jogamp.opengl.GL2ES2;

public class Uniform4fv implements Uniform {
	int count = 1;
	float[] value;
	int value_offset;

	public Uniform4fv count(int count) {
		this.count = count;
		return this;
	}

	@Override
	public void put(GL2ES2 gl, int location) {
		gl.glUniform4fv(location, count, value, value_offset);
	}

	public Uniform4fv value(float... value) {
		this.value = value;
		return this;
	}

	public float[] value() {
		return value;
	}

	public Uniform4fv value_offset(int value_offset) {
		this.value_offset = value_offset;
		return this;
	}
}
