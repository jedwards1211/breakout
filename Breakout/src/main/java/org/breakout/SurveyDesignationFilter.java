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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import org.andork.q.QObject;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.SurveyTableModel.Row;

public class SurveyDesignationFilter extends RowFilter<TableModel, Integer> {
	private static class Segment {
		String designation;

		BigDecimal lowerBound;
		BigDecimal upperBound;

		public Segment(String s) {
			Matcher m = SEGMENT_PATTERN.matcher(s);
			if (!m.find()) {
				throw new IllegalArgumentException("Not a valid segment: " + s);
			}

			designation = m.group(1);

			if (m.group(2) != null) {
				lowerBound = upperBound = new BigDecimal(m.group(3));
			}

			if (m.group(5) != null) {
				if (m.group(5).equals("+")) {
					upperBound = null;
				} else {
					upperBound = new BigDecimal(m.group(7));
				}
			}
		}

		protected void checkBound(String s, BigDecimal bound) throws Exception {
			BigDecimal number = new BigDecimal(s);
			if (bound == lowerBound && number.compareTo(bound) < 0) {
				throw new RuntimeException("number out of bounds");
			}
			if (bound == upperBound && number.compareTo(bound) > 0) {
				throw new RuntimeException("number out of bounds");
			}
		}

		public boolean include(String from) {
			int index = from.indexOf(designation);
			// allow something to come before the designation as long as it's
			// not another letter
			if (index < 0 || index > 0 && Character.isLetter(from.charAt(index - 1))) {
				return false;
			}

			if (from.length() > index + designation.length()
					&& Character.isLetter(from.charAt(index + designation.length()))) {
				return false;
			}

			try {
				if (lowerBound != null) {
					checkBound(from.substring(index + designation.length()).trim(), lowerBound);
				}

				if (upperBound != null) {
					checkBound(from.substring(index + designation.length()).trim(), upperBound);
				}
			} catch (Exception ex) {
				return false;
			}

			return true;
		}
	}

	private static final Pattern SEGMENT_PATTERN = Pattern
			.compile("\\s*(\\D+)\\s*((\\d+(\\.\\d+)?)\\s*(\\+|(-\\s*(\\d+(\\.\\d+)?)))?)?\\s*");

	Segment[] segments;

	public SurveyDesignationFilter(String designation) {
		// this.designation = designation;
		String[] split = designation.split("[,;]");
		segments = new Segment[split.length];
		int k = 0;
		for (String s : split) {
			try {
				segments[k] = new Segment(s);
				k++;
			} catch (Exception ex) {
			}
		}
		segments = Arrays.copyOf(segments, k);
	}

	@Override
	public boolean include(javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
		QObject<Row> row = ((SurveyTableModel) entry.getModel()).getRow(entry.getIdentifier());
		if (row == null) {
			return false;
		}

		boolean foundFrom = false;
		boolean foundTo = false;

		String from = row.get(Row.from);
		String to = row.get(Row.to);

		for (Segment segment : segments) {
			if (!foundFrom && from != null && segment.include(from)) {
				foundFrom = true;
				if (foundTo) {
					return true;
				}
			}
			if (!foundTo && to != null && segment.include(to)) {
				foundTo = true;
				if (foundFrom) {
					return true;
				}
			}
		}
		return false;
	}
}
