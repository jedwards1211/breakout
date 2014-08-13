package org.andork.bind2.ui;

import javax.swing.JTable;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class JTableSelectedRowBinding implements Binding {
	public final Link<Integer>	rowLink	= new Link<Integer>(this);
	public final JTable			table;

	public JTableSelectedRowBinding(JTable table) {
		super();
		this.table = table;
	}

	public void update(boolean force) {
		Integer row = rowLink.get();
		if (row == null || row < 0) {
			table.clearSelection();
		} else {
			table.getSelectionModel().setSelectionInterval(row, row);
		}
	}
}
