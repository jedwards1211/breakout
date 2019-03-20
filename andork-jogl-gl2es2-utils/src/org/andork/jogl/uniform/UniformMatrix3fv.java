package org.andork.jogl.uniform;

import org.andork.jogl.shader.UniformMatrix3fvLocation;

import com.jogamp.opengl.GL2ES2;

public class UniformMatrix3fv implements Uniform {
	int count;
	boolean transpose;
	float[] value;
	int value_offset;

	public UniformMatrix3fv count(int count) {
		this.count = count;
		return this;
	}

	@Override
	public void put(GL2ES2 gl, int location) {
		gl.glUniformMatrix3fv(location, count, transpose, value, value_offset);
	}

	public UniformMatrix3fv transpose(boolean transpose) {
		this.transpose = transpose;
		return this;
	}

	public UniformMatrix3fv value(float[] value) {
		this.value = value;
		return this;
	}

	public float[] value() {
		return value;
	}

	public UniformMatrix3fv value_offset(int value_offset) {
		this.value_offset = value_offset;
		return this;
	}
	
	public void put(GL2ES2 gl, UniformMatrix3fvLocation location) {
		put(gl, location.location());
	}
}
