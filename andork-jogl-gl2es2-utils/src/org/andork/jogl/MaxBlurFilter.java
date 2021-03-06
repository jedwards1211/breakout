package org.andork.jogl;

import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TRIANGLES;

import org.andork.jogl.shader.MaxBlurProgram;
import org.andork.jogl.util.PipelinedRenderer;
import org.andork.jogl.util.PipelinedRenderer.Options;

import com.jogamp.opengl.GL3;

public class MaxBlurFilter implements JoglFilter {
	static final PipelinedRenderer triangleRenderer =
		new PipelinedRenderer(
			new Options(true, GL_TRIANGLES, 6).addAttribute(2, GL_FLOAT, false).addAttribute(2, GL_FLOAT, false));

	float[] offset;
	float[] mappedOffset;
	float[] coefficients;

	public MaxBlurFilter() {
		this(new float[] { 0, 0 }, new float[] { 1 });
	}

	public void linear(float radius, boolean vertical) {
		int ceil = (int) Math.ceil(radius);
		int floor = (int) Math.floor(radius);
		resize(ceil * 2 + 1);
		for (int i = 0; i < coefficients.length; i++) {
			offset[i * 2 + (vertical ? 1 : 0)] = i - ceil;
			coefficients[i] = 1;
		}
		if (radius > floor) {
			coefficients[0] = coefficients[ceil - 1] = radius - floor;
		}
	}

	void resize(int size) {
		if (size > 11) {
			throw new IllegalArgumentException("size must be <= 11");
		}
		if (coefficients.length != size) {
			coefficients = new float[size];
			offset = new float[size * 2];
			mappedOffset = new float[size * 2];
		}
	}

	public MaxBlurFilter(float[] offset, float[] coefficients) {
		super();
		if (coefficients.length > 11) {
			throw new IllegalArgumentException("coefficients.length must be <= 11");
		}
		if (offset.length != coefficients.length * 2) {
			throw new IllegalArgumentException("offset must be twice as long as coefficients");
		}
		this.offset = offset;
		this.mappedOffset = new float[offset.length];
		this.coefficients = coefficients;
	}

	@Override
	public void apply(GL3 gl, int width, int height, int framebuffer, int texture, float s, float t) {
		triangleRenderer.init(gl);
		MaxBlurProgram program = MaxBlurProgram.INSTANCE;
		program.init(gl);
		program.use(gl);
		program.count.put(gl, coefficients.length);
		for (int i = 0; i < offset.length; i += 2) {
			mappedOffset[i] = s * offset[i] / width;
			mappedOffset[i + 1] = t * offset[i + 1] / height;
		}
		program.offset.put(gl, coefficients.length, mappedOffset);
		program.coefficients.put(gl, coefficients.length, coefficients);
		triangleRenderer.setVertexAttribLocations(program.position, program.texcoord);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, texture);
		program.texture.put(gl, 0);

		gl.glViewport(0, 0, width, height);

		triangleRenderer.put(-1f, -1f, 0f, 0f);
		triangleRenderer.put(1f, -1f, s, 0f);
		triangleRenderer.put(-1f, 1f, 0f, t);
		triangleRenderer.put(1f, 1f, s, t);
		triangleRenderer.put(-1f, 1f, 0f, t);
		triangleRenderer.put(1f, -1f, s, 0f);

		triangleRenderer.draw();
		program.use(gl, false);
	}
}
