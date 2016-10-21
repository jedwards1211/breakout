package org.andork.swing.table;

import java.awt.Component;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class ListTableColumn<R, V> extends TableColumn {
	@SuppressWarnings("serial")
	private class CellEditor extends AbstractCellEditor implements TableCellEditor {
		R editingRow;

		@SuppressWarnings("unchecked")
		@Override
		public Object getCellEditorValue() {
			TableCellEditor editor = ListTableColumn.super.getCellEditor();
			return setter.apply(editingRow, (V) editor.getCellEditorValue());
		}

		@SuppressWarnings("unchecked")
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
				int row, int column) {
			TableCellEditor editor = ListTableColumn.super.getCellEditor();
			editingRow = (R) value;
			return editor.getTableCellEditorComponent(table, getter.apply(editingRow), isSelected, row, column);
		}
	}

	private class CellRenderer implements TableCellRenderer {
		@SuppressWarnings("unchecked")
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			TableCellRenderer renderer = ListTableColumn.super.getCellRenderer();
			return renderer.getTableCellRendererComponent(
					table, getter.apply((R) value), isSelected, hasFocus, row, column);
		}
	}

	private static final long serialVersionUID = -7933306824250262751L;
	private Function<R, V> getter;
	private BiFunction<R, V, R> setter;
	private final CellRenderer renderer = new CellRenderer();
	private final CellEditor editor = new CellEditor();

	public ListTableColumn() {
		super(0);
		super.setCellRenderer(new DefaultTableCellRenderer());
		super.setCellEditor(new DefaultCellEditor(new JTextField()));
	}

	public TableCellEditor editor() {
		return super.getCellEditor();
	}

	public ListTableColumn<R, V> editor(TableCellEditor editor) {
		super.setCellEditor(editor);
		return this;
	}

	@Override
	public TableCellEditor getCellEditor() {
		return super.getCellEditor() != null ? editor : null;
	}

	@Override
	public TableCellRenderer getCellRenderer() {
		return super.getCellRenderer() != null ? renderer : null;
	}

	public ListTableColumn<R, V> getter(Function<R, V> getter) {
		this.getter = getter;
		return this;
	}

	public TableCellRenderer renderer() {
		return super.getCellRenderer();
	}

	public ListTableColumn<R, V> renderer(TableCellRenderer renderer) {
		super.setCellRenderer(renderer);
		return this;
	}

	public ListTableColumn<R, V> setter(BiConsumer<R, V> setter) {
		this.setter = (row, value) -> {
			setter.accept(row, value);
			return row;
		};
		return this;
	}

	public ListTableColumn<R, V> setter(BiFunction<R, V, R> setter) {
		this.setter = setter;
		return this;
	}
}
