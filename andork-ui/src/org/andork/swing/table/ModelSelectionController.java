/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.swing.table;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class ModelSelectionController implements ListSelectionListener, RowSorterListener, TableModelListener {
	JTable table;

	ListSelectionModel viewSelectionModel;
	ListSelectionModel modelSelectionModel;

	public ModelSelectionController(JTable table, ListSelectionModel modelSelectionModel) {
		this.table = table;
		viewSelectionModel = table.getSelectionModel();
		this.modelSelectionModel = modelSelectionModel;

		table.getModel().addTableModelListener(this);
		viewSelectionModel.addListSelectionListener(this);
	}

	@Override
	public void sorterChanged(RowSorterEvent e) {

	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.INSERT) {
			modelSelectionModel.insertIndexInterval(e.getFirstRow(), e.getLastRow() - e.getFirstRow() + 1, true);
		} else if (e.getType() == TableModelEvent.DELETE) {
			modelSelectionModel.removeIndexInterval(e.getFirstRow(), e.getLastRow());
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		for (int viewIndex = Math.max(0, e.getFirstIndex()); viewIndex <= Math.min(table.getRowCount() - 1,
				e.getLastIndex()); viewIndex++) {
			int modelIndex = table.convertRowIndexToModel(viewIndex);

			if (!viewSelectionModel.isSelectedIndex(viewIndex)) {
				modelSelectionModel.removeSelectionInterval(modelIndex, modelIndex);
			} else {
				modelSelectionModel.addSelectionInterval(modelIndex, modelIndex);
			}
		}

		int anchor = viewSelectionModel.getAnchorSelectionIndex();
		if (anchor >= 0) {
			anchor = table.convertRowIndexToModel(anchor);
		}
		modelSelectionModel.setAnchorSelectionIndex(anchor);

		int lead = viewSelectionModel.getLeadSelectionIndex();
		if (lead >= 0) {
			lead = table.convertRowIndexToModel(lead);
		}
		modelSelectionModel.setLeadSelectionIndex(lead);

		modelSelectionModel.setValueIsAdjusting(viewSelectionModel.getValueIsAdjusting());
	}
}
