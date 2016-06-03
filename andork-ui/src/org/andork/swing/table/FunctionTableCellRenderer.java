package org.andork.swing.table;

import java.awt.Component;
import java.util.function.Function;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("rawtypes")
public class FunctionTableCellRenderer implements TableCellRenderer {
	Function valueFn;
	TableCellRenderer wrapped;

	public FunctionTableCellRenderer(Function valueFn, TableCellRenderer wrapped) {
		super();
		this.valueFn = valueFn;
		this.wrapped = wrapped;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		return wrapped.getTableCellRendererComponent(table, valueFn.apply(value), isSelected, hasFocus, row,
				column);
	}
}
