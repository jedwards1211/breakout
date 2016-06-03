package org.andork.swing.table;

import java.awt.Component;
import java.util.EventObject;
import java.util.function.Function;

import javax.swing.CellEditor;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class FunctionCellEditor implements CellEditor, TableCellEditor {
	CellEditor wrapped;

	Function valueToEditor;
	Function editorToValue;

	public FunctionCellEditor(CellEditor wrapped, Function valueToEditor, Function editorToValue) {
		super();
		this.wrapped = wrapped;
		this.valueToEditor = valueToEditor;
		this.editorToValue = editorToValue;
	}

	@Override
	public void addCellEditorListener(CellEditorListener l) {
		wrapped.addCellEditorListener(l);
	}

	@Override
	public void cancelCellEditing() {
		wrapped.cancelCellEditing();
	}

	@Override
	public Object getCellEditorValue() {
		return editorToValue.apply(wrapped.getCellEditorValue());
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
			int column) {
		return ((TableCellEditor) wrapped).getTableCellEditorComponent(table, valueToEditor.apply(value),
				isSelected, row, column);
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		return wrapped.isCellEditable(anEvent);
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {
		wrapped.removeCellEditorListener(l);
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		return wrapped.shouldSelectCell(anEvent);
	}

	@Override
	public boolean stopCellEditing() {
		return wrapped.stopCellEditing();
	}
}
