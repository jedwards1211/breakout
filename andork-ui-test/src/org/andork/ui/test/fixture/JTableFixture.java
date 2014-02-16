package org.andork.ui.test.fixture;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.andork.func.Predicate;
import org.andork.swing.DoSwingR2;

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
			return new DoSwingR2<Cell>() {
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
			return new DoSwingR2<String>() {
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
