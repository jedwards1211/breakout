package org.andork.tracker.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.andork.tracker.Dependency;

public class TrackableList<E> {
	final List<E> list;
	final Dependency dep = new Dependency();

	public TrackableList(List<E> list) {
		this.list = Objects.requireNonNull(list);
	}

	public void add(E elem) {
		dep.changed();
		list.add(elem);
	}

	public void add(int index, E elem) {
		list.add(index, elem);
		dep.changed();
	}

	public void addAll(Collection<? extends E> collection) {
		if (list.addAll(collection)) {
			dep.changed();
		}
	}

	public void addAll(int index, Collection<? extends E> collection) {
		if (list.addAll(index, collection)) {
			dep.changed();
		}
	}

	public void clear() {
		if (!list.isEmpty()) {
			dep.changed();
		}
		list.clear();
	}

	public boolean contains(Object elem) {
		dep.depend();
		return list.contains(elem);
	}

	public boolean containsAll(Collection<?> collection) {
		dep.depend();
		return list.containsAll(collection);
	}

	@Override
	public boolean equals(Object o) {
		dep.depend();
		return list.equals(o);
	}

	public E get(int index) {
		dep.depend();
		return list.get(index);
	}

	@Override
	public int hashCode() {
		dep.depend();
		return list.hashCode();
	}

	public int indexOf(Object elem) {
		dep.depend();
		return list.indexOf(elem);
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public int lastIndexOf(Object elem) {
		dep.depend();
		return list.lastIndexOf(elem);
	}

	public void remove(int index) {
		list.remove(index);
		dep.changed();
	}

	public void remove(Object elem) {
		if (list.remove(elem)) {
			dep.changed();
		}
	}

	public void removeAll(Collection<?> collection) {
		if (list.removeAll(collection)) {
			dep.changed();
		}
	}

	public void retainAll(Collection<?> collection) {
		if (list.retainAll(collection)) {
			dep.changed();
		}
	}

	public void set(int index, E element) {
		dep.depend();
		list.set(index, element);
	}

	public int size() {
		dep.depend();
		return list.size();
	}

	public Object[] toArray() {
		dep.depend();
		return list.toArray();
	}

	public E[] toArray(E[] result) {
		dep.depend();
		return list.toArray(result);
	}
}
