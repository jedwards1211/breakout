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
package org.andork.swing.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.andork.swing.AnnotatingRowSorter;

public interface AnnotatingTableCellRenderer extends TableCellRenderer {
	/**
	 * Gets the cell renderer component. This method is identical to
	 * {@link #getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)}
	 * except it contains an additional parameter, {@code annotation} that is
	 * passed from the {@link AnnotatingRowSorter}.
	 */
	Component getTableCellRendererComponent(JTable table, Object value,
			Object annotation,
			boolean isSelected, boolean hasFocus,
			int row, int column);
}
