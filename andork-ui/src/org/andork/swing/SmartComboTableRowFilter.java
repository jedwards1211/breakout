package org.andork.swing;

import java.util.Collection;

import javax.swing.RowFilter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class SmartComboTableRowFilter extends RowFilter<TableModel, Integer> implements TableModelListener {

	private TableModel currentModel;
	private Collection<? extends RowFilter<TableModel, Integer>> filters;
	private boolean[] filterResults;
	private int filterCount;
	private int row;

	public SmartComboTableRowFilter(Collection<? extends RowFilter<TableModel, Integer>> filters) {
		super();
		this.filters = filters;
	}

	@Override
	public boolean include(javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
		if (filterResults == null || entry.getModel() != currentModel) {
			recompute(entry.getModel());
		}
		int index = entry.getIdentifier();
		return filterResults != null && index >= 0 && index < filterResults.length && filterResults[index];
	}

	private void recompute(TableModel model) {
		if (model != currentModel) {
			if (currentModel != null) {
				currentModel.removeTableModelListener(this);
			}
			currentModel = model;
			currentModel.addTableModelListener(this);
		}
		// filterResults = null;
		filterResults = new boolean[model.getRowCount()];
		filterCount = 0;

		Entry<TableModel, Integer> fakeEntry = new Entry<TableModel, Integer>() {
			@Override
			public Integer getIdentifier() {
				return row;
			}

			@Override
			public TableModel getModel() {
				return model;
			}

			@Override
			public Object getValue(int index) {
				return model.getValueAt(row, index);
			}

			@Override
			public int getValueCount() {
				return model.getColumnCount();
			}
		};

		for (RowFilter<TableModel, Integer> filter : filters) {
			// boolean[] results = new boolean[model.getRowCount()];
			int count = 0;
			// continue condition stops when this filter can't possibly do
			// better
			// than the current best
			for (row = 0; row < model.getRowCount() && count + model.getRowCount() - row - 1 > filterCount; row++) {
				filterResults[row] |= filter.include(fakeEntry);
				// if (results[row] = filter.include(fakeEntry)) {
				// count++;
				// }
			}
			// if (filterResults == null || count > filterCount) {
			// filterResults = results;
			// filterCount = count;
			// }
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		filterResults = null;
	}
}
