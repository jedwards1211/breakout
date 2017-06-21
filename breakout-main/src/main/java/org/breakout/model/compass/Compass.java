package org.breakout.model.compass;

import java.nio.ByteBuffer;

import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglDrawable;
import org.andork.jogl.JoglManagedResource;
import org.andork.jogl.util.StaticRenderer;
import org.andork.math3d.Vecmath;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;

public class Compass extends JoglManagedResource implements JoglDrawable {
	CompassProgram program;
	ByteBuffer vertices;
	StaticRenderer renderer;
	float[] projectionMatrix = Vecmath.newMat4f();
	float[] viewMatrix = Vecmath.newMat4f();
	float[] modelMatrix = Vecmath.newMat4f();
	float[] normalMatrix = Vecmath.newMat3f();
	
	public Compass() {
		viewMatrix[12] = 0;
		viewMatrix[13] = 0;
		viewMatrix[14] = -2;
		
		modelMatrix[5] = 0.3f;
		Vecmath.invAffineToTranspose3x3(modelMatrix, normalMatrix);
	}

	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		boolean cullFaceWasEnabled = gl.glIsEnabled(GL.GL_CULL_FACE);
		gl.glDisable(GL.GL_CULL_FACE);
		
		boolean depthTestWasEnabled = gl.glIsEnabled(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_DEPTH_TEST);
		
		int size = Math.max(context.width(), context.height()) / 8;
		gl.glViewport(context.width() - size, 0, size, size);
		context.projection().calculate(projectionMatrix, context, size, size);

		Vecmath.mcopyAffine(context.viewMatrix(), viewMatrix);
		program.use(gl, true);
		program.putMatrices(gl, projectionMatrix, viewMatrix, modelMatrix, normalMatrix);
		program.putAmbient(gl, 0.5f);
		renderer.setVertexAttribLocations(program.positionLocation(), program.normalLocation());
		renderer.draw();
	
		program.use(gl, false);
		gl.glViewport(0, 0, context.width(), context.height());
		
		if (cullFaceWasEnabled) {
			gl.glEnable(GL.GL_CULL_FACE);
		}
		if (!depthTestWasEnabled) {
			gl.glDisable(GL.GL_DEPTH_TEST);
		}
	}

	@Override
	protected boolean doInit(GL2ES2 gl) {
		program = CompassProgram.INSTANCE;
		program.init(gl);

		vertices = Buffers.newDirectByteBuffer(24 * 3 * 8);
		for (float x = -1; x <= 1; x += 2) {
			for (float y = -1; y <= 1; y += 2) {
				for (float z = -1; z <= 1; z += 2) {
					if (x * y * z > 0) {
						vertices.putFloat(x);
						vertices.putFloat(0);
						vertices.putFloat(0);
						vertices.putFloat(x);
						vertices.putFloat(y);
						vertices.putFloat(z);
						vertices.putFloat(0);
						vertices.putFloat(y);
						vertices.putFloat(0);
						vertices.putFloat(x);
						vertices.putFloat(y);
						vertices.putFloat(z);
						vertices.putFloat(0);
						vertices.putFloat(0);
						vertices.putFloat(z);
						vertices.putFloat(x);
						vertices.putFloat(y);
						vertices.putFloat(z);
					} else {
						vertices.putFloat(0);
						vertices.putFloat(0);
						vertices.putFloat(z);
						vertices.putFloat(x);
						vertices.putFloat(y);
						vertices.putFloat(z);
						vertices.putFloat(0);
						vertices.putFloat(y);
						vertices.putFloat(0);
						vertices.putFloat(x);
						vertices.putFloat(y);
						vertices.putFloat(z);
						vertices.putFloat(x);
						vertices.putFloat(0);
						vertices.putFloat(0);
						vertices.putFloat(x);
						vertices.putFloat(y);
						vertices.putFloat(z);
					}
				}
			}
		}
		vertices.rewind();
		StaticRenderer.Options options = new StaticRenderer.Options(true, GL.GL_TRIANGLES);
		options.addAttribute(3, GL.GL_FLOAT, false);
		options.addAttribute(3, GL.GL_FLOAT, true);
		renderer = new StaticRenderer(vertices, options);
		renderer.init(gl);
		return true;
	}

	@Override
	protected void doDispose(GL2ES2 gl) {
		program.dispose(gl);
		program = null;
		vertices = null;
		renderer.dispose(gl);
		renderer = null;
	}

}
