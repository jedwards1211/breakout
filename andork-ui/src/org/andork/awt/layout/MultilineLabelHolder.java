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
package org.andork.awt.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

@SuppressWarnings("serial")
public class MultilineLabelHolder extends JPanel {
	private class Layout implements LayoutManager {

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void layoutContainer(Container parent) {
			label.setBounds(RectangleUtils.insetCopy(new Rectangle(parent.getSize()), parent.getInsets()));
		}

		private Dimension layoutSize(Container parent, LayoutSize sizeType) {
			Dimension size = sizeType.get(label);
			if (size.width <= width) {
				return size;
			}
			View view = (View) label.getClientProperty(BasicHTML.propertyKey);
			if (view != null) {
				view.setSize(width, 0);
				size.height = (int) Math.ceil(view.getPreferredSpan(View.Y_AXIS));
			}
			return size;
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return layoutSize(parent, LayoutSize.MINIMUM);
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			return layoutSize(parent, LayoutSize.PREFERRED);
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 5628966473659441815L;

	private static String wrapText(String text) {
		if (!text.trim().toLowerCase().startsWith("<html>")) {
			text = "<html>" + text + "</html>";
		}
		return text;
	}

	JLabel label;

	int width;

	public MultilineLabelHolder(JLabel label) {
		super();
		this.label = label;
		add(label);
		setLayout(new Layout());
	}

	public MultilineLabelHolder(String text) {
		this(new JLabel(wrapText(text)));
	}

	@Override
	public int getWidth() {
		return width;
	}

	public MultilineLabelHolder setWidth(int width) {
		this.width = width;
		return this;
	}
}
