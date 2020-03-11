package org.breakout.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglDrawable;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.util.awt.TextRenderer;

public class TitleText implements JoglDrawable {
	public static interface Context {
		public String text();

		public Font font();

		public Color color();

		public float yOffsetRatio();
	}

	final Context context;
	static final FontRenderContext frc = new FontRenderContext(new AffineTransform(), false, false);

	public TitleText(Context context) {
		this.context = context;
	}

	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		String text = this.context.text();
		if (text == null)
			return;
		Font font = this.context.font();

		float scale = context.devicePixelRatio() / 2;

		TextRenderer textRenderer = TextRenderers.textRenderer(font);
		textRenderer.setColor(this.context.color());

		gl.glDisable(GL.GL_DEPTH_TEST);

		float x = context.width() / 2;
		float y = context.height() - font.getSize() * scale * 1.5f;
		y -= this.context.yOffsetRatio() * context.height();

		x -= font.getStringBounds(text, frc).getWidth() * scale / 2;

		textRenderer.beginRendering(context.width(), context.height(), false);
		textRenderer.draw3D(text, x, y, 0, scale);
		textRenderer.endRendering();
	}
}
