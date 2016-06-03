package org.andork.swing.table;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * A {@link TableCellRenderer} that can accept mouse input directly, decoupling
 * column or cell-specific logic from the table. To use, register a
 * {@link MouseInputTableCellRenderer.Controller} as a
 * {@link JTable#addMouseListener(java.awt.event.MouseListener) mouseListener}
 * on a {@link JTable}; it will automatically detect
 * {@link MouseInputTableCellRenderer}s and forward input to them.
 *
 * @author James
 */
public interface MouseInputTableCellRenderer extends TableCellRenderer {
	/**
	 * When this {@link MouseAdapter} receives {@link #mouseClicked(MouseEvent)
	 * mouseClicked} events from a {@link JTable}, it will get the renderer for
	 * the clicked cell, and if it is a {@link MouseInputTableCellRenderer}, it
	 * will call its
	 * {@link MouseInputTableCellRenderer#mouseClicked(JTable, Object, int, int, MouseEvent)
	 * mouseClicked} method.
	 *
	 * @author James
	 */
	public static class Controller extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (!(e.getComponent() instanceof JTable)) {
				return;
			}
			JTable table = (JTable) e.getComponent();
			Point p = e.getPoint();
			int row = table.rowAtPoint(p);
			int column = table.columnAtPoint(p);
			Object value = table.getValueAt(row, column);

			TableCellRenderer renderer = table.getCellRenderer(row, column);
			if (renderer instanceof MouseInputTableCellRenderer) {
				((MouseInputTableCellRenderer) renderer).mouseClicked(table, value, row, column, e);
			}
		}
	}

	public void mouseClicked(JTable table, Object value, int row, int column, MouseEvent e);
}
