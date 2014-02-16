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
