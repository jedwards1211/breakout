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
package org.andork.swing.table.old;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.andork.swing.table.old.FilteringTableModel.Filter;
import org.andork.swing.table.old.FilteringTableModel.HighlightingFilterResult;

@SuppressWarnings("serial")
public class HighlightingTable extends JTable {
	/**
	 *
	 */
	private static final long serialVersionUID = 3963921755608260116L;

	private final Map<Filter, Color> highlightColors = new HashMap<Filter, Color>();

	HighlightingTable() {
		super();
	}

	public HighlightingTable(TableModel dm) {
		super(dm);
	}

	public Color getHighlightColor(Filter filter) {
		return highlightColors.get(filter);
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row,
			int column) {
		Component rend = super.prepareRenderer(renderer, row, column);
		if (getModel() instanceof FilteringTableModel) {
			FilteringTableModel model = (FilteringTableModel) getModel();
			if (!isRowSelected(row)) {
				rend.setBackground(getBackground());
				Object filterResult = model.getFilterResultForRow(row);
				if (filterResult != null
						&& filterResult instanceof HighlightingFilterResult) {
					HighlightingFilterResult hfr = (HighlightingFilterResult) filterResult;
					if (hfr.highlightingFilter != null) {
						rend.setBackground(highlightColors.get(hfr.highlightingFilter));
					}
				}
			}
		}
		return rend;
	}

	public void setHighlightColors(Map<Filter, Color> highlightColors) {
		this.highlightColors.clear();
		this.highlightColors.putAll(highlightColors);
		repaint();
	}
}
