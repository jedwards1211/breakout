package org.andork.swing.table;

import java.awt.Component;
import java.util.function.Function;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.andork.swing.selector.DefaultSelector;

@SuppressWarnings("serial")
public class DefaultSelectorCellEditor extends AbstractCellEditor implements TableCellEditor {
	/**
	 *
	 */
	private static final long serialVersionUID = 5303051204006059858L;
	private final DefaultSelector selector;
	private final Function<Object, Object> valueToSelection;
	private final Function<Object, Object> selectionToValue;

	public DefaultSelectorCellEditor(DefaultSelector<?> selector, Function<Object, Object> valueToSelection,
			Function<Object, Object> selectionToValue) {
		super();
		this.selector = selector;
		this.valueToSelection = valueToSelection;
		this.selectionToValue = selectionToValue;
	}

	public DefaultSelectorCellEditor(Function<Object, Object> valueToSelection,
			Function<Object, Object> selectionToValue) {
		this(new DefaultSelector<>(), valueToSelection, selectionToValue);
	}

	@Override
	public Object getCellEditorValue() {
		return selectionToValue.apply(selector.getSelection());
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
			int column) {
		selector.setSelection(valueToSelection.apply(value));
		return selector.comboBox();
	}

	public DefaultSelector selector() {
		return selector;
	}
}
