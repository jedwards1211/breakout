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
	float[] color = { 1, 1, 1, 1 };
	PipelinedRenderer lineRenderer =
		new PipelinedRenderer(new PipelinedRenderer.Options(true, GL_LINES, 100).addAttribute(3, GL_FLOAT, false));
	TextRenderer textRenderer;
	Font labelFont;

	float labelSize = 12;

	float[] left = new float[3];
	float[] right = new float[3];
	float distance = 0;
	float[] up = new float[3];
	float[] p = new float[3];

	boolean imperial;
	boolean miles;
	String unit;

	NumberFormat numberFormat = NumberFormat.getInstance();

	FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);

	public void setColor(float... color) {
		System.arraycopy(color, 0, this.color, 0, 4);
	}

	public void setColor(Color stationLabelColor) {
		setColor(
			stationLabelColor.getRed() / 255f,
			stationLabelColor.getGreen() / 255f,
			stationLabelColor.getBlue() / 255f,
			stationLabelColor.getAlpha() / 255f);
	}

	public void setImperial(boolean imperial) {
		this.imperial = imperial;
	}

	public OrthoScaleBar(Font labelFont) {
		this.labelFont = labelFont;
	}

	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		if (!context.projection().isOrtho())
			return;

		FlatColorProgram program = FlatColorProgram.INSTANCE;
		program.use(gl);
		program.putMatrices(gl, context.projectionMatrix(), context.viewMatrix(), m);
		program.color.put(gl, color);
		lineRenderer.setVertexAttribLocations(program.position);

		float y = labelSize * 2;
		setf(right, context.width() * 0.96f, y, 0);
		setf(left, context.width() * 0.6f, y, 0);
		setf(up, 0, 10, 0);
		mpmul(context.screenToWorld(), left);
		mpmul(context.screenToWorld(), right);
		mvmul(context.screenToWorld(), up);
		distance = distance3(left, right);
		if (imperial) {
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

		textRenderer.beginRendering(context.width(), context.height(), false);

		textRenderer.setColor(color[0], color[1], color[2], color[3]);
		mpmul(context.worldToScreen(), left, p);
		p[1] -= labelSize * 1.5;
		drawValue(context, 0);
		drawValue(context, small);
		drawValue(context, medium);
		drawValue(context, large);

		textRenderer.end3DRendering();
	}

	void drawValue(JoglDrawContext context, float position) {
		interp3(right, left, position / distance, p);
		mpmul(context.worldToScreen(), p);
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
		textRenderer = new TextRenderer(labelFont, true, true, null, false);
		textRenderer.init();
		return true;
	}

	@Override
	protected void doDispose(GL2ES2 gl) {
		FlatColorProgram program = FlatColorProgram.INSTANCE;
		program.dispose(gl);
		lineRenderer.dispose(gl);
		textRenderer.dispose();
		textRenderer = null;
	}
}
