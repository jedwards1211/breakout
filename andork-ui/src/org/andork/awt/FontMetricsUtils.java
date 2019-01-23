package org.andork.awt;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class FontMetricsUtils {

	private FontMetricsUtils() {
	}
	
	public static Rectangle2D getMultilineStringBounds(String s, Font font, FontRenderContext frc) {
		String[] lines = s.split("\r\n?|\n");
		Rectangle2D bounds = font.getStringBounds(lines[0], frc);
		for (int i = 1; i < lines.length; i++) {
			Rectangle2D nextBounds = font.getStringBounds(lines[i], frc);
			bounds.setFrame(
				bounds.getX(),
				bounds.getY(),
				Math.max(bounds.getWidth(), nextBounds.getWidth()),
				bounds.getHeight() + nextBounds.getHeight()
			);
		}
		return bounds;
	}

}
