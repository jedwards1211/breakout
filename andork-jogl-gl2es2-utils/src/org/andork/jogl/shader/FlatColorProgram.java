package org.andork.jogl.shader;

import org.andork.jogl.JoglManagedResource;
import org.andork.jogl.uniform.UniformMatrix4fv;

import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public class FlatColorProgram extends JoglManagedResource {
	private ShaderProgram program;
	private int modelMatrixLocation = 0;
	private int viewMatrixLocation = 0;
	private int projectionMatrixLocation = 0;
	private int positionLocation = 0;
	private int colorLocation = 0;
	
	public static final FlatColorProgram INSTANCE = new FlatColorProgram();
	
	private FlatColorProgram() {
		
	}

	@Override
	public void doDispose(GL2ES2 gl) {
		program.destroy(gl);
		modelMatrixLocation = 0;
		viewMatrixLocation = 0;
		projectionMatrixLocation = 0;
		positionLocation = 0;
		colorLocation = 0;
	}

	@Override
	public boolean doInit(GL2ES2 gl) {
		try {
			program = new ShaderProgram();

			ShaderCode vertexShader = ShaderCode.create(gl, GL2ES2.GL_VERTEX_SHADER, this.getClass(),
					".", null, "FlatColorVertex", false);
			ShaderCode fragmentShader = ShaderCode.create(gl, GL2ES2.GL_FRAGMENT_SHADER, this.getClass(),
					".", null, "FlatColorFragment", false);
			program.add(gl, vertexShader, System.err);
			program.add(gl, fragmentShader, System.err);

			boolean result = program.init(gl);
			program.link(gl, System.err);
			program.validateProgram(gl, System.err);

			modelMatrixLocation = gl.glGetUniformLocation(program.program(), "m");
			viewMatrixLocation = gl.glGetUniformLocation(program.program(), "v");
			projectionMatrixLocation = gl.glGetUniformLocation(program.program(), "p");
			colorLocation = gl.glGetUniformLocation(program.program(), "color");
			positionLocation = gl.glGetAttribLocation(program.program(), "position");

			return result;
		} catch (Exception ex) {
			doDispose(gl);
			return false;
		}
	}

	public int colorLocation() {
		return colorLocation;
	}

	public int positionLocation() {
		return positionLocation;
	}

	public int modelMatrixLocation() {
		return modelMatrixLocation;
	}

	public int viewMatrixLocation() {
		return viewMatrixLocation;
	}

	public int projectionMatrixLocation() {
		return projectionMatrixLocation;
	}

	public void putMatrices(GL2ES2 gl, float[] projection, float[] view, float[] model) {
		gl.glUniformMatrix4fv(projectionMatrixLocation, 1, false, projection, 0);
		gl.glUniformMatrix4fv(viewMatrixLocation, 1, false, view, 0);
		gl.glUniformMatrix4fv(modelMatrixLocation, 1, false, model, 0);
	}

	public void putMatrices(GL2ES2 gl, UniformMatrix4fv projection, UniformMatrix4fv view, UniformMatrix4fv model) {
		projection.put(gl, projectionMatrixLocation);
		view.put(gl, viewMatrixLocation);
		model.put(gl, modelMatrixLocation);
	}

	public void putColor(GL2ES2 gl, float r, float g, float b) {
		putColor(gl, new float[] { r, g, b, 1 });
	}

	public void putColor(GL2ES2 gl, float... color) {
		gl.glUniform4fv(colorLocation, 1, color, 0);
	}

	public void use(GL2ES2 gl, boolean on) {
		if (program == null)
			return;
		program.useProgram(gl, on);
	}
}
