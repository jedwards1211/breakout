package org.breakout.model;

import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LINES;
import static org.andork.math3d.Vecmath.add3;
import static org.andork.math3d.Vecmath.distance3;
import static org.andork.math3d.Vecmath.interp3;
import static org.andork.math3d.Vecmath.mpmul;
import static org.andork.math3d.Vecmath.mvmul;
import static org.andork.math3d.Vecmath.setf;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.text.NumberFormat;

import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglDrawable;
import org.andork.jogl.JoglManagedResource;
import org.andork.jogl.shader.FlatColorProgram;
import org.andork.jogl.util.PipelinedRenderer;

import com.andork.plot.GridMath;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.util.awt.TextRenderer;

public class OrthoScaleBar extends JoglManagedResource implements JoglDrawable {
	PipelinedRenderer lineRenderer =
		new PipelinedRenderer(new PipelinedRenderer.Options(true, GL_LINES, 100).addAttribute(3, GL_FLOAT, false));

	float[] left = new float[3];
	float[] right = new float[3];
	float distance = 0;
	float[] up = new float[3];
	float[] p = new float[3];

	boolean miles;
	String unit;

	final NumberFormat numberFormat = NumberFormat.getInstance();
	private static final FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);

	public static interface Context {
		Color color();

		float labelSize();

		Font labelFont();

		boolean imperial();
	}

	final Context context;

	public OrthoScaleBar(Context context) {
		this.context = context;
	}

	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		if (!context.projection().isOrtho())
			return;

		Font labelFont = this.context.labelFont();
		float labelSize = this.context.labelSize() * context.devicePixelRatio();

		FlatColorProgram program = FlatColorProgram.INSTANCE;
		program.use(gl);
		program.putMatrices(gl, context.projectionMatrix(), context.viewMatrix(), m);
		program.color.putColor(gl, this.context.color());
		lineRenderer.setVertexAttribLocations(program.position);

		float y = labelSize * 2;
		setf(right, context.width() * 0.96f, y, 0);
		setf(left, context.width() * 0.6f, y, 0);
		setf(up, 0, 10, 0);
		mpmul(context.screenToWorld(), left);
		mpmul(context.screenToWorld(), right);
		mvmul(context.screenToWorld(), up);
		distance = distance3(left, right);
		if (this.context.imperial()) {
			distance *= 3.28084;
			unit = "ft";
			if (distance >= 5280) {
				unit = "mi";
				distance /= 5280;
			}
		}
		else {
			unit = "m";
			if (distance >= 1000) {
				unit = "km";
				distance /= 1000;
			}
		}
		float large = (float) GridMath.niceFloor(distance, GridMath.HALF | GridMath.FIFTH);
		float medium = (float) GridMath.niceFloor(large * 0.9, GridMath.HALF | GridMath.FIFTH);
		float small = (float) GridMath.niceFloor(medium * 0.9, GridMath.HALF | GridMath.FIFTH);

		int fractionDigits = GridMath.niceFloorFractionDigits(medium * 0.9, GridMath.HALF | GridMath.FIFTH);
		numberFormat.setMinimumFractionDigits(fractionDigits);
		numberFormat.setMaximumFractionDigits(fractionDigits);

		lineRenderer.put(add3(right, up, p));
		lineRenderer.put(right);
		lineRenderer.put(right);
		lineRenderer.put(interp3(right, left, large / distance, p));
		lineRenderer.put(p);
		lineRenderer.put(add3(p, up, p));
		lineRenderer.put(interp3(right, left, medium / distance, p));
		lineRenderer.put(add3(p, up, p));
		lineRenderer.put(interp3(right, left, small / distance, p));
		lineRenderer.put(add3(p, up, p));
		lineRenderer.draw();

		program.done(gl);

		TextRenderer textRenderer = TextRenderers.textRenderer(labelFont);
		textRenderer.setColor(this.context.color());
		textRenderer.beginRendering(context.width(), context.height(), false);
		mpmul(context.worldToScreen(), left, p);
		p[1] -= labelSize * 1.5;
		drawValue(context, textRenderer, 0);
		drawValue(context, textRenderer, small);
		drawValue(context, textRenderer, medium);
		drawValue(context, textRenderer, large);

		textRenderer.end3DRendering();
	}

	void drawValue(JoglDrawContext context, TextRenderer textRenderer, float position) {
		interp3(right, left, position / distance, p);
		mpmul(context.worldToScreen(), p);
		Font labelFont = this.context.labelFont();
		float labelSize = this.context.labelSize() * context.devicePixelRatio();
		p[1] -= labelSize * 1.5;
		String text = numberFormat.format(position) + " " + unit;
		double width = labelFont.getStringBounds(text, frc).getWidth();
		float scale = labelSize / labelFont.getSize();
		p[0] -= width * scale * 0.5;
		textRenderer.draw3D(text, p[0], p[1], p[2], scale);
	}

	@Override
	protected boolean doInit(GL2ES2 gl) {
		FlatColorProgram program = FlatColorProgram.INSTANCE;
		program.init(gl);
		lineRenderer.init(gl);
		return true;
	}

	@Override
	protected void doDispose(GL2ES2 gl) {
		FlatColorProgram program = FlatColorProgram.INSTANCE;
		program.dispose(gl);
		lineRenderer.dispose(gl);
	}
}
