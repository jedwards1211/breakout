/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.swing.table;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class SectionHeaderRowSet {
	private final TreeSet<Integer>	rows	= new TreeSet<Integer>();

	private TableModel				tableModel;
	private SectionHeaderModel		headerModel;
	private ChangeHandler			changeHandler;
	private ChangeListener			listener;

	public SectionHeaderRowSet(TableModel tableModel, SectionHeaderModel headerModel) {
		setTableModel(tableModel);
		setSectionHeaderModel(headerModel);
	}

	public void setTableModel(TableModel newModel) {
		if (tableModel != newModel) {
			if (tableModel != null) {
				tableModel.removeTableModelListener(changeHandler);
				changeHandler = null;
			}

			tableModel = newModel;

			if (tableModel != null) {
				tableModel.addTableModelListener(changeHandler = new ChangeHandler());
			}

			rebuild(0, Integer.MAX_VALUE);
		}
	}

	public void setSectionHeaderModel(SectionHeaderModel newModel) {
		if (headerModel != newModel) {
			headerModel = newModel;
			rebuild(0, Integer.MAX_VALUE);
		}
	}

	public SortedSet<Integer> getSectionHeaderRows() {
		return Collections.unmodifiableSortedSet(rows);
	}
	
	public int getSectionHeaderRowCount() {
		return rows.size();
	}

	private static Integer floor(SortedSet<Integer> set, int value) {
		try {
			return set.headSet(value + 1).last();
		} catch (NoSuchElementException ex) {
			return null;
		}
	}

	public int getSectionHeaderRowFor(int rowIndex) {
		Integer headerRow = floor(rows, rowIndex);
		return headerRow == null ? -1 : headerRow;
	}

	public boolean isSectionHeaderRow(int rowIndex) {
		return headerModel != null && tableModel != null && headerModel.getSectionHeader(rowIndex) != null;
	}

	private void rebuild(int firstRow, int lastRow) {
		int subSetEnd = lastRow;
		if (subSetEnd < Integer.MAX_VALUE) {
			subSetEnd++;
		}
		SortedSet<Integer> oldValues = rows.subSet(firstRow, subSetEnd);
		SortedSet<Integer> newValues = new TreeSet<Integer>();

		if (headerModel != null && tableModel != null) {
			for (int row = firstRow; row <= lastRow && row < tableModel.getRowCount(); row++) {
				if (headerModel.getSectionHeader(row) != null) {
					newValues.add(row);
				}
			}
		}

		if (!newValues.equals(oldValues)) {
			oldValues.clear();
			rows.addAll(newValues);

			if (listener != null) {
				try {
					listener.stateChanged(new ChangeEvent(this));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public ChangeListener getListener() {
		return listener;
	}

	public void setListener(ChangeListener listener) {
		this.listener = listener;
	}

	private class ChangeHandler implements TableModelListener {
		public void tableChanged(TableModelEvent e) {
			if (e.getSource() != tableModel) {
				return;
			}

			if (e.getLastRow() == Integer.MAX_VALUE || e.getColumn() == TableModelEvent.ALL_COLUMNS || e.getFirstRow() == TableModelEvent.HEADER_ROW) {
				rebuild(0, Integer.MAX_VALUE);
			}

			switch (e.getType()) {
			case TableModelEvent.UPDATE:
				rebuild(e.getFirstRow(), e.getLastRow());
				break;
			case TableModelEvent.INSERT:
			case TableModelEvent.DELETE:
				rebuild(e.getFirstRow(), Integer.MAX_VALUE);
				break;
			}
		}
	}
}
