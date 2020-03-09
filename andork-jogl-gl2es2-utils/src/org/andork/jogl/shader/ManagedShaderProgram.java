package org.andork.jogl.shader;

import org.andork.jogl.JoglManagedResource;

import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public abstract class ManagedShaderProgram extends JoglManagedResource {
	private ShaderProgram program;
	private GLLocation[] locations;

	public ManagedShaderProgram() {
	}

	@Override
	protected void doDispose(GL2ES2 gl) {
		program.destroy(gl);
	}

	protected void setLocations(GLLocation... locations) {
		this.locations = locations;
	}

	protected abstract Iterable<ShaderCode> initShaders(GL2ES2 gl);

	@Override
	protected boolean doInit(GL2ES2 gl) {
		try {
			program = new ShaderProgram();

			for (ShaderCode shader : initShaders(gl)) {
				program.add(gl, shader, System.err);
			}

			boolean result = program.init(gl);
			program.link(gl, System.err);
			program.validateProgram(gl, System.err);

			for (GLLocation location : locations) {
				location.update(gl, program.program());
				if (location.location() < 0) {
					throw new IllegalStateException("Location not found: " + location.name());
				}
			}

			return result;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			doDispose(gl);
			return false;
		}
	}

	public void use(GL2ES2 gl) {
		use(gl, true);
	}

	public void done(GL2ES2 gl) {
		use(gl, false);
	}

	public void use(GL2ES2 gl, boolean on) {
		if (program == null)
			return;
		program.useProgram(gl, on);
	}
}
