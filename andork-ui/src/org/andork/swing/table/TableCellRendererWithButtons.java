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
package org.andork.swing.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.andork.swing.CellRendererWithButtons;

@SuppressWarnings("serial")
public abstract class TableCellRendererWithButtons extends CellRendererWithButtons implements TableCellRendererTracker, TableCellRenderer {
	protected TableCellRenderer	wrapped;

	protected int				rendererRow;
	protected int				rendererColumn;

	protected TableCellRendererWithButtons(TableCellRenderer wrapped) {
		super();
		this.wrapped = wrapped;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		rendererRow = row;
		rendererColumn = column;
		setContent(wrapped.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column));
		return this;
	}

	@Override
	public int getRendererRow() {
		return rendererRow;
	}

	@Override
	public int getRendererColumn() {
		return rendererColumn;
	}
}
