package org.andork.swing.table;

import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.andork.swing.list.RealListModel;

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

	@SuppressWarnings("unchecked")
	public static <E> List<E> getList(TableModel model) {
		if (model instanceof ListTableModel) {
			return RealListModel.getList(((ListTableModel<E>) model).getListModel());
		}
		return null;
	}

	private final ListModel<E> listModel;

	private final Listener listener = new Listener();

	private boolean editable = true;

	public ListTableModel(List<E> list) {
		this((ListModel<E>) RealListModel.wrap(list));
	}

	public ListTableModel(ListModel<E> list) {
		this.listModel = list;
		list.addListDataListener(listener);
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	public ListModel<E> getListModel() {
		return listModel;
	}

	@Override
	public int getRowCount() {
		return listModel != null ? listModel.getSize() : 0;
	}

	@Override
	public E getValueAt(int rowIndex, int columnIndex) {
		return listModel.getElementAt(rowIndex);
	}

	/**
	 * Always returns {@code true}. If you want a cell not to be editable, make
	 * sure its
	 * {@linkplain ListTableColumn#editor(javax.swing.table.TableCellEditor)
	 * column editor} is {@code null}.
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (listModel instanceof List) {
			((List<E>) listModel).set(rowIndex, (E) aValue);
		}
	}
}
