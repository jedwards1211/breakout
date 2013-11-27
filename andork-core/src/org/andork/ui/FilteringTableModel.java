package org.andork.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.andork.util.Java16PlusMethods;

/**
 * Provides an automatically-updated filtered view of a backing table model. The
 * filter can be any implementation of the {@link Filter} interface.<br>
 * <br>
 * 
 * If more than a set number of rows have to be filtered at once, the filtering
 * will be performed on a background thread to keep from tying up the Swing
 * thread. In this case, the filtered data will be temporarily out-of-date, but
 * {@link #isRebuildingInBackground()} will return {@code true}.<br>
 * <br>
 * 
 * You can make the table display a wait cursor when background filtering is
 * occuring using a {@link TableModelListener}:
 * 
 * <pre>
 * filteringTableModel.addTableModelListener(new TableModelListener() {
 * 	public void tableChanged(TableModelEvent e) {
 * 		Cursor cursor = null;
 * 		if (filteringTableModel.isRebuildingInBackground()) {
 * 			cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
 * 		}
 * 		table.setCursor(cursor);
 * 	}
 * });
 * </pre>
 * 
 * @author james.a.edwards
 */
@SuppressWarnings("serial")
public class FilteringTableModel extends AbstractTableModel {
	/**
	 * Determines which rows a {@link FilteringTableModel} will include from its
	 * backing {@link TableModel}.
	 * 
	 * @author andy.edwards
	 */
	public static interface Filter {
		/**
		 * @param row
		 *            a row of data copied from the backing {@link TableModel}.
		 * @return if the row should be included, any non-null {@code Object}.
		 *         This object may represent info about the filter result such
		 *         as where exactly a regular expression matched. If the row
		 *         should not be included, the implementation should return
		 *         {@code null}.
		 */
		public Object include(Object[] row);
	}

	/**
	 * A filter that includes all rows whose {@code value.toString()} in a
	 * specified {@code column} (or any column) contains a match for a specified
	 * {@code pattern} (the pattern does not have to match the entire value).
	 * 
	 * @author james.a.edwards
	 */
	public static class PatternFilter implements Filter {
		/**
		 * Creates a filter that includes all rows whose
		 * {@code value.toString()} in the specified {@code column} contains a
		 * match for the specified {@code pattern} (the pattern does not have to
		 * match the entire value).
		 * 
		 * @param pattern
		 *            the filter pattern.
		 * @param column
		 *            the column to look for matches in.
		 */
		public PatternFilter(Pattern pattern, int column) {
			this.pattern = pattern;
			this.column = column;
		}

		/**
		 * Creates a filter that includes all rows whose
		 * {@code value.toString()} in any column contains a match for the
		 * specified {@code pattern} (the pattern does not have to match the
		 * entire value).
		 * 
		 * @param pattern
		 *            the filter pattern.
		 * @param column
		 *            the column to look for matches in.
		 */
		public PatternFilter(Pattern pattern) {
			this.pattern = pattern;
		}

		private Pattern	pattern;
		private Integer	column;

		public Pattern getPattern() {
			return pattern;
		}

		public Integer getColumn() {
			return column;
		}

		public Object include(Object[] row) {
			if (column != null) {
				Object value = row[column];
				return value != null
						&& pattern.matcher(value.toString()).find() ? Boolean.TRUE : null;
			} else {
				StringBuffer combined = new StringBuffer();
				for (int column = 0; column < row.length; column++) {
					Object value = row[column];
					if (value != null) {
						combined.append(value.toString());
					}
					if (column < row.length - 1) {
						combined.append('\t');
					}
				}
				return pattern.matcher(combined.toString()).find() ? Boolean.TRUE : null;
			}
		}

		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (o instanceof PatternFilter) {
				PatternFilter f = (PatternFilter) o;
				return Java16PlusMethods.equals(column, f.column)
						&& pattern.equals(f.pattern);
			}
			return false;
		}
	}

	public static class HighlightingFilterResult {
		HighlightingFilterResult(Object mainFilterResult,
				Filter highlightingFilter, Object highlightingFilterResult) {
			super();
			this.mainFilterResult = mainFilterResult;
			this.highlightingFilter = highlightingFilter;
			this.highlightingFilterResult = highlightingFilterResult;
		}

		public final Object	mainFilterResult;
		public final Filter	highlightingFilter;
		public final Object	highlightingFilterResult;
	}

	/**
	 * A filter that applies a delegate filter to determine which rows to
	 * include, and marks whether included rows passed one of a list of
	 * highlighting filters. This is so that a table can highlight rows that
	 * passed a given filter. It will indicate if a row passed any highlighting
	 * filter via a {@link HighlightingFilterResult} for that row.
	 * 
	 * @author james.a.edwards
	 */
	public static class HighlightingFilter implements Filter {
		public HighlightingFilter(Filter mainFilter, Filter... highlightingFilters) {
			super();
			this.mainFilter = mainFilter;
			this.highlightingFilters = highlightingFilters;
		}

		private Filter		mainFilter;
		private Filter[]	highlightingFilters;

		public Object include(Object[] row) {
			Object mainFilterResult = null;
			if (mainFilter != null) {
				mainFilterResult = mainFilter.include(row);
			}
			if (mainFilter == null || mainFilterResult != null) {
				Filter highlightingFilter = null;
				Object highlightingFilterResult = null;

				for (int i = 0; i < highlightingFilters.length; i++) {
					highlightingFilterResult = highlightingFilters[i]
							.include(row);
					if (highlightingFilterResult != null) {
						highlightingFilter = highlightingFilters[i];
						break;
					}
				}

				return new HighlightingFilterResult(mainFilterResult,
						highlightingFilter, highlightingFilterResult);
			}
			return null;
		}
	}

	/**
	 * Creates a filter that only includes rows that match each filter in an
	 * array.
	 * 
	 * @author james.a.edwards
	 */
	public static class AndFilter implements Filter {
		/**
		 * Creates a filter that only includes rows that match each filter in
		 * {@code filters}.
		 * 
		 * @param filters
		 *            the filters to use.
		 */
		public AndFilter(Filter[] filters) {
			this.filters = filters;
		}

		public AndFilter(List<Filter> filters) {
			this.filters = (Filter[]) filters
					.toArray(new Filter[filters.size()]);
		}

		private Filter[]	filters;

		public Object[] include(Object[] row) {
			Object[] filterResult = new Object[filters.length];
			for (int i = 0; i < filters.length; i++) {
				filterResult[i] = filters[i].include(row);
				if (filterResult[i] == null) {
					return null;
				}
			}
			return filterResult;
		}

		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (o instanceof AndFilter) {
				AndFilter f = (AndFilter) o;
				if (filters.length != f.filters.length) {
					return false;
				}
				for (int i = 0; i < filters.length; i++) {
					if (!filters[i].equals(f.filters[i])) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
	}

	public FilteringTableModel(TableModel backingModel) {
		this(backingModel, null);
	}

	public FilteringTableModel(TableModel backingModel, Filter filter) {
		super();
		setBackingModel(backingModel);
		setFilter(filter);
	}

	// NOTE: To guarantee thread safety, all FilteringTableModel
	// instance variables should only be accessed on the Swing
	// thread!

	private TableModel				backingModel;
	private Filter					filter;
	private BaseModelListener		backingModelListener;

	private boolean					backingModelChanged				= false;
	private Thread					rebuildThread					= null;

	private String[]				columnNames;
	private Class<?>[]				columnClasses;

	/**
	 * an ArrayList of Object[]s representing the rows
	 */
	private ArrayList<Object[]>		rows							= new ArrayList<Object[]>();
	/**
	 * an ArrayList of filter results for each row
	 */
	private ArrayList<Object>		filterResults					= new ArrayList<Object>();
	/**
	 * an ArrayList of Integers representing the indices the included rows in
	 * the backing model.
	 */
	private ArrayList<Integer>		backingRowIndices				= new ArrayList<Integer>();

	private int						smallChangeLimit				= 1000;

	private final Queue<Runnable>	invokeWhenDoneRebuildingQueue	= new LinkedList<Runnable>();

	public void setBackingModel(TableModel backingModel) {
		if (this.backingModel != backingModel) {
			if (this.backingModel != null) {
				this.backingModel
						.removeTableModelListener(backingModelListener);
				backingModelListener = null;
			}
			this.backingModel = backingModel;
			if (backingModel != null) {
				backingModelListener = new BaseModelListener();
				this.backingModel.addTableModelListener(backingModelListener);
			}
			rebuildLater();
		}
	}

	public TableModel getBackingModel() {
		return backingModel;
	}

	public void setFilter(Filter filter) {
		if (!Java16PlusMethods.equals(this.filter, filter)) {
			this.filter = filter;
			rebuildLater();
		}
	}

	public Filter getFilter() {
		return filter;
	}

	public int getRowCount() {
		return rows.size();
	}

	public int getColumnCount() {
		return columnNames == null ? 0 : columnNames.length;
	}

	public int convertRowIndexToBackingModel(int rowIndex) {
		return ((Integer) backingRowIndices.get(rowIndex)).intValue();
	}

	public Object getFilterResultForRow(int rowIndex) {
		return filterResults.get(rowIndex);
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return ((Object[]) rows.get(rowIndex))[columnIndex];
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (isRebuildingInBackground()) {
			return false;
		}
		return backingModel.isCellEditable(
				((Integer) backingRowIndices.get(rowIndex)).intValue(),
				columnIndex);
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (isRebuildingInBackground()) {
			throw new UnsupportedOperationException();
		}
		backingModel.setValueAt(aValue,
				((Integer) backingRowIndices.get(rowIndex)).intValue(),
				columnIndex);
		rebuildLater();
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}

	public Class<?> getColumnClass(int columnIndex) {
		return columnClasses[columnIndex];
	}

	public boolean isRebuildingInBackground() {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Must be called from the EDT");
		}
		return rebuildThread != null;
	}

	/**
	 * Makes {@code FilteringTableModel} start rebuilding the filtered results
	 * on a background thread. If a rebuild is currently in progress, it will be
	 * aborted and rebuilding will start over. (The rebuilt results will still
	 * be inserted into the table on the EDT.) This method returns immediately;
	 * it does not block until the rebuilding is complete.
	 * 
	 * @throws IllegalStateException
	 *             if not called on the EDT
	 */
	public void rebuildLater() {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Must be called from the EDT");
		}
		backingModelChanged = true;
		if (rebuildThread == null) {
			rebuildThread = new Thread(new Rebuilder());
			rebuildThread.setName(getClass().getSimpleName() + " Rebuilder");
			rebuildThread.start();
		}
	}

	/**
	 * Makes {@code FilteringTableModel} rebuild the filtered results
	 * immediately on the calling thread (which must be the EDT). This method
	 * does not return until the rebuilding is complete.
	 * 
	 * @throws IllegalStateException
	 *             if not called on the EDT
	 */
	public void rebuildAndWait() {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Must be called from the EDT");
		}
		backingModelChanged = true;
		new Rebuilder().run();

		// Careful! If the backing model changes and this method is run
		// while a background rebuilder is running, it could cause the
		// background rebuilder to install obsolete data when it
		// finishes! So just force it to start over so it will intall
		// the most up-to-date data.
		if (isRebuildingInBackground()) {
			backingModelChanged = true;
		}
	}

	/**
	 * Queues the given {@link Runnable} to be invoked later when the background
	 * thread has finished rebuilding the filtered results. If the filtered
	 * results are up-to-date and no background thread is running, the
	 * {@code Runnable} will be invoked immediately on the EDT, and this method
	 * will not return until it returns.
	 * 
	 * @param r
	 *            the {@link Runnable} to invoke
	 * 
	 * @throws IllegalStateException
	 *             if not called on the EDT
	 */
	public void invokeWhenDoneRebuilding(Runnable r) {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Must be called from the EDT");
		}
		if (isRebuildingInBackground()) {
			invokeWhenDoneRebuildingQueue.add(r);
		} else {
			r.run();
		}
	}

	private class BaseModelListener implements TableModelListener {
		public void tableChanged(TableModelEvent e) {
			if (e.getType() == TableModelEvent.UPDATE
					&& e.getLastRow() - e.getFirstRow() <= smallChangeLimit) {
				handleSmallUpdate(e);
			} else if (e.getType() == TableModelEvent.INSERT
					&& e.getLastRow() == backingModel.getRowCount() - 1
					&& e.getLastRow() - e.getFirstRow() <= smallChangeLimit) {
				handleSmallAppend(e);
			} else if (backingModel.getRowCount() <= smallChangeLimit) {
				rebuildAndWait();
			} else {
				rebuildLater();
			}
		}

		protected void handleSmallAppend(TableModelEvent e) {
			int firstFilteredRow = rows.size();

			for (int originalRow = e.getFirstRow(); originalRow <= e
					.getLastRow(); originalRow++) {
				Object[] row = new Object[backingModel.getColumnCount()];
				for (int column = 0; column < backingModel.getColumnCount(); column++) {
					row[column] = backingModel.getValueAt(originalRow, column);
				}
				Object filterResult = null;
				if (filter != null) {
					filterResult = filter.include(row);
				}
				if (filter == null || filterResult != null) {
					rows.add(row);
					filterResults.add(filterResult);
					backingRowIndices.add(new Integer(originalRow));
				}
			}

			int lastFilteredRow = rows.size() - 1;

			if (lastFilteredRow >= firstFilteredRow) {
				fireTableRowsInserted(firstFilteredRow, lastFilteredRow);
			}
		}

		protected void handleSmallUpdate(TableModelEvent e) {
			for (int originalRow = e.getLastRow(); originalRow >= e
					.getFirstRow(); originalRow--) {
				int filterIndex = Collections.binarySearch(backingRowIndices,
						new Integer(originalRow));

				if (filterIndex >= 0) {
					Object[] row = new Object[backingModel.getColumnCount()];
					for (int column = 0; column < backingModel.getColumnCount(); column++) {
						row[column] = backingModel.getValueAt(originalRow,
								column);
					}

					Object filterResult = null;
					if (filter != null) {
						filterResult = filter.include(row);
					}
					if (filter == null || filterResult != null) {
						rows.set(filterIndex, row);
						filterResults.set(filterIndex, filterResult);
					} else {
						rows.remove(filterIndex);
						filterResults.remove(filterIndex);
						backingRowIndices.remove(filterIndex);
					}
				}
			}
			fireTableDataChanged();
		}
	}

	private class PreRebuild implements Runnable {
		String[]	newColumnNames;
		Class<?>[]	newColumnClasses;
		Filter		filter;

		public void run() {
			filter = FilteringTableModel.this.filter;
			if (backingModel != null) {
				newColumnNames = new String[backingModel.getColumnCount()];
				newColumnClasses = new Class[backingModel.getColumnCount()];
				for (int column = 0; column < backingModel.getColumnCount(); column++) {
					newColumnNames[column] = backingModel.getColumnName(column);
					newColumnClasses[column] = backingModel
							.getColumnClass(column);
				}
			}
			backingModelChanged = false;
		}
	}

	private class RowCopier implements Runnable {
		RowCopier(ArrayList<Object[]> newRows, int numRows) {
			super();
			this.newRows = newRows;
			this.numRows = numRows;
		}

		ArrayList<Object[]>	newRows;
		int					numRows;
		boolean				backingModelChanged;
		boolean				complete;

		public void run() {
			if (backingModel != null) {
				int endRow = Math.min(newRows.size() + numRows,
						backingModel.getRowCount());
				for (int row = newRows.size(); row < endRow; row++) {
					Object[] newRow = new Object[backingModel.getColumnCount()];
					for (int column = 0; column < backingModel.getColumnCount(); column++) {
						newRow[column] = backingModel.getValueAt(row, column);
					}
					newRows.add(newRow);
				}
				complete = newRows.size() == backingModel.getRowCount();
			} else {
				complete = true;
			}
			backingModelChanged = FilteringTableModel.this.backingModelChanged;
		}
	}

	private class PostRebuild implements Runnable {
		PostRebuild(String[] newColumnNames, Class<?>[] newColumnClasses,
				ArrayList<Object[]> newRows, ArrayList<Object> newFilterResults,
				ArrayList<Integer> newBackingRowIndices) {
			super();
			this.newColumnNames = newColumnNames;
			this.newColumnClasses = newColumnClasses;
			this.newRows = newRows;
			this.newFilterResults = newFilterResults;
			this.newBackingRowIndices = newBackingRowIndices;
		}

		boolean				backingModelChanged;
		ArrayList<Integer>	newBackingRowIndices;
		ArrayList<Object>	newFilterResults;
		ArrayList<Object[]>	newRows;
		String[]			newColumnNames;
		Class<?>[]			newColumnClasses;

		public void run() {
			backingModelChanged = FilteringTableModel.this.backingModelChanged;
			if (!backingModelChanged) {
				rebuildThread = null;
			}
			columnNames = newColumnNames;
			columnClasses = newColumnClasses;
			rows = newRows;
			filterResults = newFilterResults;
			backingRowIndices = newBackingRowIndices;
			fireTableDataChanged();

			if (!backingModelChanged) {
				while (!invokeWhenDoneRebuildingQueue.isEmpty()) {
					try {
						invokeWhenDoneRebuildingQueue.poll().run();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	private class Rebuilder implements Runnable {
		public void run() {
			// NOTE: To guarantee thread safety, all FilteringTableModel
			// instance variables should only be accessed on the Swing
			// thread!

			boolean backingModelChanged;

			do {
				backingModelChanged = false;

				// copy the filter, column names and classes and clear the
				// backingModelChanged flag (the instance variable, not the
				// local variable) on the EDT.

				PreRebuild preRebuild = new PreRebuild();
				doSwing(preRebuild);

				// Copy the rows from backingModel in chunks on the EDT, so we
				// don't tie it up. If the backing model is changed during this
				// process, start over again.

				ArrayList<Object[]> newRows = new ArrayList<Object[]>();
				int k = 0;
				RowCopier rowCopier = new RowCopier(newRows, 100);
				while (!rowCopier.complete) {
					doSwing(rowCopier);

					if (rowCopier.backingModelChanged) {
						break;
					}
					if (k++ == 0) {
						doSwing(new Runnable() {
							public void run() {
								fireTableDataChanged();
							}
						});
					}
				}
				if (rowCopier.backingModelChanged) {
					backingModelChanged = true;
					continue;
				}

				// Now we have a coherent copy of the backing model, and we can
				// filter it.

				ArrayList<Integer> newBackingRowIndices = new ArrayList<Integer>();
				ArrayList<Object> newFilterResults = new ArrayList<Object>();

				for (int row = newRows.size() - 1; row >= 0; row--) {
					Object filterResult = null;
					if (preRebuild.filter != null) {
						filterResult = preRebuild.filter
								.include((Object[]) newRows.get(row));
						if (filterResult != null) {
							newFilterResults.add(0, filterResult);
						}
					} else {
						newFilterResults.add(0, null);
					}
					if (preRebuild.filter == null || filterResult != null) {
						newBackingRowIndices.add(0, new Integer(row));
					} else {
						newRows.remove(row);
					}
				}

				// Install the filtered data on the EDT, and check if the
				// backing
				// model has been changed again.

				PostRebuild postRebuild = new PostRebuild(
						preRebuild.newColumnNames, preRebuild.newColumnClasses,
						newRows, newFilterResults, newBackingRowIndices);
				doSwing(postRebuild);
				backingModelChanged = postRebuild.backingModelChanged;

				// if the backing model changed after all the data was copied,
				// start over again.

			} while (backingModelChanged);
		}
	}

	private void doSwing(Runnable r) {
		if (SwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
