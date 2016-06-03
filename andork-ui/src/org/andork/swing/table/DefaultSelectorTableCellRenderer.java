package org.andork.swing.table;

import java.awt.Component;
import java.util.function.Function;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.andork.swing.selector.DefaultSelector;

public class DefaultSelectorTableCellRenderer implements TableCellRenderer {
	private final DefaultSelector selector;
	private final Function<Object, Object> valueToSelection;

	public DefaultSelectorTableCellRenderer(DefaultSelector<?> selector, Function<Object, Object> valueToSelection) {
		super();
		this.selector = selector;
		this.valueToSelection = valueToSelection;
	}

	public DefaultSelectorTableCellRenderer(Function<Object, Object> valueToSelection) {
		this(new DefaultSelector<>(), valueToSelection);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		selector.setSelection(valueToSelection.apply(value));
		return selector.comboBox();
	}

	public DefaultSelector selector() {
		return selector;
	}
}
