package org.andork.ui.test.fixture;

import javax.swing.JTable;

import org.andork.swing.DoSwing;

public class HardJTableFixture extends HardComponentFixture implements JTableFixture {

	@Override
	public void selectCell(final JTable table, final Cell cell) {
		new DoSwing() {
			@Override
			public void run() {
				table.getSelectionModel().setSelectionInterval(cell.row, cell.row);
				table.getColumnModel().getSelectionModel().setSelectionInterval(cell.column, cell.column);
			}
		};
	}

}
