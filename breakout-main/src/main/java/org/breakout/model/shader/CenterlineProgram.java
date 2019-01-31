package org.breakout.model.shader;

import java.util.Arrays;

import org.andork.jogl.shader.AttribLocation;
import org.andork.jogl.shader.ManagedShaderProgram;
import org.andork.jogl.shader.Uniform1fvLocation;
import org.andork.jogl.shader.Uniform4fvLocation;
import org.andork.jogl.shader.UniformMatrix4fvLocation;

import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.util.glsl.ShaderCode;

public class CenterlineProgram extends ManagedShaderProgram {
	public final UniformMatrix4fvLocation modelMatrix = new UniformMatrix4fvLocation("m");
	public final UniformMatrix4fvLocation viewMatrix = new UniformMatrix4fvLocation("v");
	public final UniformMatrix4fvLocation projectionMatrix = new UniformMatrix4fvLocation("p");
	public final AttribLocation position = new AttribLocation("a_pos");
	public final Uniform4fvLocation color = new Uniform4fvLocation("u_color");
	public final Uniform1fvLocation maxCenterlineDistance = new Uniform1fvLocation("u_maxCenterlineDistance");

	public static final CenterlineProgram INSTANCE = new CenterlineProgram();
	
	private CenterlineProgram() {
		setLocations(
			modelMatrix,
			viewMatrix,
			projectionMatrix,
			position,
			color,
			maxCenterlineDistance
		);
	}

	@Override
	protected Iterable<ShaderCode> initShaders(GL2ES2 gl) {
		ShaderCode vertexShader = ShaderCode.create(gl, GL2ES2.GL_VERTEX_SHADER, this.getClass(),
				".", null, "centerline", false);
		ShaderCode fragmentShader = ShaderCode.create(gl, GL2ES2.GL_FRAGMENT_SHADER, this.getClass(),
				".", null, "centerline", false);
		return Arrays.asList(vertexShader, fragmentShader);
	}

	public void putMatrices(GL2ES2 gl, float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix) {
		this.projectionMatrix.put(gl, projectionMatrix);
		this.viewMatrix.put(gl, viewMatrix);
		this.modelMatrix.put(gl, modelMatrix);
	}
}
