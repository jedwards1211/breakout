/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.util;

public class Levenshtein {
	private Levenshtein() {

	}

	public static int distance(String a, String b) {
		int d[][] = new int[a.length() + 1][b.length() + 1];

		for (int i = 1; i <= a.length(); i++) {
			d[i][0] = i;
		}
		for (int j = 1; j <= b.length(); j++) {
			d[0][j] = j;
		}

		for (int j = 1; j <= b.length(); j++) {
			for (int i = 1; i <= a.length(); i++) {
				if (a.charAt(i - 1) == b.charAt(j - 1)) {
					d[i][j] = d[i - 1][j - 1];
				} else {
					d[i][j] = Math.min(d[i - 1][j] + 1, Math.min(d[i][j - 1] + 1, d[i - 1][j - 1] + 1));
				}
			}
		}

		return d[a.length()][b.length()];
	}
}
