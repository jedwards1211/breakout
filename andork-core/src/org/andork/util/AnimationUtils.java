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
package org.andork.util;

public class AnimationUtils {

	public static float animate(float current, float target, long time, float factor, float extra, int delay) {
		while (time > 0 && current != target) {
			if (target > current) {
				current = Math.min(target, current + (target - current + extra) * factor);
			} else {
				current = Math.max(target, current + (target - current - extra) * factor);
			}

			time -= delay;
		}
		return current;
	}

	public static int animate(int current, int target, long time, float factor, int extra, int delay) {
		while (time > 0 && current != target) {
			if (target > current) {
				current = Math.min(target, Math.max(current + 1,
						current + (int) ((target - current + extra) * factor)));
			} else {
				current = Math.max(target, Math.min(current - 1,
						current + (int) ((target - current - extra) * factor)));
			}

			time -= delay;
		}
		return current;
	}

	public static float getInterpFactor(float a, float b, float x) {
		if (b - a == 0) {
			return 0;
		}
		return (x - a) / (b - a);
	}

	private AnimationUtils() {
	}

}
