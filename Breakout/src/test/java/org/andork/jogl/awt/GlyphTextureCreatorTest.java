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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import com.andork.plot.ImagePlotPanel;
import com.jogamp.nativewindow.awt.DirectDataBufferInt;

public class GlyphTextureCreatorTest {
	public static void main(String[] args) {
		// BufferedImage image = new BufferedImage( 512 , 512 ,
		// BufferedImage.TYPE_BYTE_GRAY );
		// BufferedImage image = DirectDataBufferByte.createBufferedImage( 512 ,
		// 512 , BufferedImage.TYPE_BYTE_GRAY , new Point( ) , new
		// Hashtable<Object,
		// Object>( ) );
		BufferedImage image = DirectDataBufferInt.createBufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB,
				new Point(), new Hashtable<Object, Object>());
		Graphics2D g2 = image.createGraphics();
		FontMetrics fm = g2.getFontMetrics(new Font("Arial", Font.PLAIN, 24));
		GlyphPage page = new GlyphPage(null, fm, image, new OutlinedGlyphPagePainter(
				new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND),
				Color.RED, Color.WHITE), (char) 0);

		ImagePlotPanel.showImageViewer(image);
	}
}
