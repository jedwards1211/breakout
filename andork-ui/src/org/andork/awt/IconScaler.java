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
package org.andork.awt;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class IconScaler {
	public static ImageIcon rescale(Icon icon, int maxWidth, int maxHeight) {
		if (icon == null) {
			return null;
		}

		BufferedImage b = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		icon.paintIcon(null, b.getGraphics(), 0, 0);

		double aspect = (double) icon.getIconWidth() / icon.getIconHeight();
		double newAspect = (double) maxWidth / maxHeight;

		int newWidth, newHeight;

		if (newAspect > aspect) {
			newHeight = maxHeight;
			newWidth = icon.getIconWidth() * maxHeight / icon.getIconHeight();
		} else {
			newWidth = maxWidth;
			newHeight = icon.getIconHeight() * maxWidth / icon.getIconWidth();
		}

		Image scaled = b.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}
}
