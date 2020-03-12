package org.breakout.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

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

		/**
		 * @return x position - 0 is left, 1 is right
		 */
		public float relativeX();

		/**
		 * @return y position - 0 is bottom, 1 is top
		 */
		public float relativeY();

		/**
		 * @return additional x offset, in logical pixels.
		 */
		public default float xOffset() {
			return 0;
		}

		/**
		 * @return additional y offset, in logical pixels.
		 */
		public default float yOffset() {
			return 0;
		}
	}

	final Context context;
	static final FontRenderContext frc = new FontRenderContext(new AffineTransform(), false, false);

	public TitleText(Context context) {
		this.context = context;
	}

	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		String text = this.context.text();
		if (text == null || text.isEmpty())
			return;
		Font font = this.context.font();

		float scale = context.devicePixelRatio() / 2;

		TextRenderer textRenderer = TextRenderers.textRenderer(font);
		textRenderer.setColor(this.context.color());

		gl.glDisable(GL.GL_DEPTH_TEST);

		float x = context.width() * this.context.relativeX();
		float y = context.height() * this.context.relativeY();
		x += this.context.xOffset() * context.devicePixelRatio();
		y += this.context.yOffset() * context.devicePixelRatio();

		Rectangle2D bounds = font.getStringBounds(text, frc);

		x -= bounds.getWidth() * scale / 2;
		y -= bounds.getHeight() * scale / 2;

		textRenderer.beginRendering(context.width(), context.height(), false);
		textRenderer.draw3D(text, x, y, 0, scale);
		textRenderer.endRendering();
	}
}
