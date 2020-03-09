package org.andork.jogl.shader;

import java.awt.Color;

import com.jogamp.opengl.GL2ES2;

public class Uniform4fvLocation extends UniformLocation {
	public Uniform4fvLocation(String name) {
		super(name);
	}

	public void put(GL2ES2 gl, float... value) {
		gl.glUniform4fv(location(), 1, value, 0);
	}

	public void putColor(GL2ES2 gl, Color color) {
		put(gl, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
	}
}
