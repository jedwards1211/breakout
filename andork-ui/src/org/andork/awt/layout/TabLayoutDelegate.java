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
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

import org.andork.awt.layout.DelegatingLayoutManager.LayoutDelegate;

public class TabLayoutDelegate implements LayoutDelegate {
	Component content;

	Corner corner;
	Side side;
	Insets insets;

	public TabLayoutDelegate(Component content, Corner corner, Side side) {
		super();
		this.content = content;
		this.corner = corner;
		this.side = side;
	}

	public TabLayoutDelegate corner(Corner corner) {
		this.corner = corner;
		return this;
	}

	@Override
	public Rectangle desiredBounds(Container parent, Component target, LayoutSize layoutSize) {
		Rectangle bounds = new Rectangle(layoutSize.get(target));
		Rectangle desiredContentBounds = content.getBounds();
		Container contentParent = content.getParent();
		if (contentParent != null) {
			desiredContentBounds = SwingUtilities.convertRectangle(contentParent, desiredContentBounds, parent);
		}

		if (corner != null) {
			if (side != null) {
				side.opposite().setLocation(bounds, side.location(desiredContentBounds));
				Side otherSide = side == corner.xSide() ? corner.ySide() : corner.xSide();
				otherSide.setLocation(bounds, otherSide.location(desiredContentBounds));
			} else {
				corner.xSide().opposite().setLocation(bounds, corner.xSide().location(desiredContentBounds));
				corner.ySide().opposite().setLocation(bounds, corner.ySide().location(desiredContentBounds));
			}
		} else {
			side.opposite().setLocation(bounds, side.location(desiredContentBounds));
			Axis invAxis = side.axis().opposite();
			invAxis.setLower(bounds, invAxis.center(desiredContentBounds) - invAxis.size(target) / 2);
		}

		if (insets != null) {
			RectangleUtils.inset(bounds, insets, bounds);
		}

		return bounds;
	}

	@Override
	public List<Component> getDependencies() {
		return content == null ? Collections.<Component> emptyList() : Collections.singletonList(content);
	}

	public TabLayoutDelegate insets(Insets insets) {
		this.insets = insets;
		return this;
	}

	public TabLayoutDelegate insets(int top, int left, int bottom, int right) {
		return insets(new Insets(top, left, bottom, right));
	}

	@Override
	public void layoutComponent(Container parent, Component target) {
		target.setBounds(desiredBounds(parent, target, LayoutSize.PREFERRED));
	}

	public TabLayoutDelegate side(Side side) {
		this.side = side;
		return this;
	}
}
