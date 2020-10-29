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
package org.breakout;

import java.util.Set;

import org.andork.func.FloatBinaryOperator;
import org.andork.math3d.Vecmath;
import org.andork.spatial.RTraversal;
import org.andork.spatial.Rectmath;
import org.breakout.model.Survey3dModel;
import org.breakout.model.Survey3dModel.Shot3d;
import org.omg.CORBA.FloatHolder;

public class FluidPerspectiveToOrtho {
	private static float getFarthestExtent(
		Survey3dModel model,
		Set<Shot3d> shotsInView,
		float[] shotsInViewMbr,
		float[] direction,
		FloatBinaryOperator extentFunction) {
		FloatHolder farthest = new FloatHolder(Float.NaN);

		float[] temp = new float[3];

		RTraversal.traverse(model.getTree().getRoot(), node -> {
			if (!Rectmath.intersects3(shotsInViewMbr, node.mbr())) {
				return false;
			}
			return Rectmath.findCorner3(node.mbr(), temp, corner -> {
				float dist = Vecmath.dot3(corner, direction);
				return farthest.value != extentFunction.applyAsFloat(farthest.value, dist) ? true : null;
			}) ? true : false; // make sure we don't return null!
		}, leaf -> {
			if (shotsInView.contains(leaf.object())) {
				for (float[] coord : leaf.object().vertices(temp)) {
					float dist = Vecmath.dot3(coord, direction);
					farthest.value = extentFunction.applyAsFloat(farthest.value, dist);
				}
			}
			return true;
		});

		return farthest.value;
	}

	public static float[] getOrthoBounds(
		Survey3dModel model,
		Set<Shot3d> shotsInView,
		float[] orthoRight,
		float[] orthoUp,
		float[] orthoForward) {
		float[] result = new float[4];

		float[] shotsInViewMbr = Rectmath.voidRectf(3);

		for (Shot3d shot : shotsInView) {
			shot.unionMbrInto(shotsInViewMbr);
		}

		FloatBinaryOperator minFunc = (a, b) -> Float.isNaN(a) || b < a ? b : a;
		FloatBinaryOperator maxFunc = (a, b) -> Float.isNaN(a) || b > a ? b : a;

		result[0] = getFarthestExtent(model, shotsInView, shotsInViewMbr, orthoRight, minFunc);
		result[1] = getFarthestExtent(model, shotsInView, shotsInViewMbr, orthoUp, minFunc);
		result[2] = getFarthestExtent(model, shotsInView, shotsInViewMbr, orthoForward, minFunc);
		result[3] = getFarthestExtent(model, shotsInView, shotsInViewMbr, orthoRight, maxFunc);
		result[4] = getFarthestExtent(model, shotsInView, shotsInViewMbr, orthoUp, maxFunc);
		result[5] = getFarthestExtent(model, shotsInView, shotsInViewMbr, orthoForward, maxFunc);

		return result;
	}
}
