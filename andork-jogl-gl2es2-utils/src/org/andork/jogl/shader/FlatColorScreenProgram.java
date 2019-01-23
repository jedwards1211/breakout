package org.andork.jogl.shader;

import java.util.Arrays;

import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.util.glsl.ShaderCode;

public class FlatColorScreenProgram extends ManagedShaderProgram {
	public final UniformMatrix4fvLocation screenXform = new UniformMatrix4fvLocation("screenXform");
	public final AttribLocation position = new AttribLocation("position");
	public final Uniform4fvLocation color = new Uniform4fvLocation("color");
	
	private FlatColorScreenProgram() {
		setLocations(screenXform, position, color);
	}
	
	public static final FlatColorScreenProgram INSTANCE = new FlatColorScreenProgram();
	
	protected Iterable<ShaderCode> initShaders(GL2ES2 gl) {
		ShaderCode vertexShader = ShaderCode.create(gl, GL2ES2.GL_VERTEX_SHADER, this.getClass(),
				".", null, "FlatColorVertexScreen", false);
		ShaderCode fragmentShader = ShaderCode.create(gl, GL2ES2.GL_FRAGMENT_SHADER, this.getClass(),
				".", null, "FlatColorFragment", false);
		return Arrays.asList(vertexShader, fragmentShader);
	}
}
