package org.andork.jogl.shader;

import org.andork.jogl.JoglManagedResource;
import org.andork.jogl.uniform.UniformMatrix4fv;

import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public class FlatColorScreenProgram extends JoglManagedResource {
	private ShaderProgram program;
	private int screenXformLocation = 0;
	private int positionLocation = 0;
	private int colorLocation = 0;
	
	public static final FlatColorScreenProgram INSTANCE = new FlatColorScreenProgram();
	
	private FlatColorScreenProgram() {
		
	}

	@Override
	public void doDispose(GL2ES2 gl) {
		program.destroy(gl);
		screenXformLocation = 0;
		positionLocation = 0;
		colorLocation = 0;
	}

	@Override
	public boolean doInit(GL2ES2 gl) {
		try {
			program = new ShaderProgram();

			ShaderCode vertexShader = ShaderCode.create(gl, GL2ES2.GL_VERTEX_SHADER, this.getClass(),
					".", null, "FlatColorVertexScreen", false);
			ShaderCode fragmentShader = ShaderCode.create(gl, GL2ES2.GL_FRAGMENT_SHADER, this.getClass(),
					".", null, "FlatColorFragment", false);
			program.add(gl, vertexShader, System.err);
			program.add(gl, fragmentShader, System.err);

			boolean result = program.init(gl);
			program.link(gl, System.err);
			program.validateProgram(gl, System.err);

			screenXformLocation = gl.glGetUniformLocation(program.program(), "screenXform");
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

	public int screenXformLocation() {
		return screenXformLocation;
	}

	public void putMatrices(GL2ES2 gl, float[] screenXform) {
		gl.glUniformMatrix4fv(screenXformLocation, 1, false, screenXform, 0);
	}

	public void putMatrices(GL2ES2 gl, UniformMatrix4fv screenXform) {
		screenXform.put(gl, screenXformLocation);
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
