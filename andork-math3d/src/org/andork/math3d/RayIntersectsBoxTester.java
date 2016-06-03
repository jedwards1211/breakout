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
package org.andork.math3d;

public class RayIntersectsBoxTester {

	public static boolean rayIntersects(float[] rayOrigin, float[] rayDirection, float[] rect) {
		for (int d = 0; d < 3; d++) {
			if (rayOrigin[d] <= rect[d] && rayDirection[d] < 0
					|| rayOrigin[d] >= rect[d + 3] && rayDirection[d] > 0) {
				return false;
			}
		}

		for (int d0 = 0; d0 < 3; d0++) {
			if (rayDirection[d0] == 0) {
				if (rayOrigin[d0] < rect[d0] || rayOrigin[d0] > rect[d0 + 3]) {
					return false;
				}
				continue;
			}

			float l0;

			if (rayOrigin[d0] <= rect[d0]) {
				l0 = rect[d0] - rayOrigin[d0];
			} else if (rayOrigin[d0] >= rect[d0 + 3]) {
				l0 = rect[d0 + 3] - rayOrigin[d0];
			} else {
				continue;
			}

			for (int i = 1; i < 3; i++) {
				int d1 = (d0 + i) % 3;
				float l1 = rayDirection[d1] * l0 / rayDirection[d0];
				if (rayOrigin[d1] <= rect[d1 + 3] && rayOrigin[d1] + l1 > rect[d1 + 3] ||
						rayOrigin[d1] >= rect[d1] && rayOrigin[d1] + l1 < rect[d1]) {
					return false;
				}
			}
		}

		return true;
	}

}
