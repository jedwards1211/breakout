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
package org.andork.math.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Fitting {
	/**
	 * Finds the exponential curve through three points where x1, x2, and x3 are
	 * equally spaced.
	 * 
	 * https://math.stackexchange.com/questions/3311614/find-the-exponential-curve-through-three-data-points
	 * 
	 * @param x1 The x coordinate of the first point
	 * @param y1 The y coordinate of the first point
	 * @param x3 The x coordinate of the third point
	 * @param y3 The y coordinate of the third point
	 * @param y2 The y coordinate of the second point (note that there is no x2
	 *           argument. x2 is assumed to be (x1 + x3) / 2.
	 * @return [a, b, c] such that y_i = a * e ^ (b * x_i) + c.
	 */
	public static double[] threePointExponential(double x1, double y1, double x3, double y3, double y2) {
		if (x3 <= x1)
			throw new IllegalArgumentException("x3 must be > x1");
		if (y3 <= y1)
			throw new IllegalArgumentException("y3 must be > y1");
		if (y3 <= y2)
			throw new IllegalArgumentException("y3 must be > y2");
		if (y2 <= y1)
			throw new IllegalArgumentException("y2 must be > y1");
		double d = (x3 - x1) / 2;
		double r = (y3 - y2) / (y2 - y1);
		double b = Math.log(r) / d;
		double a = (y3 - y1) / (Math.pow(r, x3 / d) - Math.pow(r, x1 / d));
		double c = y1 - a * Math.exp(b * x1);
		return new double[] { a, b, c };
	}

	/**
	 * Performs a linear least-squares fit.
	 *
	 * @param points a list of 2-dimensional points (x, y)
	 * @return [m, b] such that the least-fit line is y = m*x + b
	 */
	public static float[] linearLeastSquares2f(Iterable<float[]> points) {
		float A0 = 0, A1 = 0, A2 = 0, A3 = 0;
		float B0 = 0, B1 = 0;

		for (float[] point : points) {
			if (Float.isNaN(point[0]) || Float.isNaN(point[1]) || Float.isInfinite(point[0]) || Float.isNaN(point[1])) {
				continue;
			}

			B0 += 2f * point[1];
			B1 += 2f * point[1] * point[0];

			A0 += 2f;
			A1 += 2f * point[0];
			A2 += 2f * point[0];
			A3 += 2f * point[0] * point[0];
		}

		float det = A0 * A3 - A1 * A2;
		if (det == 0f) {
			return new float[] { Float.NaN, Float.NaN };
		}

		float rdet = 1f / det;

		float b = rdet * (A3 * B0 - A2 * B1);
		float m = rdet * (-A1 * B0 + A0 * B1);

		return new float[] { m, b };
	}

	/**
	 * @param points a list of 2-element points
	 * @return a 2-element [slope, intercept] array
	 */
	public static float[] theilSen(List<float[]> points) {
		List<Float> slopes = new ArrayList<>();

		for (int i = 0; i < points.size(); i++) {
			float[] p1 = points.get(i);
			for (int j = i + 1; j < points.size(); j++) {
				float[] p2 = points.get(j);
				slopes.add((p2[1] - p1[1]) / (p2[0] - p1[0]));
			}
		}

		Collections.sort(slopes);

		float[] result = new float[2];
		result[0] = slopes.get(slopes.size() / 2);
		if ((slopes.size() & 0x1) == 0) {
			result[0] = 0.5f * (result[0] + slopes.get(slopes.size() / 2 - 1));
		}

		float[] intercepts = new float[points.size()];
		for (int i = 0; i < points.size(); i++) {
			float[] point = points.get(i);
			intercepts[i] = point[1] - result[0] * point[0];
		}

		Arrays.sort(intercepts);
		result[1] = intercepts[points.size() / 2];
		if ((points.size() & 0x1) == 0) {
			result[1] = 0.5f * (result[1] + intercepts[points.size() / 2 - 1]);
		}

		return result;
	}

}
