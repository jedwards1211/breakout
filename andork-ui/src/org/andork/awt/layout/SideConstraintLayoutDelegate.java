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
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.andork.awt.layout.DelegatingLayoutManager.LayoutDelegate;

public class SideConstraintLayoutDelegate implements LayoutDelegate {
	private final Map<Side, SideConstraint> extraConstraints = new HashMap<>();

	private void applyConstraints(final Component target, Rectangle targetBounds) {
		for (Map.Entry<Side, SideConstraint> entry : extraConstraints.entrySet()) {
			Side side = entry.getKey();
			SideConstraint constraint = entry.getValue();

			Point p = new Point();
			side.axis().set(p, constraint.location());
			p = SwingUtilities.convertPoint(constraint.targetComponent.getParent(), p, target.getParent());

			if (extraConstraints.containsKey(side.opposite())) {
				side.stretch(targetBounds, side.axis().get(p));
			} else {
				side.setLocation(targetBounds, side.axis().get(p));
			}
		}
	}

	@Override
	public Rectangle desiredBounds(Container parent, Component target, LayoutSize layoutSize) {
		Rectangle bounds = new Rectangle(layoutSize.get(target));
		applyConstraints(target, bounds);
		return bounds;
	}

	public SideConstraint extraConstraint(Side side) {
		return extraConstraints.get(side);
	}

	@Override
	public List<Component> getDependencies() {
		if (extraConstraints.isEmpty()) {
			return Collections.emptyList();
		}
		List<Component> result = new ArrayList<Component>(extraConstraints.size());
		for (SideConstraint constraint : extraConstraints.values()) {
			result.add(constraint.targetComponent);
		}
		return result;
	}

	@Override
	public void layoutComponent(final Container parent, final Component target) {
		target.setBounds(desiredBounds(parent, target, LayoutSize.PREFERRED));
	}

	public SideConstraintLayoutDelegate putExtraConstraint(Side side, SideConstraint constraint) {
		extraConstraints.put(side, constraint);
		return this;
	}
}
