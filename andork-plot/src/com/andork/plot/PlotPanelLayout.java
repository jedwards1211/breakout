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
package com.andork.plot;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.andork.awt.layout.Corner;
import org.andork.awt.layout.RectangleUtils;

import com.andork.plot.PlotAxis.Orientation;

public class PlotPanelLayout implements LayoutManager2 {
	private static enum SizeType {
		MINIMUM, PREFERRED, MAXIMUM;
	}

	private final Map<Component, Object> constraints = new HashMap<Component, Object>();

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		this.constraints.put(comp, constraints);
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {

	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	private Dimension getSize(Component comp, SizeType sizeType) {
		switch (sizeType) {
		case MAXIMUM:
			return comp.getMaximumSize();
		case PREFERRED:
			return comp.getPreferredSize();
		case MINIMUM:
			return comp.getMinimumSize();
		default:
			throw new IllegalArgumentException("invalid sizeType: " + sizeType);
		}
	}

	@Override
	public void invalidateLayout(Container target) {

	}

	@Override
	public void layoutContainer(Container parent) {
		Insets insets = parent.getInsets();
		Insets plotInsets = new Insets(0, 0, 0, 0);

		Dimension prefSize = preferredLayoutSize(parent);
		Dimension actualSize = parent.getSize();

		SizeType hSizeType = actualSize.width >= prefSize.width ? SizeType.PREFERRED : SizeType.MINIMUM;
		SizeType vSizeType = actualSize.height >= prefSize.height ? SizeType.PREFERRED : SizeType.MINIMUM;

		for (Component comp : parent.getComponents()) {
			if (!comp.isVisible()) {
				continue;
			}
			if (comp instanceof PlotAxis) {
				PlotAxis axis = (PlotAxis) comp;
				switch (axis.getLabelPosition()) {
				case TOP:
					plotInsets.top += getSize(axis, vSizeType).height;
					break;
				case LEFT:
					plotInsets.left += getSize(axis, hSizeType).width;
					break;
				case BOTTOM:
					plotInsets.bottom += getSize(axis, vSizeType).height;
					break;
				case RIGHT:
					plotInsets.right += getSize(axis, hSizeType).width;
					break;
				}
			}
		}

		Rectangle insetBounds = RectangleUtils.insetCopy(SwingUtilities.getLocalBounds(parent), insets);
		Rectangle plotBounds = RectangleUtils.insetCopy(insetBounds, plotInsets);

		Insets axisInsets = (Insets) insets.clone();

		int plotLeft = insets.left + plotInsets.left;
		int plotTop = insets.top + plotInsets.top;
		int plotWidth = parent.getWidth() - insets.left - plotInsets.left - insets.right - plotInsets.right;
		int plotHeight = parent.getHeight() - insets.top - plotInsets.top - insets.bottom - plotInsets.bottom;

		for (Component comp : parent.getComponents()) {
			if (comp instanceof PlotAxis) {
				PlotAxis axis = (PlotAxis) comp;
				Dimension size;
				switch (axis.getLabelPosition()) {
				case TOP:
					size = getSize(axis, vSizeType);
					axis.setBounds(plotLeft, axisInsets.top, plotWidth, size.height);
					axisInsets.top += size.height;
					break;
				case LEFT:
					size = getSize(axis, hSizeType);
					axis.setBounds(axisInsets.left, plotTop, size.width, plotHeight);
					axisInsets.bottom += size.height;
					break;
				case BOTTOM:
					size = getSize(axis, vSizeType);
					axis.setBounds(plotLeft, parent.getHeight() - axisInsets.bottom - size.height, plotWidth,
							size.height);
					axisInsets.bottom += size.height;
					break;
				case RIGHT:
					size = getSize(axis, hSizeType);
					axis.setBounds(parent.getWidth() - axisInsets.right - size.width, plotTop, size.width, plotHeight);
					axisInsets.right += size.width;
					break;
				}
			} else {
				Object constraints = this.constraints.get(comp);
				if (constraints != null && constraints instanceof Corner) {
					Corner corner = (Corner) constraints;
					Rectangle bounds = new Rectangle();
					corner.setLocation(bounds, corner.location(insetBounds));
					corner.opposite().stretch(bounds, corner.location(plotBounds));
					comp.setBounds(bounds);
				} else {
					comp.setBounds(plotLeft, plotTop, plotWidth, plotHeight);
				}
			}
		}
	}

	private Dimension layoutSize(Container parent, SizeType sizeType) {
		Dimension plotSize = new Dimension();
		Dimension axisSize = new Dimension();

		for (Component comp : parent.getComponents()) {
			if (!comp.isVisible()) {
				continue;
			}
			if (comp instanceof Plot) {
				Dimension size = getSize(comp, sizeType);
				plotSize.width = Math.max(plotSize.width, size.width);
				plotSize.height = Math.max(plotSize.height, size.height);
			} else if (comp instanceof PlotAxis) {
				PlotAxis axis = (PlotAxis) comp;
				Dimension size = getSize(comp, sizeType);
				if (axis.getOrientation() == Orientation.HORIZONTAL) {
					axisSize.height += size.height;
					axisSize.width = Math.max(axisSize.width, size.width);
				} else {
					axisSize.width += size.width;
					axisSize.height = Math.max(axisSize.height, size.height);
				}
			}
		}

		Insets insets = parent.getInsets();

		return new Dimension(plotSize.width + axisSize.width + insets.left + insets.right,
				plotSize.height + axisSize.height + insets.top + insets.bottom);
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return layoutSize(parent, SizeType.MINIMUM);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return layoutSize(parent, SizeType.PREFERRED);
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		constraints.remove(comp);
	}
}
