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

import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterEvent.Type;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.andork.swing.AnnotatingRowSorter;

/**
 * A {@link JTable} with the following added features:
 * <ul>
 * <li>It does a perfect job of maintaining the row selections when row
 * visibility/order changes.
 * <li>You can {@linkplain #getModelSelectionModel() get} and
 * {@linkplain #setModelSelectionModel(ListSelectionModel) set} the model
 * selection model; {@code BetterJTable} will update the model selection model
 * when the {@linkplain #getSelectionModel() view selection model} changes, and
 * vice versa.
 * </ul>
 *
 *
 * @author Andy
 */
@SuppressWarnings("serial")
public class BetterJTable extends JTable {
	/**
	 *
	 */
	private static final long serialVersionUID = 7877994145179355425L;
	protected ListSelectionModel modelSelectionModel = null;
	protected boolean ignoreModelSelectionChanges = false;
	protected boolean ignoreViewSelectionChanges = false;

	public BetterJTable() {
		super();
		init();
	}

	public BetterJTable(int numRows, int numColumns) {
		super(numRows, numColumns);
		init();
	}

	public BetterJTable(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
		init();
	}

	public BetterJTable(TableModel dm) {
		super(dm);
		init();
	}

	public BetterJTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
		init();
	}

	public BetterJTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
		init();
	}

	public BetterJTable(Vector rowData, Vector columnNames) {
		super(rowData, columnNames);
		init();
	}

	/**
	 * @return the {@link ListSelectionModel} of selected model indices.
	 *         {@code BetterJTable} will automatically keep it in sync with the
	 *         view selection model.
	 */
	public ListSelectionModel getModelSelectionModel() {
		return modelSelectionModel;
	}

	protected void init() {
		setModelSelectionModel(new DefaultListSelectionModel());
	}

	protected void rebuildModelSelectionModel() {
		ListSelectionModel viewSelectionModel = getSelectionModel();
		if (viewSelectionModel == null || modelSelectionModel == null) {
			return;
		}

		ignoreModelSelectionChanges = true;

		try {
			try {
				modelSelectionModel.setValueIsAdjusting(true);

				if (modelSelectionModel.getMinSelectionIndex() >= 0) {
					modelSelectionModel.removeSelectionInterval(modelSelectionModel.getMinSelectionIndex(),
							modelSelectionModel.getMaxSelectionIndex());
				}

				int index0 = -1;
				int viewIndex, modelIndex;

				for (modelIndex = 0; modelIndex < getRowCount(); modelIndex++) {
					viewIndex = convertRowIndexToView(modelIndex);

					if (viewSelectionModel.isSelectedIndex(viewIndex)) {
						if (index0 < 0) {
							index0 = modelIndex;
						}
					} else if (index0 >= 0) {
						modelSelectionModel.addSelectionInterval(index0, modelIndex - 1);
						index0 = -1;
					}
				}

				if (index0 >= 0) {
					modelSelectionModel.addSelectionInterval(index0, modelIndex - 1);
				}
			} finally {
				modelSelectionModel.setValueIsAdjusting(viewSelectionModel.getValueIsAdjusting());
			}
		} finally {
			ignoreModelSelectionChanges = false;
		}
	}

	protected void rebuildViewSelectionModel() {
		ignoreViewSelectionChanges = true;
		try {
			ListSelectionModel viewSelectionModel = getSelectionModel();
			if (viewSelectionModel == null || modelSelectionModel == null) {
				return;
			}

			try {
				viewSelectionModel.setValueIsAdjusting(true);

				if (viewSelectionModel.getMinSelectionIndex() >= 0) {
					viewSelectionModel.removeSelectionInterval(viewSelectionModel.getMinSelectionIndex(),
							viewSelectionModel.getMaxSelectionIndex());
				}

				int index0 = -1;
				int modelIndex, viewIndex;

				for (viewIndex = 0; viewIndex < getRowCount(); viewIndex++) {
					modelIndex = convertRowIndexToModel(viewIndex);

					if (modelSelectionModel.isSelectedIndex(modelIndex)) {
						if (index0 < 0) {
							index0 = viewIndex;
						}
					} else if (index0 >= 0) {
						viewSelectionModel.addSelectionInterval(index0, viewIndex - 1);
						index0 = -1;
					}
				}

				if (index0 >= 0) {
					viewSelectionModel.addSelectionInterval(index0, viewIndex - 1);
				}
			} finally {
				viewSelectionModel.setValueIsAdjusting(modelSelectionModel.getValueIsAdjusting());
			}
		} finally {
			ignoreViewSelectionChanges = false;
		}
	}

	@Override
	public void setModel(TableModel dataModel) {
		if (dataModel != getModel()) {
			ignoreViewSelectionChanges = true;
			try {
				super.setModel(dataModel);
			} finally {
				ignoreViewSelectionChanges = false;
			}
			rebuildViewSelectionModel();
		}
	}

	/**
	 * Sets the {@link ListSelectionModel} of selected model indices. The view
	 * selection model will be immediately updated to match the new model
	 * selection model.
	 *
	 * @return the {@link ListSelectionModel} of selected model indices.
	 *         {@code BetterJTable} will automatically keep it in sync with the
	 *         view selection model.
	 */
	public void setModelSelectionModel(ListSelectionModel newModel) {
		if (modelSelectionModel != newModel) {
			if (modelSelectionModel != null) {
				modelSelectionModel.removeListSelectionListener(this);
			}
			modelSelectionModel = newModel;
			rebuildViewSelectionModel();
			if (newModel != null) {
				newModel.addListSelectionListener(this);
			}
		}
	}

	@Override
	public void setRowSorter(RowSorter<? extends TableModel> sorter) {
		if (sorter != getRowSorter()) {
			ignoreViewSelectionChanges = true;
			try {
				super.setRowSorter(sorter);
			} finally {
				ignoreViewSelectionChanges = false;
			}
			rebuildViewSelectionModel();
		}
	}

	@Override
	public void setSelectionModel(ListSelectionModel newModel) {
		if (newModel != getSelectionModel()) {
			ignoreViewSelectionChanges = true;
			try {
				super.setSelectionModel(newModel);
				rebuildModelSelectionModel();
			} finally {
				ignoreViewSelectionChanges = false;
			}
		}
	}

	@Override
	public void sorterChanged(RowSorterEvent e) {
		ignoreViewSelectionChanges = true;
		try {
			super.sorterChanged(e);
		} finally {
			ignoreViewSelectionChanges = false;
		}
		updateViewSelectionModel(e);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		ignoreViewSelectionChanges = true;
		try {
			super.tableChanged(e);
			updateModelSelectionModel(e);
		} finally {
			ignoreViewSelectionChanges = false;
		}
	}

	protected void updateModelSelectionModel(ListSelectionEvent e) {
		if (modelSelectionModel == null) {
			return;
		}

		ignoreModelSelectionChanges = true;
		try {
			try {
				modelSelectionModel.setValueIsAdjusting(true);

				ListSelectionModel selectionModel = (ListSelectionModel) e.getSource();

				for (int viewIndex = Math.max(0, e.getFirstIndex()); viewIndex <= Math.min(getRowCount() - 1,
						e.getLastIndex()); viewIndex++) {
					int modelIndex = convertRowIndexToModel(viewIndex);

					if (selectionModel.isSelectedIndex(viewIndex)) {
						modelSelectionModel.addSelectionInterval(modelIndex, modelIndex);
					} else {
						modelSelectionModel.removeSelectionInterval(modelIndex, modelIndex);
					}
				}
			} finally {
				modelSelectionModel.setValueIsAdjusting(e.getValueIsAdjusting());
			}
		} finally {
			ignoreModelSelectionChanges = false;
		}
	}

	protected void updateModelSelectionModel(TableModelEvent e) {
		if (modelSelectionModel == null) {
			return;
		}

		switch (e.getType()) {
		case TableModelEvent.UPDATE:
			if (e.getFirstRow() == 0 && e.getLastRow() == Integer.MAX_VALUE
					&& e.getColumn() == TableModelEvent.ALL_COLUMNS) {
				modelSelectionModel.clearSelection();
			}
			break;
		case TableModelEvent.INSERT:
			modelSelectionModel.insertIndexInterval(e.getFirstRow(), e.getLastRow(), true);
			break;
		case TableModelEvent.DELETE:
			modelSelectionModel.removeIndexInterval(e.getFirstRow(), e.getLastRow());
			break;
		}
	}

	protected void updateViewSelectionModel(ListSelectionEvent e) {
		ListSelectionModel viewSelectionModel = getSelectionModel();
		if (viewSelectionModel == null) {
			return;
		}

		ignoreViewSelectionChanges = true;
		try {
			try {
				viewSelectionModel.setValueIsAdjusting(true);

				ListSelectionModel selectionModel = (ListSelectionModel) e.getSource();

				for (int modelIndex = Math.max(0, e.getFirstIndex()); modelIndex <= Math
						.min(getModel().getRowCount() - 1, e.getLastIndex()); modelIndex++) {
					int viewIndex = convertRowIndexToView(modelIndex);

					if (selectionModel.isSelectedIndex(modelIndex)) {
						viewSelectionModel.addSelectionInterval(viewIndex, viewIndex);
					} else {
						viewSelectionModel.removeSelectionInterval(viewIndex, viewIndex);
					}
				}
			} finally {
				viewSelectionModel.setValueIsAdjusting(e.getValueIsAdjusting());
			}
		} finally {
			ignoreViewSelectionChanges = false;
		}
	}

	protected void updateViewSelectionModel(RowSorterEvent e) {
		if (e.getType() == Type.SORTED) {
			rebuildViewSelectionModel();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == getSelectionModel()) {
			super.valueChanged(e);

			if (!e.getValueIsAdjusting() && !ignoreViewSelectionChanges) {
				updateModelSelectionModel(e);
			}
		} else if (e.getSource() == modelSelectionModel) {
			if (!e.getValueIsAdjusting() && !ignoreModelSelectionChanges) {
				if (getRowSorter() instanceof AnnotatingRowSorter
						&& ((AnnotatingRowSorter) getRowSorter()).isSortingInBackground()) {
					return;
				}
				updateViewSelectionModel(e);
			}
		}
	}

}
