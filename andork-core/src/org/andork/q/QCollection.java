package org.andork.q;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.andork.event.HierarchicalBasicPropertyChangeListener.ChangeType;

public abstract class QCollection<E, C extends Collection<E>> extends QElement implements Collection<E>
{
	protected final C	collection	= createCollection();

	protected abstract C createCollection();

	public boolean add(E element)
	{
		if (collection.add(element))
		{
			if (element instanceof QElement)
			{
				((QElement) element).changeSupport().addPropertyChangeListener(propagator);
			}
			changeSupport.fireChildAdded(this, element);
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		List<E> added = new ArrayList<E>();
		for (E e : c) {
			if (collection.add(e)) {
				if (e instanceof QElement)
				{
					((QElement) e).changeSupport().addPropertyChangeListener(propagator);
				}
				added.add(e);
			}
		}
		if (!added.isEmpty()) {
			changeSupport.fireChildrenChanged(this, ChangeType.CHILDREN_ADDED, added.toArray());
		}
		return !added.isEmpty();
	}

	public void clear()
	{
		if (!isEmpty()) {
			for (E element : collection)
			{
				if (element instanceof QElement)
				{
					((QElement) element).changeSupport().removePropertyChangeListener(propagator);
				}
			}
			collection.clear();
			changeSupport.fireChildrenChanged(this);
		}
	}

	@Override
	public boolean contains(Object o) {
		return collection.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return collection.containsAll(c);
	}

	public boolean isEmpty()
	{
		return collection.isEmpty();
	}

	public Iterator<E> iterator()
	{
		return new Iter(collection);
	}

	public boolean remove(Object element)
	{
		if (collection.remove(element))
		{
			if (element instanceof QElement)
			{
				((QElement) element).changeSupport().removePropertyChangeListener(propagator);
			}
			changeSupport.fireChildRemoved(this, element);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		List<Object> removed = new ArrayList<Object>();
		for (Object e : c) {
			if (collection.remove(e)) {
				if (e instanceof QElement)
				{
					((QElement) e).changeSupport().removePropertyChangeListener(propagator);
				}
				removed.add(e);
			}
		}
		if (!removed.isEmpty()) {
			changeSupport.fireChildrenChanged(this, ChangeType.CHILDREN_REMOVED, removed.toArray());
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
				if (e instanceof QElement)
				{
					((QElement) e).changeSupport().removePropertyChangeListener(propagator);
				}
				removed.add(e);
			}
		}
		if (!removed.isEmpty()) {
			changeSupport.fireChildrenChanged(this, ChangeType.CHILDREN_REMOVED, removed.toArray());
		}
		return !removed.isEmpty();
	}

	public int size()
	{
		return collection.size();
	}

	@Override
	public Object[] toArray() {
		return collection.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return collection.toArray(a);
	}

	protected class Iter implements Iterator<E>
	{
		Iterator<E>	wrapped;
		E			last;

		public Iter(Collection<E> collection) {
			wrapped = collection.iterator();
		}

		@Override
		public boolean hasNext()
		{
			return wrapped.hasNext();
		}

		@Override
		public E next()
		{
			return last = wrapped.next();
		}

		@Override
		public void remove()
		{
			wrapped.remove();

			if (last instanceof QElement)
			{
				((QElement) last).changeSupport().removePropertyChangeListener(propagator);
			}
			changeSupport.fireChildRemoved(this, last);
		}
	}
}