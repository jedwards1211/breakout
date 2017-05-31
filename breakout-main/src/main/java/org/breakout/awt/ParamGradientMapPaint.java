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

package org.breakout.awt;

import static org.andork.math3d.Vecmath.hasNaNsOrInfinites;
import static org.andork.math3d.Vecmath.setf;

import java.awt.Color;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.util.Arrays;

/**
 * The {@code ParamGradientMapPaint} class provides a way to fill a
 * {@link java.awt.Shape} with a linear color gradient pattern. The user may
 * specify two or more gradient colors, and this paint will provide an
 * interpolation between each color. The user also specifies start and end
 * points which define where in user space the color gradient should begin and
 * end.
 * <p>
 * The user must provide an array of floats specifying how to distribute the
 * colors along the gradient. These values should range from 0.0 to 1.0 and act
 * like keyframes along the gradient (they mark where the gradient should be
 * exactly a particular color).
 * <p>
 * In the event that the user does not set the first keyframe value equal to 0
 * and/or the last keyframe value equal to 1, keyframes will be created at these
 * positions and the first and last colors will be replicated there. So, if a
 * user specifies the following arrays to construct a gradient:<br>
 *
 * <pre>
 *     {Color.BLUE, Color.RED}, {.3f, .7f}
 * </pre>
 *
 * this will be converted to a gradient with the following keyframes:<br>
 *
 * <pre>
 *     {Color.BLUE, Color.BLUE, Color.RED, Color.RED}, {0f, .3f, .7f, 1f}
 * </pre>
 *
 * <p>
 * The user may also select what action the {@code ParamGradientMapPaint} should
 * take when filling color outside the start and end points. If no cycle method
 * is specified, {@code NO_CYCLE} will be chosen by default, which means the
 * endpoint colors will be used to fill the remaining area.
 * <p>
 * The colorSpace parameter allows the user to specify in which colorspace the
 * interpolation should be performed, default sRGB or linearized RGB.
 *
 * <p>
 * The following code demonstrates typical usage of
 * {@code ParamGradientMapPaint}:
 * <p>
 *
 * <pre>
 * Point2D start = new Point2D.Float(0, 0);
 * Point2D end = new Point2D.Float(50, 50);
 * float[] dist = { 0.0f, 0.2f, 1.0f };
 * Color[] colors = { Color.RED, Color.WHITE, Color.BLUE };
 * ParamGradientMapPaint p = new ParamGradientMapPaint(start, end, dist, colors);
 * </pre>
 * <p>
 * This code will create a {@code ParamGradientMapPaint} which interpolates
 * between red and white for the first 20% of the gradient and between white and
 * blue for the remaining 80%.
 *
 * <p>
 * This image demonstrates the example code above for each of the three cycle
 * methods:
 * <p>
 * <center> <img src = "doc-files/ParamGradientMapPaint.png"> </center>
 *
 * @see java.awt.Paint
 * @see java.awt.Graphics2D#setPaint
 * @author Nicholas Talian, Vincent Hardy, Jim Graham, Jerry Evans
 * @since 1.6
 */
public final class ParamGradientMapPaint extends MultipleGradientPaint {

	private static float[] normalizeFractions(float[] fractions) {
		float[] normalized = new float[fractions.length];
		for (int i = 0; i < fractions.length; i++) {
			normalized[i] = (fractions[i] - fractions[0]) / (fractions[fractions.length - 1] - fractions[0]);
		}
		return normalized;
	}

	/** Gradient start and end points. */
	private final float[] origin = new float[2];
	private final float[] majorAxis = new float[2];
	private final float[] minorAxis = new float[2];

	private final float startParam, endParam;

	public ParamGradientMapPaint(float[] origin, float[] majorAxis, float[] minorAxis, float startParam, float endParam,
			float[] fractions, Color[] colors) {
		this(origin, majorAxis, minorAxis, startParam, endParam, fractions, colors,
				CycleMethod.NO_CYCLE, ColorSpaceType.SRGB, new AffineTransform());
	}

	/**
	 * Constructs a {@code ParamGradientMapPaint} with a default {@code SRGB}
	 * color space.
	 *
	 * @param startX
	 *            the X coordinate of the gradient axis start point in user
	 *            space
	 * @param startY
	 *            the Y coordinate of the gradient axis start point in user
	 *            space
	 * @param endX
	 *            the X coordinate of the gradient axis end point in user space
	 * @param endY
	 *            the Y coordinate of the gradient axis end point in user space
	 * @param fractions
	 *            numbers ranging from 0.0 to 1.0 specifying the distribution of
	 *            colors along the gradient
	 * @param colors
	 *            array of colors corresponding to each fractional value
	 * @param cycleMethod
	 *            either {@code NO_CYCLE}, {@code REFLECT}, or {@code REPEAT}
	 *
	 * @throws NullPointerException
	 *             if {@code fractions} array is null, or {@code colors} array
	 *             is null, or {@code cycleMethod} is null
	 * @throws IllegalArgumentException
	 *             if start and end points are the same points, or
	 *             {@code fractions.length != colors.length}, or {@code colors}
	 *             is less than 2 in size, or a {@code fractions} value is less
	 *             than 0.0 or greater than 1.0, or the {@code fractions} are
	 *             not provided in strictly increasing order
	 */
	public ParamGradientMapPaint(float[] origin, float[] majorAxis, float[] minorAxis, float startParam, float endParam,
			float[] fractions, Color[] colors,
			CycleMethod cycleMethod,
			ColorSpaceType colorSpace,
			AffineTransform gradientTransform) {
		super(normalizeFractions(fractions), colors, cycleMethod, colorSpace, gradientTransform);

		for (float[] item : Arrays.asList(origin, majorAxis, minorAxis, fractions,
				new float[] { startParam, endParam })) {
			if (hasNaNsOrInfinites(item)) {
				throw new IllegalArgumentException("arguments may not have NaN or infinite values");
			}
		}

		startParam = (startParam - fractions[0]) / (fractions[fractions.length - 1] - fractions[0]);
		endParam = (endParam - fractions[0]) / (fractions[fractions.length - 1] - fractions[0]);

		setf(this.origin, origin);
		setf(this.majorAxis, majorAxis);
		setf(this.minorAxis, minorAxis);
		this.startParam = startParam;
		this.endParam = endParam;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaintContext createContext(ColorModel cm,
			Rectangle deviceBounds,
			Rectangle2D userBounds,
			AffineTransform transform,
			RenderingHints hints) {
		// avoid modifying the user's transform...
		transform = new AffineTransform(transform);
		// incorporate the gradient transform
		transform.concatenate(gradientTransform);

		return new ParamGradientMapPaintContext(this, cm,
				deviceBounds, userBounds,
				transform, hints,
				origin, majorAxis, minorAxis, startParam, endParam,
				fractions, colors,
				cycleMethod, colorSpace);
	}
}
