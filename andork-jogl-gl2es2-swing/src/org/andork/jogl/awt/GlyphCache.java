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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.WeakHashMap;

import org.andork.jogl.JoglResourceManager;

public class GlyphCache {
	JoglResourceManager manager;
	Font font;
	public final FontMetrics fontMetrics;

	BufferedImageFactory imageFactory;
	GlyphPagePainter painter;

	int cellWidth;
	int cellHeight;
	int cellBaseline;
	int rowsPerPage;
	int colsPerPage;

	int pageWidth;
	int pageHeight;
	int charsPerPage;

	final WeakHashMap<Integer, GlyphPage> pageCache = new WeakHashMap<Integer, GlyphPage>();

	public GlyphCache(JoglResourceManager manager, Font f, int pageWidth, int pageHeight,
			BufferedImageFactory imageFactory, GlyphPagePainter painter) {
		this.manager = manager;
		font = f;
		this.imageFactory = imageFactory;
		this.painter = painter;
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;

		BufferedImage image = imageFactory.newImage(1, 1);
		Graphics2D g2 = image.createGraphics();

		fontMetrics = g2.getFontMetrics(f);
		cellWidth = fontMetrics.getMaxAdvance();
		cellHeight = fontMetrics.getMaxDescent() + fontMetrics.getMaxAscent();
		cellBaseline = fontMetrics.getMaxAscent();

		g2.dispose();

		colsPerPage = pageWidth / cellWidth;
		rowsPerPage = pageHeight / cellHeight;
		charsPerPage = colsPerPage * rowsPerPage;
	}

	public GlyphPage createPage(int pageIndex) {
		int startChar = charsPerPage * pageIndex;
		if (startChar > Character.MAX_VALUE) {
			throw new IllegalArgumentException("pageIndex out of range: " + pageIndex);
		}

		BufferedImage image = imageFactory.newImage(pageWidth, pageHeight);
		return new GlyphPage(manager, fontMetrics, image, painter, (char) startChar);
	}

	public GlyphPage getPage(char c) {
		return getPage(pageIndex(c));
	}

	public GlyphPage getPage(int pageIndex) {
		GlyphPage page = pageCache.get(pageIndex);
		if (page == null) {
			page = createPage(pageIndex);
			pageCache.put(pageIndex, page);
		}
		return page;
	}

	public int pageIndex(char c) {
		return c / charsPerPage;
	}
}
