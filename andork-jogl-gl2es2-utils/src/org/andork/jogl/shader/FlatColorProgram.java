package org.andork.jogl.shader;

import java.util.Arrays;

import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.util.glsl.ShaderCode;

public class FlatColorProgram extends ManagedShaderProgram {
	public final UniformMatrix4fvLocation modelMatrix = new UniformMatrix4fvLocation("m");
	public final UniformMatrix4fvLocation viewMatrix = new UniformMatrix4fvLocation("v");
	public final UniformMatrix4fvLocation projectionMatrix = new UniformMatrix4fvLocation("p");
	public final AttribLocation position = new AttribLocation("position");
	public final Uniform4fvLocation color = new Uniform4fvLocation("color");
	
	public static final FlatColorProgram INSTANCE = new FlatColorProgram();
	
	private FlatColorProgram() {
		setLocations(
			modelMatrix,
			viewMatrix,
			projectionMatrix,
			position,
			color
		);
	}

	@Override
	protected Iterable<ShaderCode> initShaders(GL2ES2 gl) {
		ShaderCode vertexShader = ShaderCode.create(gl, GL2ES2.GL_VERTEX_SHADER, this.getClass(),
				".", null, "FlatColorVertex", false);
		ShaderCode fragmentShader = ShaderCode.create(gl, GL2ES2.GL_FRAGMENT_SHADER, this.getClass(),
				".", null, "FlatColorFragment", false);
		return Arrays.asList(vertexShader, fragmentShader);
	}

	public void putMatrices(GL2ES2 gl, float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix) {
		this.projectionMatrix.put(gl, projectionMatrix);
		this.viewMatrix.put(gl, viewMatrix);
		this.modelMatrix.put(gl, modelMatrix);
	}
}
