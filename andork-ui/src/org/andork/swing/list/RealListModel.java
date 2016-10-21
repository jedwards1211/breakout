package org.andork.swing.list;

import java.util.AbstractList;
import java.util.Collection;
import java.util.EventListener;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * A {@link List} wrapper that implements {@link ListModel} and fires
 * {@link ListDataEvent}s whenever it's modified. Currently the implementation
 * is simple to avoid bugs, and not optimized for {@link #removeAll(Collection)}
 * , {@link #retainAll(Collection)}. Also, it requires the wrapped list to be
 * {@link RandomAccess}, since otherwise performance would be horrible, and it
 * would be a bad backing model for a UI list anyway.
 *
 * @param <E>
 *            the type of elements in the list.
 */
public class RealListModel<E> extends AbstractList<E> implements ListModel<E> {
	List<E> list;
	protected EventListenerList listenerList = new EventListenerList();

	public RealListModel(List<E> list) {
		if (!(list instanceof RandomAccess)) {
			throw new IllegalArgumentException("list must be a RandomAccess");
		}
		this.list = list;
	}

	@Override
	public void add(int index, E element) {
		list.add(index, element);
		fireIntervalAdded(index, index);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		if (list.addAll(index, c)) {
			fireIntervalAdded(index, index + c.size() - 1);
			return true;
		}
		return false;
	}

	/**
	 * Adds a listener to the list that's notified each time a change to the
	 * data model occurs.
	 *
	 * @param l
	 *            the <code>ListDataListener</code> to be added
	 */
	@Override
	public void addListDataListener(ListDataListener l) {
		listenerList.add(ListDataListener.class, l);
	}

	/**
	 * <code>AbstractListModel</code> subclasses must call this method
	 * <b>after</b> one or more elements of the list change. The changed
	 * elements are specified by the closed interval index0, index1 -- the
	 * endpoints are included. Note that index0 need not be less than or equal
	 * to index1.
	 *
	 * @param source
	 *            the <code>ListModel</code> that changed, typically "this"
	 * @param index0
	 *            one end of the new interval
	 * @param index1
	 *            the other end of the new interval
	 * @see EventListenerList
	 * @see DefaultListModel
	 */
	protected void fireContentsChanged(int index0, int index1) {
		Object[] listeners = listenerList.getListenerList();
		ListDataEvent e = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				if (e == null) {
					e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index0, index1);
				}
				((ListDataListener) listeners[i + 1]).contentsChanged(e);
			}
		}
	}

	/**
	 * <code>AbstractListModel</code> subclasses must call this method
	 * <b>after</b> one or more elements are added to the model. The new
	 * elements are specified by a closed interval index0, index1 -- the
	 * enpoints are included. Note that index0 need not be less than or equal to
	 * index1.
	 *
	 * @param source
	 *            the <code>ListModel</code> that changed, typically "this"
	 * @param index0
	 *            one end of the new interval
	 * @param index1
	 *            the other end of the new interval
	 * @see EventListenerList
	 * @see DefaultListModel
	 */
	protected void fireIntervalAdded(int index0, int index1) {
		Object[] listeners = listenerList.getListenerList();
		ListDataEvent e = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				if (e == null) {
					e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index0, index1);
				}
				((ListDataListener) listeners[i + 1]).intervalAdded(e);
			}
		}
	}

	/**
	 * <code>AbstractListModel</code> subclasses must call this method
	 * <b>after</b> one or more elements are removed from the model.
	 * <code>index0</code> and <code>index1</code> are the end points of the
	 * interval that's been removed. Note that <code>index0</code> need not be
	 * less than or equal to <code>index1</code>.
	 *
	 * @param source
	 *            the <code>ListModel</code> that changed, typically "this"
	 * @param index0
	 *            one end of the removed interval, including <code>index0</code>
	 * @param index1
	 *            the other end of the removed interval, including
	 *            <code>index1</code>
	 * @see EventListenerList
	 * @see DefaultListModel
	 */
	protected void fireIntervalRemoved(int index0, int index1) {
		Object[] listeners = listenerList.getListenerList();
		ListDataEvent e = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				if (e == null) {
					e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index0, index1);
				}
				((ListDataListener) listeners[i + 1]).intervalRemoved(e);
			}
		}
	}

	@Override
	public E get(int index) {
		return list.get(index);
	}

	@Override
	public E getElementAt(int index) {
		return list.get(index);
	}

	/**
	 * Returns an array of all the list data listeners registered on this
	 * <code>AbstractListModel</code>.
	 *
	 * @return all of this model's <code>ListDataListener</code>s, or an empty
	 *         array if no list data listeners are currently registered
	 *
	 * @see #addListDataListener
	 * @see #removeListDataListener
	 *
	 * @since 1.4
	 */
	public ListDataListener[] getListDataListeners() {
		return listenerList.getListeners(ListDataListener.class);
	}

	/**
	 * Returns an array of all the objects currently registered as
	 * <code><em>Foo</em>Listener</code>s upon this model. <code><em>Foo</em>
	 * Listener</code>s are registered using the <code>add<em>Foo</em>
	 * Listener</code> method.
	 * <p>
	 * You can specify the <code>listenerType</code> argument with a class
	 * literal, such as <code><em>Foo</em>Listener.class</code>. For example,
	 * you can query a list model <code>m</code> for its list data listeners
	 * with the following code:
	 *
	 * <pre>
	 * ListDataListener[] ldls = (ListDataListener[]) (m.getListeners(ListDataListener.class));
	 * </pre>
	 *
	 * If no such listeners exist, this method returns an empty array.
	 *
	 * @param listenerType
	 *            the type of listeners requested; this parameter should specify
	 *            an interface that descends from
	 *            <code>java.util.EventListener</code>
	 * @return an array of all objects registered as <code><em>Foo</em>
	 *         Listener</code>s on this model, or an empty array if no such
	 *         listeners have been added
	 * @exception ClassCastException
	 *                if <code>listenerType</code> doesn't specify a class or
	 *                interface that implements
	 *                <code>java.util.EventListener</code>
	 *
	 * @see #getListDataListeners
	 *
	 * @since 1.3
	 */
	public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
		return listenerList.getListeners(listenerType);
	}

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public E remove(int index) {
		E removed = list.remove(index);
		fireIntervalRemoved(index, index);
		return removed;
	}

	/**
	 * Removes a listener from the list that's notified each time a change to
	 * the data model occurs.
	 *
	 * @param l
	 *            the <code>ListDataListener</code> to be removed
	 */
	@Override
	public void removeListDataListener(ListDataListener l) {
		listenerList.remove(ListDataListener.class, l);
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		ListIterator<E> it = list.listIterator(fromIndex);
		for (int i = 0, n = toIndex - fromIndex; i < n; i++) {
			it.next();
			it.remove();
		}
		if (toIndex > fromIndex) {
			fireIntervalRemoved(fromIndex, toIndex - 1);
		}
	}

	@Override
	public E set(int index, E element) {
		E replaced = list.set(index, element);
		fireContentsChanged(index, index);
		return replaced;
	}

	@Override
	public int size() {
		return list.size();
	}

}
