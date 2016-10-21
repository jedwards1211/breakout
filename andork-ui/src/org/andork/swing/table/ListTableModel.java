package org.andork.swing.table;

import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

public class ListTableModel<E> extends AbstractTableModel {
	private class Listener implements ListDataListener {
		@Override
		public void contentsChanged(ListDataEvent e) {
			fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
		}

		@Override
		public void intervalAdded(ListDataEvent e) {
			fireTableRowsInserted(e.getIndex0(), e.getIndex1());
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
		}
	}

	private static final long serialVersionUID = -3448915340186127126L;

	private final ListModel<E> list;
	private final Listener listener = new Listener();

	public ListTableModel(ListModel<E> list) {
		this.list = list;
		list.addListDataListener(listener);
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		return list != null ? list.getSize() : 0;
	}

	@Override
	public E getValueAt(int rowIndex, int columnIndex) {
		return list.getElementAt(rowIndex);
	}

	/**
	 * Always returns {@code true}. If you want a cell not to be editable, make
	 * sure its
	 * {@linkplain ListTableColumn#editor(javax.swing.table.TableCellEditor)
	 * column editor} is {@code null}.
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (list instanceof List) {
			((List<E>) list).set(rowIndex, (E) aValue);
		}
	}
}
