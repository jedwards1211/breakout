package org.andork.jogl;

import static org.andork.math3d.Vecmath.setf;

import java.util.Arrays;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;

public class JoglBackgroundColor implements JoglDrawable {

	private final float[] bgColor = { 0, 0, 0, 1 };

	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		gl.glClearColor(bgColor[0], bgColor[1], bgColor[2], bgColor[3]);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	}

	public float[] get() {
		return Arrays.copyOf(bgColor, bgColor.length);
	}

	public float[] get(float[] out) {
		setf(out, bgColor);
		return out;
	}

	public void set(float... bgColor) {
		setf(this.bgColor, bgColor);
	}

}
