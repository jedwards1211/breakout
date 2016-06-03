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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevenshteinCorrector {
	private final List<String> expected = new ArrayList<String>();
	private final Map<String, String> corrections = new HashMap<String, String>();

	public void addCorrection(String s, String correction) {
		corrections.put(s.toLowerCase(), correction);
	}

	public void addExpected(Collection<String> expected) {
		this.expected.addAll(expected);
	}

	public String correct(String s) {
		s = s.toLowerCase();
		String correction = corrections.get(s);
		if (correction == null) {
			correction = s;
			int bestDist = s.length() * 3 / 4;
			for (String exp : expected) {
				String explc = exp.toLowerCase();
				int dist = Levenshtein.distance(s, explc);
				if (s.startsWith(explc.substring(0, 1)) && dist < bestDist) {
					correction = exp;
					bestDist = dist;
				}
			}
			System.out.println(s + " -> " + correction);
			corrections.put(s, correction);
		}
		return correction;
	}
}
