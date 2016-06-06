package org.andork.swing.list;

import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.andork.event.HierarchicalBasicPropertyChangeListener;
import org.andork.q.QList;

public class QListListModel<E> implements ListModel<E>, HierarchicalBasicPropertyChangeListener {
	QList<? extends E, ?> wrapped;
	final EventListenerList listeners = new EventListenerList();

	public QListListModel(QList<? extends E, ?> list) {
		this.wrapped = list;
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		boolean wasEmpty = listeners.getListenerCount() == 0;
		listeners.add(ListDataListener.class, l);
		if (wasEmpty) {
			wrapped.changeSupport().addPropertyChangeListener(this);
		}
	}

	@Override
	public void childrenChanged(Object source, ChangeType changeType, Object... children) {
		for (ListDataListener l : listeners.getListeners(ListDataListener.class)) {
			l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
		}
	}

	@Override
	public E getElementAt(int index) {
		return wrapped.get(index);
	}

	@Override
	public int getSize() {
		return wrapped.size();
	}

	@Override
	public void propertyChange(Object source, Object property, Object oldValue, Object newValue, int index) {
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(ListDataListener.class, l);
		if (listeners.getListenerCount() == 0) {
			wrapped.changeSupport().removePropertyChangeListener(this);
		}
	}
}
