package org.breakout.model;

import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LINES;

import java.util.WeakHashMap;

import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.shader.FlatColorProgram;
import org.andork.jogl.util.PipelinedRenderer;
import org.andork.math3d.Vecmath;

import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLContext;

public class DebugDraw {
	static WeakHashMap<GLContext, Boolean> initialized = new WeakHashMap<>();
	static FlatColorProgram program = FlatColorProgram.INSTANCE;
	static PipelinedRenderer lineRenderer =
		new PipelinedRenderer(new PipelinedRenderer.Options(true, GL_LINES, 100).addAttribute(3, GL_FLOAT, false));

	static float[] modelMatrix = Vecmath.newMat4f();

	public static void beginLines(JoglDrawContext context, GL2ES2 gl, float... color) {
		init(gl);
		program.use(gl);
		program.putMatrices(gl, context.projectionMatrix(), context.viewMatrix(), modelMatrix);
		program.color.put(gl, color);
		lineRenderer.setVertexAttribLocations(program.position);
	}

	/**
	 * Draws one or more lines
	 * 
	 * @param lines an array of two or more xyz points
	 */
	public static void drawLines(float... lines) {
		lineRenderer.put(lines);
	}

	/**
	 * Draws a wireframe bounding box to {@link #lineRenderer}.
	 * 
	 * @param bbox a bounding box of the form [xmin, ymin, zmin, xmax, ymax, zmax]
	 */
	public static void drawBoundingBox(float[] bbox) {
		for (int x = 0; x < 6; x += 3) {
			lineRenderer.put(bbox[x], bbox[1], bbox[2]);
			lineRenderer.put(bbox[x], bbox[4], bbox[2]);
			lineRenderer.put(bbox[x], bbox[1], bbox[5]);
			lineRenderer.put(bbox[x], bbox[4], bbox[5]);
		}
		for (int y = 1; y < 6; y += 3) {
			lineRenderer.put(bbox[0], bbox[y], bbox[2]);
			lineRenderer.put(bbox[0], bbox[y], bbox[5]);
			lineRenderer.put(bbox[3], bbox[y], bbox[2]);
			lineRenderer.put(bbox[3], bbox[y], bbox[5]);
		}
		for (int z = 2; z < 6; z += 3) {
			lineRenderer.put(bbox[0], bbox[1], bbox[z]);
			lineRenderer.put(bbox[3], bbox[1], bbox[z]);
			lineRenderer.put(bbox[0], bbox[4], bbox[z]);
			lineRenderer.put(bbox[3], bbox[4], bbox[z]);
		}
	}

	/**
	 * Draws crosshairs at the given point.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param radius the radius of the crosshairs
	 */
	public static void drawPoint(float x, float y, float z, float radius) {
		lineRenderer.put(x - radius, y, z, x + radius, y, z);
		lineRenderer.put(x, y - radius, z, x, y + radius, z);
		lineRenderer.put(x, y, z - radius, x, y, z + radius);
	}

	public static void endLines(GL2ES2 gl) {
		lineRenderer.draw();
		program.done(gl);
	}

	public static void init(GL2ES2 gl) {
		if (initialized.containsKey(gl.getContext()))
			return;
		if (initialized.put(gl.getContext(), true) != null)
			return;
		program.init(gl);
		lineRenderer.init(gl);
	}
}
