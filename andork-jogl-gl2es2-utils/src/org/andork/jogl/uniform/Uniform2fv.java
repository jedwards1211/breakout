package org.andork.jogl.uniform;

import com.jogamp.opengl.GL2ES2;

public class Uniform2fv implements Uniform {
	int count = 1;
	float[] value;
	int value_offset;

	public Uniform2fv count(int count) {
		this.count = count;
		return this;
	}

	@Override
	public void put(GL2ES2 gl, int location) {
		gl.glUniform2fv(location, count, value, value_offset);
	}

	public Uniform2fv value(float... value) {
		this.value = value;
		return this;
	}

	public float[] value() {
		return value;
	}

	public Uniform2fv value_offset(int value_offset) {
		this.value_offset = value_offset;
		return this;
	}
}
