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

import java.util.function.BiFunction;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import org.andork.swing.table.RowFilterFactory;
import org.andork.util.StringUtils;

public class MultiRowFilterFactory implements RowFilterFactory<String, TableModel, Integer> {
	BiFunction<String, String, RowFilter<TableModel, Integer>> filterMap;

	public MultiRowFilterFactory(BiFunction<String, String, RowFilter<TableModel, Integer>> filterMap) {
		this.filterMap = filterMap;
	}

	@Override
	public RowFilter<TableModel, Integer> createFilter(String input) {
		int colonIndex = StringUtils.unescapedIndexOf(input, ':', '\\');
		if (colonIndex < 0) {
			return filterMap.apply(null, input);
		}
		return filterMap.apply(StringUtils.escape(input.substring(0, colonIndex), '\\'),
				input.substring(colonIndex + 1));
	}
}
