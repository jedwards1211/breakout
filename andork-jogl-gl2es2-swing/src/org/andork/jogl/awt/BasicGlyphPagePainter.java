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

import java.awt.Graphics2D;

public class BasicGlyphPagePainter implements GlyphPagePainter {
	@Override
	public void drawGlyphs(Graphics2D g, char startChar, int rows, int cols, int cellHeight, int cellWidth,
			int cellBaseline) {
		char[] chars = { startChar };
		int y = cellBaseline;
		for (int row = 0; row < rows; row++, y += cellHeight) {
			int x = 0;
			for (int col = 0; col < cols; col++, x += cellWidth, chars[0]++) {
				g.drawChars(chars, 0, 1, x, y);
			}
		}
	}
}
