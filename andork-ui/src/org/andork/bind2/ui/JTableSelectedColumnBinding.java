package org.andork.bind2.ui;

import javax.swing.JTable;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class JTableSelectedColumnBinding implements Binding {
	public final Link<Integer> columnLink = new Link<Integer>(this);
	public final JTable table;

	public JTableSelectedColumnBinding(JTable table) {
		super();
		this.table = table;
	}

	@Override
	public void update(boolean force) {
		Integer column = columnLink.get();
		if (column == null || column < 0) {
			table.clearSelection();
		} else {
			table.getColumnModel().getSelectionModel().setSelectionInterval(column, column);
		}
	}
}
