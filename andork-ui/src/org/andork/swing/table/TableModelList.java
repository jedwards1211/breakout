package org.andork.swing.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

/**
 * A list of row objects designed specifically to back a {@link TableModel}.
 * <br>
 * <br>
 * The difference from {@link java.util.List} is that the available modification
 * methods have been reduced to a subset that are convenient for a table to
 * handle, and modifications fire events to registered
 * {@link TableModelList.Listener Listener}s. (However, since there is no
 * uniform way to listen for changes in the row element objects, when you change
 * a row you should call {@link #fireElementsUpdated(int, int)} manually.)
 *
 * @author James
 *
 * @param <E>
 *            the type of elements used for the rows.
 */
public class TableModelList<E> implements Iterable<E> {
	private class Iter implements Iterator<E> {
		ListIterator<E> wrapped;
		int lastIndex;

		public Iter() {
			wrapped = elements.listIterator();
		}

		@Override
		public boolean hasNext() {
			return wrapped.hasNext();
		}

		@Override
		public E next() {
			lastIndex = wrapped.nextIndex();
			return wrapped.next();
		}

		@Override
		public void remove() {
			wrapped.remove();
			fireElementsDeleted(lastIndex, lastIndex);
		}
	}

	/**
	 * A listener for a {@link TableModelList}.
	 *
	 * @author James
	 *
	 * @param <E>
	 *            the {@link TableModelList}'s row element type.
	 */
	public static interface Listener<E> {
		/**
		 * Indicates the row elements present in {@code list} and their internal
		 * values have drastically changed.
		 */
		public void dataChanged(TableModelList<E> list);

		/**
		 * Indicates row elements were deleted from {@code list} at
		 * {@code fromIndex} to {@code toIndex} (inclusive).
		 */
		public void elementsDeleted(TableModelList<E> list, int fromIndex, int toIndex);

		/**
		 * Indicates row elements were inserted into {@code list} at
		 * {@code fromIndex} to {@code toIndex} (inclusive).
		 */
		public void elementsInserted(TableModelList<E> list, int fromIndex, int toIndex);

		/**
		 * Indicates row elements were in {@code list} at {@code fromIndex} to
		 * {@code toIndex} (inclusive) were updated.
		 */
		public void elementsUpdated(TableModelList<E> list, int fromIndex, int toIndex);

		/**
		 * Indicates the structure of row elements in {@code list} (e.g. that
		 * would determine which columns to show) has changed, as well which row
		 * elements are present.
		 */
		public void structureChanged(TableModelList<E> list);
	}

	private final List<E> elements = new ArrayList<E>();

	private final List<Listener<E>> listeners = new ArrayList<>();

	public void add(E element) {
		add(elements.size(), element);
	}

	public void add(int index, E element) {
		elements.add(index, element);
		fireElementsInserted(index, index);
	}

	public void addAll(Collection<? extends E> elements) {
		addAll(elements.size(), elements);
	}

	public void addAll(int index, Collection<? extends E> elements) {
		this.elements.addAll(index, elements);
		fireElementsInserted(index, index + elements.size() - 1);
	}

	public void addListener(Listener<E> listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void clear() {
		if (!isEmpty()) {
			int size = elements.size();
			elements.clear();
			fireElementsDeleted(0, size - 1);
		}
	}

	public void fireDataChanged() {
		for (Listener<E> listener : listeners) {
			listener.dataChanged(this);
		}
	}

	protected void fireElementsDeleted(int fromIndex, int toIndex) {
		for (Listener<E> listener : listeners) {
			listener.elementsDeleted(this, fromIndex, toIndex);
		}
	}

	protected void fireElementsInserted(int fromIndex, int toIndex) {
		for (Listener<E> listener : listeners) {
			listener.elementsInserted(this, fromIndex, toIndex);
		}
	}

	public void fireElementsUpdated(int fromIndex, int toIndex) {
		for (Listener<E> listener : listeners) {
			listener.elementsUpdated(this, fromIndex, toIndex);
		}
	}

	public void fireStructureChanged() {
		for (Listener<E> listener : listeners) {
			listener.structureChanged(this);
		}
	}

	public E get(int index) {
		return elements.get(index);
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return new Iter();
	}

	public void remove(E element) {
		int index = elements.indexOf(element);
		if (index >= 0) {
			remove(index);
		}
	}

	public void remove(int index) {
		elements.remove(index);
		fireElementsDeleted(index, index);
	}

	public void removeListener(Listener<E> listener) {
		listeners.remove(listener);
	}

	/**
	 * Removes a sublist of elements from this list and fires an
	 * {@link Listener#elementsDeleted(TableModelList, int, int)
	 * elementsDeleted} event.
	 *
	 * @param fromIndex
	 *            low endpoint (inclusive) of the sublist to remove
	 * @param toIndex
	 *            high endpoint (exclusive) of the sublist to remove (note that
	 *            the event will use an <i>inclusive</i> toIndex to conform to
	 *            {@link TableModelEvent} semantics!)
	 */
	public void removeSublist(int fromIndex, int toIndex) {
		elements.subList(fromIndex, toIndex).clear();
		if (fromIndex < toIndex) {
			fireElementsDeleted(fromIndex, toIndex - 1);
		}
	}

	public void set(int index, E element) {
		E previous = elements.set(index, element);
		if (!previous.equals(element)) {
			fireElementsUpdated(index, index);
		}
	}

	public int size() {
		return elements.size();
	}
}
