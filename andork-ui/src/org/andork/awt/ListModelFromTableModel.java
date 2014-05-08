package org.andork.awt;

import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * A ListModel that is based upon a {@link TableModel}. It will listen to the
 * model for {@link TableModelEvent}s and fire corresponding
 * {@link ListDataEvent}s. By default {@link #getElementAt(int)} just returns
 * values in column 0 of the model, but derived classes may override it to
 * return other values.
 * 
 * @author andy.edwards
 */
public class ListModelFromTableModel implements ListModel {
	protected TableModel		tableModel;

	/** List of listeners */
	protected EventListenerList	listenerList	= new EventListenerList();

	protected ChangeHandler		changeHandler	= new ChangeHandler();

	public ListModelFromTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
		tableModel.addTableModelListener(changeHandler);
	}

	public void destroy() {
		tableModel.removeTableModelListener(changeHandler);
		tableModel = null;
	}

	@Override
	public int getSize() {
		return tableModel.getRowCount();
	}

	@Override
	public Object getElementAt(int index) {
		return tableModel.getValueAt(index, 0);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listenerList.add(ListDataListener.class, l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listenerList.remove(ListDataListener.class, l);
	}

	public ListDataListener[] getListDataListeners() {
		return (ListDataListener[]) listenerList.getListeners(
				ListDataListener.class);
	}

	protected void fireContentsChanged(int index0, int index1) {
		ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index0, index1);
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				ListDataListener listener = (ListDataListener) listeners[i + 1];
				listener.contentsChanged(event);
			}
		}
	}

	protected void fireIntervalAdded(int index0, int index1) {
		ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index0, index1);
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				ListDataListener listener = (ListDataListener) listeners[i + 1];
				listener.contentsChanged(event);
			}
		}
	}

	protected void fireIntervalRemoved(int index0, int index1) {
		ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index0, index1);
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				ListDataListener listener = (ListDataListener) listeners[i + 1];
				listener.contentsChanged(event);
			}
		}
	}

	protected class ChangeHandler implements TableModelListener {
		@Override
		public void tableChanged(TableModelEvent e) {
			if (tableModel == null) {
				return;
			}
			switch (e.getType()) {
			case TableModelEvent.INSERT:
				fireIntervalAdded(e.getFirstRow(), e.getLastRow());
				break;
			case TableModelEvent.UPDATE:
				fireContentsChanged(0, Integer.MAX_VALUE);
				break;
			case TableModelEvent.DELETE:
				fireIntervalRemoved(e.getFirstRow(), e.getLastRow());
				break;
			}
		}
	}
}
