package org.andork.jogl.uniform;

import com.jogamp.opengl.GL2ES2;

public class Uniform3fv implements Uniform {
	int count = 1;
	float[] value;
	int value_offset;

	public Uniform3fv count(int count) {
		this.count = count;
		return this;
	}

	@Override
	public void put(GL2ES2 gl, int location) {
		gl.glUniform3fv(location, count, value, value_offset);
	}

	public Uniform3fv value(float... value) {
		this.value = value;
		return this;
	}

	public float[] value() {
		return value;
	}

	public Uniform3fv value_offset(int value_offset) {
		this.value_offset = value_offset;
		return this;
	}
}
