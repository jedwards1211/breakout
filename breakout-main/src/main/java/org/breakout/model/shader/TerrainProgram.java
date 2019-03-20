package org.breakout.model.shader;

import java.util.Arrays;

import org.andork.jogl.shader.AttribLocation;
import org.andork.jogl.shader.ManagedShaderProgram;
import org.andork.jogl.shader.Uniform1fvLocation;
import org.andork.jogl.shader.Uniform1ivLocation;
import org.andork.jogl.shader.Uniform2fvLocation;
import org.andork.jogl.shader.Uniform3fvLocation;
import org.andork.jogl.shader.UniformMatrix3fvLocation;
import org.andork.jogl.shader.UniformMatrix4fvLocation;
import org.andork.math3d.Clip3f;

import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.util.glsl.ShaderCode;

public class TerrainProgram extends ManagedShaderProgram {
	public final UniformMatrix4fvLocation modelMatrix = new UniformMatrix4fvLocation("m");
	public final UniformMatrix4fvLocation viewMatrix = new UniformMatrix4fvLocation("v");
	public final UniformMatrix4fvLocation projectionMatrix = new UniformMatrix4fvLocation("p");
	public final UniformMatrix3fvLocation inverseNormalMatrix = new UniformMatrix3fvLocation("n");
	public final AttribLocation position = new AttribLocation("a_position");
	public final AttribLocation normal = new AttribLocation("a_normal");
	public final AttribLocation texcoord = new AttribLocation("a_texcoord");
	public final Uniform3fvLocation lightDirection = new Uniform3fvLocation("u_lightDirection");
	public final Uniform1fvLocation ambient = new Uniform1fvLocation("u_ambient");
	public final Uniform1fvLocation alpha = new Uniform1fvLocation("u_alpha");
	public final Uniform1ivLocation satelliteImagery = new Uniform1ivLocation("u_satelliteImagery");
	public final Uniform3fvLocation clipAxis = new Uniform3fvLocation("u_clipAxis");
	public final Uniform2fvLocation clipNearFar = new Uniform2fvLocation("u_clipNearFar");
	
	public static final TerrainProgram INSTANCE = new TerrainProgram();
	
	private TerrainProgram() {
		setLocations(
			modelMatrix,
			viewMatrix,
			projectionMatrix,
			inverseNormalMatrix,
			position,
			normal,
			texcoord,
			lightDirection,
			ambient,
			alpha,
			satelliteImagery,
			clipAxis,
			clipNearFar
		);
	}

	@Override
	protected Iterable<ShaderCode> initShaders(GL2ES2 gl) {
		ShaderCode vertexShader = ShaderCode.create(gl, GL2ES2.GL_VERTEX_SHADER, this.getClass(),
				".", null, "terrain", false);
		ShaderCode fragmentShader = ShaderCode.create(gl, GL2ES2.GL_FRAGMENT_SHADER, this.getClass(),
				".", null, "terrain", false);
		return Arrays.asList(vertexShader, fragmentShader);
	}

	public void putMatrices(GL2ES2 gl, float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix, float[] n) {
		this.projectionMatrix.put(gl, projectionMatrix);
		this.viewMatrix.put(gl, viewMatrix);
		this.modelMatrix.put(gl, modelMatrix);
		this.inverseNormalMatrix.put(gl, n);
	}
	
	public void putClip(GL2ES2 gl, Clip3f clip) {
		clipAxis.put(gl, clip.axis());
		clipNearFar.put(gl, clip.near(), clip.far());
	}
}
