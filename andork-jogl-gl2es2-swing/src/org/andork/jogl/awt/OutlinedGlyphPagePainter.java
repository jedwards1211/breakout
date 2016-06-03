/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.jogl.awt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

public class OutlinedGlyphPagePainter implements GlyphPagePainter {
	Stroke outlineStroke;
	Color outlineColor;
	Color fillColor;

	public OutlinedGlyphPagePainter(Stroke outlineStroke, Color outlineColor, Color fillColor) {
		super();
		this.outlineStroke = outlineStroke;
		this.outlineColor = outlineColor;
		this.fillColor = fillColor;
	}

	@Override
	public void drawGlyphs(Graphics2D g, char startChar, int rows, int cols, int cellHeight, int cellWidth,
			int cellBaseline) {
		AffineTransform prevXform = g.getTransform();
		Stroke prevStroke = g.getStroke();
		Paint prevPaint = g.getPaint();

		char[] chars = { startChar };
		g.translate(0, cellBaseline);
		for (int row = 0; row < rows; row++, g.translate(-cellWidth * cols, cellHeight)) {
			for (int col = 0; col < cols; col++, chars[0]++, g.translate(cellWidth, 0)) {
				g.setColor(outlineColor);
				g.setStroke(outlineStroke);
				Shape outline = g.getFont().createGlyphVector(g.getFontRenderContext(), chars).getOutline();
				g.draw(outline);
				g.setColor(fillColor);
				g.fill(outline);
			}
		}
		g.setTransform(prevXform);
		g.setStroke(prevStroke);
		g.setPaint(prevPaint);
	}
}
