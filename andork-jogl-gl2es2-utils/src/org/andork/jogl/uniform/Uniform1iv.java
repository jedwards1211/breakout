package org.andork.jogl.uniform;

import com.jogamp.opengl.GL2ES2;

public class Uniform1iv implements Uniform {
	int count = 1;
	int[] value;
	int value_offset;

	public Uniform1iv count(int count) {
		this.count = count;
		return this;
	}

	@Override
	public void put(GL2ES2 gl, int location) {
		gl.glUniform1iv(location, count, value, value_offset);
	}

	public Uniform1iv value(int... value) {
		this.value = value;
		return this;
	}

	public int[] value() {
		return value;
	}

	public Uniform1iv value_offset(int value_offset) {
		this.value_offset = value_offset;
		return this;
	}
}
