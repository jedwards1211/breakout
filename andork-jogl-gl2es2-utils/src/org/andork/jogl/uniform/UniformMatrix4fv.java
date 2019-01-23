package org.andork.jogl.uniform;

import org.andork.jogl.shader.UniformMatrix4fvLocation;

import com.jogamp.opengl.GL2ES2;

public class UniformMatrix4fv implements Uniform {
	int count;
	boolean transpose;
	float[] value;
	int value_offset;

	public UniformMatrix4fv count(int count) {
		this.count = count;
		return this;
	}

	@Override
	public void put(GL2ES2 gl, int location) {
		gl.glUniformMatrix4fv(location, count, transpose, value, value_offset);
	}

	public UniformMatrix4fv transpose(boolean transpose) {
		this.transpose = transpose;
		return this;
	}

	public UniformMatrix4fv value(float[] value) {
		this.value = value;
		return this;
	}

	public float[] value() {
		return value;
	}

	public UniformMatrix4fv value_offset(int value_offset) {
		this.value_offset = value_offset;
		return this;
	}
	
	public void put(GL2ES2 gl, UniformMatrix4fvLocation location) {
		put(gl, location.location());
	}
}
