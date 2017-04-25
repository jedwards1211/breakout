package org.andork.jogl.uniform;

import com.jogamp.opengl.GL2ES2;

public class Uniform1fv implements Uniform {
	int count = 1;
	float[] value;
	int value_offset;

	public Uniform1fv count(int count) {
		this.count = count;
		return this;
	}

	@Override
	public void put(GL2ES2 gl, int location) {
		gl.glUniform1fv(location, count, value, value_offset);
	}

	public Uniform1fv value(float... value) {
		this.value = value;
		return this;
	}

	public float[] value() {
		return value;
	}

	public Uniform1fv value_offset(int value_offset) {
		this.value_offset = value_offset;
		return this;
	}
}
