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

import java.util.regex.Pattern;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import org.breakout.model.SurveyTableModel;
import org.breakout.model.raw.SurveyRow;

public class DescriptionFilter extends RowFilter<TableModel, Integer> {
	// String[ ] descriptions;
	Pattern[] descriptions;

	public DescriptionFilter(String descriptions) {
		String[] parts = descriptions.toLowerCase().split("\\s+");
		this.descriptions = new Pattern[parts.length];
		for (int i = 0; i < parts.length; i++) {
			this.descriptions[i] = Pattern.compile("\\b" + parts[i] + "\\b");
		}
	}

	@Override
	public boolean include(javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
		if (descriptions.length == 0) {
			return true;
		}

		SurveyRow row = ((SurveyTableModel) entry.getModel()).getRow(entry.getIdentifier());
		if (row == null || row.getTrip() == null || row.getTrip().getName() == null) {
			return false;
		}
		String desc = row.getTrip().getName().toLowerCase();
		for (Pattern description : descriptions) {
			if (!description.matcher(desc).find()) {
				return false;
			}
		}
		return true;
	}
}
