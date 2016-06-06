package org.andork.swing.list;

import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.andork.event.HierarchicalBasicPropertyChangeAdapter;
import org.andork.q.QList;

public class QListListModel<E> implements ListModel<E> {
	QList<? extends E, ?> wrapped;
	final EventListenerList listeners = new EventListenerList();

	public QListListModel(QList<? extends E, ?> list) {
		this.wrapped = list;
		this.wrapped.changeSupport().addPropertyChangeListener(new HierarchicalBasicPropertyChangeAdapter() {
			@Override
			public void childrenChanged(Object source, ChangeType changeType, Object... children) {
				for (ListDataListener l : listeners.getListeners(ListDataListener.class)) {
					l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
				}
			}
		});
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(ListDataListener.class, l);
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
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(ListDataListener.class, l);
	}
}
