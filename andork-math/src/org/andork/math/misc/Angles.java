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

public class Angles {
	public static double rotationTo(double from, double to) {
		double difference = normalize(to - from);
		return difference <= Math.PI ? difference : difference - Math.PI * 2;
	}

	public static double absDifference(double angle0, double angle1) {
		double difference = normalize(angle0 - angle1);
		return difference <= Math.PI ? difference : Math.PI * 2 - difference;
	}

	public static double normalize(double angle) {
		angle %= Math.PI * 2;
		return angle >= 0 ? angle : angle + Math.PI * 2;
	}

	public static double opposite(double radians) {
		return (radians + Math.PI) % (Math.PI * 2);
	}

	public static double bisector(double angle0, double angle1) {
		return angle0 + rotationTo(angle0, angle1) / 2;
	}

	public static double average(double... anglesInRadians) {
		switch (anglesInRadians.length) {
		case 0:
			return Double.NaN;
		case 1:
			return anglesInRadians[0];
		case 2:
			return normalize(anglesInRadians[0] + rotationTo(anglesInRadians[0], anglesInRadians[1]) / 2);
		default:
			double x = 0;
			double y = 0;
			for (double angle : anglesInRadians) {
				x += Math.cos(angle);
				y += Math.sin(angle);
			}
			return Math.atan2(y, x);
		}
	}
}
