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
package org.andork.ui.test;

import java.util.Comparator;

import org.andork.util.ArrayUtils;

public class ConfigurableStringComparator implements Comparator<String> {

	public static enum Option {
		IGNORE_CASE, TRIM, IGNORE_WHITESPACE
	}

	boolean	ignoreCase;
	boolean	trim;
	boolean	ignoreWhitespace;

	public ConfigurableStringComparator(Option... options) {
		trim = ArrayUtils.contains(options, Option.TRIM);
		ignoreCase = ArrayUtils.contains(options, Option.IGNORE_CASE);
		ignoreWhitespace = ArrayUtils.contains(options, Option.IGNORE_WHITESPACE);
	}

	public String reformat(String s) {
		if (ignoreWhitespace) {
			s = s == null ? null : s.replaceAll("\\s+", "");
		}
		else if (trim) {
			s = s == null ? null : s.trim();
		}
		if (ignoreCase) {
			s = s == null ? null : s.toLowerCase();
		}
		return s;
	}

	public int compare(String a, String b) {
		if (ignoreWhitespace) {
			a = a == null ? null : a.replaceAll("\\s+", "");
			b = b == null ? null : b.replaceAll("\\s+", "");
		}
		else if (trim) {
			a = a == null ? null : a.trim();
			b = b == null ? null : b.trim();
		}
		if (ignoreCase) {
			a = a == null ? null : a.toLowerCase();
			b = b == null ? null : b.toLowerCase();
		}
		if (a == null) {
			if (b == null) {
				return 0;
			}
			return 1;
		}
		if (b == null) {
			return -1;
		}
		return a.compareTo(b);
	}

	public boolean equals(String a, String b) {
		return compare(a, b) == 0;
	}

	public boolean contains(String a, String substr) {
		return reformat(a).contains(reformat(substr));
	}
}
