package org.andork.awt;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public interface AnnotatingTableCellRenderer extends TableCellRenderer
{
	/**
	 * Gets the cell renderer component. This method is identical to {@link #getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)} except
	 * it contains an additional parameter, {@code annotation} that is passed from the {@link AnnotatingRowSorter}.
	 */
	Component getTableCellRendererComponent( JTable table , Object value ,
			Object annotation ,
			boolean isSelected , boolean hasFocus ,
			int row , int column );
}
