package org.andork.swing.table;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.andork.swing.table.ListTableModel.Column;

@SuppressWarnings("serial")
public class ListTableColumn<R, V> extends TableColumn {
	private final ListTableModel.Column<R, V> modelColumn;

	public ListTableColumn(ListTableModel.Column<R, V> modelColumn) {
		super(0);
		this.modelColumn = modelColumn;
	}

	public ListTableColumn<R, V> editor(TableCellEditor editor) {
		setCellEditor(editor);
		return this;
	}

	public ListTableColumn<R, V> headerValue(Object value) {
		setHeaderValue(value);
		return this;
	}

	public ListTableColumn<R, V> maxWidth(int maxWidth) {
		setMaxWidth(maxWidth);
		return this;
	}

	public ListTableColumn<R, V> preferredWidth(int preferredWidth) {
		setPreferredWidth(preferredWidth);
		return this;
	}

	public ListTableColumn<R, V> renderer(TableCellRenderer renderer) {
		setCellRenderer(renderer);
		return this;
	}

	@SuppressWarnings("unchecked")
	public static <R> void updateModelIndices(JTable table) {
		ListTableModel<R> m = (ListTableModel<R>) table.getModel();
		TableColumnModel cm = table.getColumnModel();
		Map<Column<? super R, ?>, Integer> modelIndices = new HashMap<>();
		int index = 0;
		for (Column<? super R, ?> c : m.getColumns()) {
			modelIndices.put(c, index++);
		}

		for (int i = 0; i < cm.getColumnCount(); i++) {
			TableColumn c = cm.getColumn(i);
			if (c instanceof ListTableColumn) {
				c.setModelIndex(modelIndices.get(((ListTableColumn<R, ?>) c).modelColumn));
			}
		}
	}

	@Override
	public void setWidth(int width) {
		if (width == getWidth()) {
			return;
		}
		super.setWidth(width);
	}
}
