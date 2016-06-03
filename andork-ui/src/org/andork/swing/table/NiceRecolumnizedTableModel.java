package org.andork.swing.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.andork.swing.table.NiceTableModel.Column;

public class NiceRecolumnizedTableModel extends AbstractTableModel implements TableModelListener {
	/**
	 *
	 */
	private static final long serialVersionUID = -3582058456019625665L;

	private TableModel model;

	private final List<Column<Integer>> columns = new ArrayList<>();
	private final List<Column<Integer>> unmodifiableColumns = Collections.unmodifiableList(columns);

	public NiceRecolumnizedTableModel() {

	}

	public NiceRecolumnizedTableModel(TableModel model) {
		setModel(model);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columns.get(columnIndex).getColumnClass();
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public String getColumnName(int column) {
		return columns.get(column).getColumnName();
	}

	public List<Column<Integer>> getColumns() {
		return unmodifiableColumns;
	}

	public TableModel getModel() {
		return model;
	}

	@Override
	public int getRowCount() {
		return model == null ? 0 : model.getRowCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return columns.get(columnIndex).getValueAt(rowIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columns.get(columnIndex).isCellEditable(rowIndex);
	}

	public void setColumns(List<Column<Integer>> newColumns) {
		columns.clear();
		columns.addAll(newColumns);
		fireTableStructureChanged();
	}

	public void setModel(TableModel newModel) {
		if (model != newModel) {
			if (model != null) {
				model.removeTableModelListener(this);
			}
			model = newModel;
			if (newModel != null) {
				newModel.addTableModelListener(this);
			}
			fireTableDataChanged();
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		columns.get(columnIndex).setValueAt(aValue, rowIndex);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		switch (e.getType()) {
		case TableModelEvent.INSERT:
			fireTableChanged(new TableModelEvent(this, e.getFirstRow(), e.getLastRow(),
					TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
			break;
		case TableModelEvent.DELETE:
			fireTableChanged(new TableModelEvent(this, e.getFirstRow(), e.getLastRow(),
					TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
			break;
		case TableModelEvent.UPDATE:
			fireTableChanged(new TableModelEvent(this, e.getFirstRow(), e.getLastRow(),
					TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
			break;
		}
	}
}
