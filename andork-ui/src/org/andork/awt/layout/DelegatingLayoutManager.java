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
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DelegatingLayoutManager implements LayoutManager2 {
	public static interface LayoutDelegate {
		public Rectangle desiredBounds(Container parent, Component target, LayoutSize layoutSize);

		public List<Component> getDependencies();

		public void layoutComponent(Container parent, Component target);
	}

	private final Map<Component, LayoutDelegate> layoutDelegates = new HashMap<Component, LayoutDelegate>();

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		layoutDelegates.put(comp, (LayoutDelegate) constraints);
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	public LayoutDelegate getDelegate(Component comp) {
		return layoutDelegates.get(comp);
	}

	public Rectangle getDesiredBounds(Component comp, LayoutSize size) {
		Container parent = comp.getParent();

		if (parent == null || parent.getLayout() != this) {
			throw new IllegalArgumentException("comp does not belong to this layout");
		}

		LayoutDelegate delegate = layoutDelegates.get(comp);
		if (delegate != null) {
			return delegate.desiredBounds(parent, comp, size);
		}

		return LayoutUtils.calculateInnerArea(parent, LayoutSize.ACTUAL);
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	public void invalidateLayout(Container target) {
	}

	private void layoutComponent(Container parent, Component comp, Set<Component> laidOut) {
		if (!laidOut.add(comp)) {
			return;
		}
		LayoutDelegate layoutDelegate = layoutDelegates.get(comp);
		if (layoutDelegate != null) {
			for (Component dep : layoutDelegate.getDependencies()) {
				if (layoutDelegates.containsKey(dep)) {
					layoutComponent(parent, dep, laidOut);
				}
			}
			layoutDelegate.layoutComponent(parent, comp);
		} else {
			Rectangle defaultBounds = LayoutUtils.calculateInnerArea(parent, LayoutSize.ACTUAL);
			comp.setBounds(defaultBounds);
		}

	}

	@Override
	public void layoutContainer(Container parent) {
		Set<Component> laidOut = new HashSet<Component>();

		for (Component comp : parent.getComponents()) {
			layoutComponent(parent, comp, laidOut);
		}
	}

	private Dimension layoutSize(Container parent, LayoutSize size) {
		Rectangle bounds = new Rectangle();
		for (Component comp : parent.getComponents()) {
			LayoutDelegate layoutDelegate = layoutDelegates.get(comp);
			if (layoutDelegate != null) {
				bounds.add(layoutDelegate.desiredBounds(parent, comp, size));
			} else {
				bounds.add(new Rectangle(size.get(comp)));
			}
		}

		bounds = bounds.union(new Rectangle(0, 0, 0, 0));

		return bounds.getSize();
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return layoutSize(parent, LayoutSize.MINIMUM);
	}

	public void onLayoutChanged(Container target) {
		target.invalidate();
		target.validate();
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return layoutSize(parent, LayoutSize.PREFERRED);
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		layoutDelegates.remove(comp);
	}
}
