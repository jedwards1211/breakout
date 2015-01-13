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
package org.andork.ui.test.fixture;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.andork.func.Predicate;
import org.andork.ui.test.DoSwingR;

public interface JTableFixture extends ComponentFixture {
	public void selectCell(JTable table, Cell cell);

	public static class Cell {
		public final int	row;
		public final int	column;

		public Cell(int row, int col) {
			super();
			this.row = row;
			this.column = col;
		}

		public Cell changeColumn(int column) {
			return new Cell(row, column);
		}
	}

	public static class Common {
		public static Cell findCellInColumnByText(final JTable table, final int col, final Predicate<String> p) {
			return new DoSwingR<Cell>() {
				@Override
				protected Cell doRun() {
					for (int row = 0; row < table.getRowCount(); row++) {
						if (p.eval(readText(table, row, col))) {
							return new Cell(row, col);
						}
					}
					return null;
				}
			}.result();
		}

		public static String readText(final JTable table, final int row, final int col) {
			return new DoSwingR<String>() {
				@Override
				protected String doRun() {
					TableCellRenderer renderer = table.getCellRenderer(row, col);
					Component rendComp = table.prepareRenderer(renderer, row, col);
					try {
						return ComponentFixture.Common.readText(rendComp);
					} catch (Exception ex) {
					}
					return String.valueOf(table.getValueAt(row, col));
				}
			}.result();
		}
	}
}
