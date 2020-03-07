package org.andork.jogl.shader;

import java.util.Arrays;

import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.util.glsl.ShaderCode;

public class MaxBlurProgram extends ManagedShaderProgram {
	public final AttribLocation position = new AttribLocation("a_position");
	public final AttribLocation texcoord = new AttribLocation("a_texcoord");
	public final Uniform1ivLocation count = new Uniform1ivLocation("u_count");
	public final Uniform2fvLocation offset = new Uniform2fvLocation("u_offset");
	public final Uniform1fvLocation coefficients = new Uniform1fvLocation("u_coeff");
	public final Uniform1ivLocation texture = new Uniform1ivLocation("u_texture");

	public static final MaxBlurProgram INSTANCE = new MaxBlurProgram();

	private MaxBlurProgram() {
		setLocations(position, texcoord, count, offset, coefficients, texture);
	}

	@Override
	protected Iterable<ShaderCode> initShaders(GL2ES2 gl) {
		ShaderCode vertexShader =
			ShaderCode.create(gl, GL2ES2.GL_VERTEX_SHADER, this.getClass(), ".", null, "MaxBlur", false);
		ShaderCode fragmentShader =
			ShaderCode.create(gl, GL2ES2.GL_FRAGMENT_SHADER, this.getClass(), ".", null, "MaxBlur", false);
		return Arrays.asList(vertexShader, fragmentShader);
	}
}
