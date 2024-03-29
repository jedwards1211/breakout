package org.breakout.model.compass;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglDrawable;
import org.andork.jogl.JoglFilter;
import org.andork.jogl.JoglManagedResource;
import org.andork.jogl.JoglViewSettings;
import org.andork.jogl.JoglViewState;
import org.andork.jogl.util.StaticRenderer;
import org.andork.math3d.Vecmath;
import org.breakout.model.TextRenderers;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.awt.TextRenderer;

public class Compass extends JoglManagedResource implements JoglDrawable {
	CompassProgram program;
	ByteBuffer vertices;
	StaticRenderer renderer;
	float[] projectionMatrix = Vecmath.newMat4f();
	float[] viewMatrix = Vecmath.newMat4f();
	float[] modelMatrix = Vecmath.newMat4f();
	float[] labelModelMatrix = Vecmath.newMat4f();
	float[] pvmMatrix = Vecmath.newMat4f();
	float[] normalMatrix = Vecmath.newMat3f();
	float[] labelScreenPosition = new float[3];
	static final FontRenderContext fontRenderContext = new FontRenderContext(new AffineTransform(), true, true);

	static final Map<String, float[]> directions = new HashMap<>();
	static {
		directions.put("N", new float[] { 0, 0, -1 });
		directions.put("S", new float[] { 0, 0, 1 });
		directions.put("E", new float[] { 1, 0, 0 });
		directions.put("W", new float[] { -1, 0, 0 });
	}

	public static interface Context {
		public Font labelFont();

		public Color labelColor();

		public float yOffset();
	}

	final Context context;

	public Compass(Context context) {
		this.context = context;
		viewMatrix[12] = 0;
		viewMatrix[13] = 0;
		viewMatrix[14] = -2.5f;

		modelMatrix[5] = 0.3f;
		float labelFactor = 1.2f;
		labelModelMatrix[0] = modelMatrix[0] * labelFactor;
		labelModelMatrix[5] = modelMatrix[5] * labelFactor;
		labelModelMatrix[10] = modelMatrix[10] * labelFactor;
		Vecmath.invAffineToTranspose3x3(modelMatrix, normalMatrix);
	}

	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		boolean cullFaceWasEnabled = gl.glIsEnabled(GL.GL_CULL_FACE);
		if (!cullFaceWasEnabled)
			gl.glEnable(GL.GL_CULL_FACE);

		boolean depthTestWasEnabled = gl.glIsEnabled(GL.GL_DEPTH_TEST);
		if (!depthTestWasEnabled)
			gl.glEnable(GL.GL_DEPTH_TEST);

		JoglViewState newContext = new JoglViewState(new JoglViewState.Context() {
			@Override
			public void applyFilters(GL3 gl, JoglFilter... filters) {
				context.applyFilters(gl, filters);
			}
		});

		int size = Math.max(context.width(), context.height()) / 7;
		int x = context.width() - size;
		int y = (int) this.context.yOffset();
		gl.glViewport(x, y, size, size);
		JoglViewSettings settings = new JoglViewSettings();
		settings.copy(context.settings());
		settings.setProjection(context.projection().resize(5, 5, 0.1f, 100));
		settings.setViewXform(viewMatrix);
		newContext.update(settings, x, y, size, size);

		Vecmath.mcopyAffine(context.viewMatrix(), viewMatrix);
		program.use(gl, true);
		program.putMatrices(gl, newContext.projectionMatrix(), newContext.viewMatrix(), modelMatrix, normalMatrix);
		program.putAmbient(gl, 0.5f);
		renderer.setVertexAttribLocations(program.positionLocation(), program.normalLocation());
		renderer.draw();

		program.use(gl, false);
		gl.glViewport(0, 0, context.width(), context.height());

		boolean blendWasEnabled = gl.glIsEnabled(GL.GL_BLEND);
		if (!blendWasEnabled) {
			gl.glEnable(GL.GL_BLEND);
		}

		Font labelFont = this.context.labelFont();

		TextRenderer textRenderer = TextRenderers.textRenderer(labelFont);
		textRenderer.beginRendering(context.width(), context.height(), false);
		textRenderer.setColor(this.context.labelColor());

		for (Map.Entry<String, float[]> direction : directions.entrySet()) {
			String text = direction.getKey();
			float[] position = direction.getValue();

			Vecmath.mpmulAffine(labelModelMatrix, position, labelScreenPosition);
			Vecmath.mpmulAffine(newContext.viewMatrix(), labelScreenPosition);
			Vecmath.mpmul(newContext.viewToScreen(), labelScreenPosition);

			LineMetrics metrics = labelFont.getLineMetrics(text, fontRenderContext);
			Rectangle2D bounds = labelFont.getStringBounds(text, fontRenderContext);
			float scale = context.devicePixelRatio() / 2;
			labelScreenPosition[0] -= bounds.getWidth() / 2 * scale;
			labelScreenPosition[1] -= metrics.getAscent() / 2 * scale;
			textRenderer.draw3D(text, labelScreenPosition[0], labelScreenPosition[1], labelScreenPosition[2], scale);
		}

		textRenderer.endRendering();

		if (!blendWasEnabled) {
			gl.glDisable(GL.GL_BLEND);
		}
		if (!cullFaceWasEnabled) {
			gl.glDisable(GL.GL_CULL_FACE);
		}
		if (!depthTestWasEnabled) {
			gl.glDisable(GL.GL_DEPTH_TEST);
		}
	}

	@Override
	protected boolean doInit(GL2ES2 gl) {
		program = CompassProgram.INSTANCE;
		program.init(gl);

		Astroid3f astroid = new Astroid3f(1, 8);

		vertices = Buffers.newDirectByteBuffer(astroid.triangles.length * 24);
		for (int i = 0; i < astroid.triangles.length; i++) {
			float[] point = astroid.points[astroid.triangles[i]];
			float[] normal = astroid.normals[astroid.triangles[i]];
			vertices.putFloat(point[0]);
			vertices.putFloat(point[1]);
			vertices.putFloat(point[2]);
			vertices.putFloat(normal[0]);
			vertices.putFloat(normal[1]);
			vertices.putFloat(normal[2]);
		}
		vertices.rewind();

		StaticRenderer.Options options = new StaticRenderer.Options(true, GL.GL_TRIANGLES);
		options.addAttribute(3, GL.GL_FLOAT, false);
		options.addAttribute(3, GL.GL_FLOAT, false);
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
