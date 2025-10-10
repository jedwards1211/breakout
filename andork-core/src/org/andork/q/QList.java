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
package org.andork.q;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.andork.event.HierarchicalBasicPropertyChangeListener.ChangeType;

public abstract class QList<E, C extends List<E>> extends QCollection<E, C> implements List<E> {
	protected class ListIter implements ListIterator<E> {
		private ListIterator<E> wrapped;
		private E last;

		public ListIter(List<E> list) {
			wrapped = list.listIterator();
		}

		public ListIter(List<E> list, int index) {
			wrapped = list.listIterator(index);
		}

		@Override
		public void add(E e) {
			wrapped.add(e);
			if (e instanceof QElement) {
				((QElement) e).changeSupport().addPropertyChangeListener(propagator);
			}
			changeSupport.fireChildAdded(QList.this, e);
			dependency.fireChanged();
		}

		@Override
		public boolean hasNext() {
			dependency.depend();
			return wrapped.hasNext();
		}

		@Override
		public boolean hasPrevious() {
			dependency.depend();
			return wrapped.hasPrevious();
		}

		@Override
		public E next() {
			dependency.depend();
			return last = wrapped.next();
		}

		@Override
		public int nextIndex() {
			dependency.depend();
			return wrapped.nextIndex();
		}

		@Override
		public E previous() {
			dependency.depend();
			return last = wrapped.previous();
		}

		@Override
		public int previousIndex() {
			dependency.depend();
			return wrapped.previousIndex();
		}

		@Override
		public void remove() {
			wrapped.remove();
			if (last instanceof QElement) {
				((QElement) last).changeSupport().removePropertyChangeListener(propagator);
			}
			changeSupport.fireChildRemoved(QList.this, last);
			dependency.fireChanged();
		}

		@Override
		public void set(E e) {
			if (last != e) {
				wrapped.set(e);
				if (last instanceof QElement) {
					((QElement) last).changeSupport().removePropertyChangeListener(propagator);
				}
				changeSupport.fireChildRemoved(QList.this, last);
				if (e instanceof QElement) {
					((QElement) e).changeSupport().addPropertyChangeListener(propagator);
				}
				changeSupport.fireChildAdded(QList.this, e);
				dependency.fireChanged();
				last = e;
			}
		}
	}

	protected class SubList implements List<E> {
		private List<E> wrapped;

		public SubList(List<E> list, int fromIndex, int toIndex) {
			wrapped = list.subList(fromIndex, toIndex);
		}

		@Override
		public boolean add(E e) {
			if (wrapped.add(e)) {
				if (e instanceof QElement) {
					((QElement) e).changeSupport().addPropertyChangeListener(propagator);
				}
				changeSupport.fireChildAdded(QList.this, e);
				dependency.fireChanged();
				return true;
			}
			return false;
		}

		@Override
		public void add(int index, E element) {
			wrapped.add(index, element);
			if (element instanceof QElement) {
				((QElement) element).changeSupport().addPropertyChangeListener(propagator);
			}
			changeSupport.fireChildAdded(QList.this, element);
			dependency.fireChanged();
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			List<E> added = new ArrayList<E>();
			for (E e : c) {
				if (wrapped.add(e)) {
					if (e instanceof QElement) {
						((QElement) e).changeSupport().addPropertyChangeListener(propagator);
					}
					added.add(e);
				}
			}
			if (!added.isEmpty()) {
				changeSupport.fireChildrenChanged(QList.this, ChangeType.CHILDREN_ADDED, added.toArray());
				dependency.fireChanged();
			}
			return !added.isEmpty();
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			wrapped.addAll(index, c);
			for (E e : c) {
				if (e instanceof QElement) {
					((QElement) e).changeSupport().addPropertyChangeListener(propagator);
				}
			}
			changeSupport.fireChildrenChanged(QList.this, ChangeType.CHILDREN_ADDED, c);
			dependency.fireChanged();
			return true;
		}

		@Override
		public void clear() {
			if (!isEmpty()) {
				Object[] removed = toArray();
				for (E element : wrapped) {
					if (element instanceof QElement) {
						((QElement) element).changeSupport().removePropertyChangeListener(propagator);
					}
				}
				wrapped.clear();
				changeSupport.fireChildrenChanged(QList.this, ChangeType.CHILDREN_REMOVED, removed);
				dependency.fireChanged();
			}
		}

		@Override
		public boolean contains(Object o) {
			dependency.depend();
			return wrapped.contains(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			dependency.depend();
			return wrapped.containsAll(c);
		}

		@Override
		public E get(int index) {
			dependency.depend();
			return wrapped.get(index);
		}

		@Override
		public int indexOf(Object o) {
			dependency.depend();
			return wrapped.indexOf(o);
		}

		@Override
		public boolean isEmpty() {
			dependency.depend();
			return wrapped.isEmpty();
		}

		@Override
		public java.util.Iterator<E> iterator() {
			return new Iter(wrapped);
		}

		@Override
		public int lastIndexOf(Object o) {
			dependency.depend();
			return wrapped.lastIndexOf(o);
		}

		@Override
		public ListIterator<E> listIterator() {
			return new ListIter(this);
		}

		@Override
		public ListIterator<E> listIterator(int index) {
			return new ListIter(this, index);
		}

		@Override
		public E remove(int index) {
			E removed = wrapped.remove(index);
			if (removed instanceof QElement) {
				((QElement) removed).changeSupport().removePropertyChangeListener(propagator);
			}
			changeSupport.fireChildRemoved(QList.this, removed);
			dependency.fireChanged();
			return removed;
		}

		@Override
		public boolean remove(Object o) {
			if (collection.remove(o)) {
				if (o instanceof QElement) {
					((QElement) o).changeSupport().removePropertyChangeListener(propagator);
				}
				changeSupport.fireChildRemoved(QList.this, o);
				dependency.fireChanged();
				return true;
			}
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			List<Object> removed = new ArrayList<Object>();
			for (Object e : c) {
				if (wrapped.remove(e)) {
					if (e instanceof QElement) {
						((QElement) e).changeSupport().removePropertyChangeListener(propagator);
					}
					removed.add(e);
				}
			}
			if (!removed.isEmpty()) {
				changeSupport.fireChildrenChanged(QList.this, ChangeType.CHILDREN_REMOVED, removed.toArray());
				dependency.fireChanged();
			}
			return !removed.isEmpty();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			List<Object> removed = new ArrayList<Object>();
			Iterator<E> iter = iterator();
			while (iter.hasNext()) {
				E e = iter.next();
				if (!c.contains(e)) {
					iter.remove();
					if (e instanceof QElement) {
						((QElement) e).changeSupport().removePropertyChangeListener(propagator);
					}
					removed.add(e);
				}
			}
			if (!removed.isEmpty()) {
				changeSupport.fireChildrenChanged(QList.this, ChangeType.CHILDREN_REMOVED, removed.toArray());
				dependency.fireChanged();
			}
			return !removed.isEmpty();
		}

		@Override
		public E set(int index, E element) {
			E oldValue = wrapped.get(index);
			if (oldValue != element) {
				if (oldValue instanceof QElement) {
					((QElement) oldValue).changeSupport().removePropertyChangeListener(propagator);
				}
				wrapped.set(index, element);
				if (element instanceof QElement) {
					((QElement) element).changeSupport().addPropertyChangeListener(propagator);
				}
				changeSupport.fireChildRemoved(QList.this, oldValue);
				changeSupport.fireChildAdded(QList.this, element);
				dependency.fireChanged();
			}
			return oldValue;
		}

		@Override
		public int size() {
			dependency.depend();
			return wrapped.size();
		}

		@Override
		public List<E> subList(int fromIndex, int toIndex) {
			return new SubList(this, fromIndex, toIndex);
		}

		@Override
		public Object[] toArray() {
			dependency.depend();
			return wrapped.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			dependency.depend();
			return wrapped.toArray(a);
		}
	}

	@Override
	public void add(int index, E element) {
		collection.add(index, element);
		if (element instanceof QElement) {
			((QElement) element).changeSupport().addPropertyChangeListener(propagator);
		}
		changeSupport.fireChildAdded(this, element);
		dependency.fireChanged();
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		collection.addAll(index, c);
		for (E e : c) {
			if (e instanceof QElement) {
				((QElement) e).changeSupport().addPropertyChangeListener(propagator);
			}
		}
		changeSupport.fireChildrenChanged(this, ChangeType.CHILDREN_ADDED, c);
		dependency.fireChanged();
		return true;
	}

	@Override
	public E get(int index) {
		dependency.depend();
		return collection.get(index);
	}

	@Override
	public int indexOf(Object o) {
		dependency.depend();
		return collection.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		dependency.depend();
		return collection.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		dependency.depend();
		return new ListIter(collection);
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		dependency.depend();
		return new ListIter(collection, index);
	}

	@Override
	public E remove(int index) {
		E removed = collection.remove(index);
		if (removed instanceof QElement) {
			((QElement) removed).changeSupport().removePropertyChangeListener(propagator);
		}
		changeSupport.fireChildRemoved(this, removed);
		dependency.fireChanged();
		return removed;
	}

	@Override
	public E set(int index, E element) {
		E oldValue = collection.get(index);
		if (oldValue != element) {
			if (oldValue instanceof QElement) {
				((QElement) oldValue).changeSupport().removePropertyChangeListener(propagator);
			}
			collection.set(index, element);
			if (element instanceof QElement) {
				((QElement) element).changeSupport().addPropertyChangeListener(propagator);
			}
			changeSupport.fireChildRemoved(this, oldValue);
			changeSupport.fireChildAdded(this, element);
			dependency.fireChanged();
		}
		return oldValue;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return new SubList(collection, fromIndex, toIndex);
	}
}
