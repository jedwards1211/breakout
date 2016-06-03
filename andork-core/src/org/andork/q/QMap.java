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

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.andork.model.Model;
import org.andork.util.Java7;

public abstract class QMap<K, V, C extends Map<K, V>> extends QElement implements Map<K, V>, Model {
	private class EntryIterator extends HashIterator<Map.Entry<K, V>> {
		@Override
		public Map.Entry<K, V> next() {
			return nextEntry();
		}
	}

	private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
		@Override
		public void clear() {
			QMap.this.clear();
		}

		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			Map.Entry<K, V> e = (Map.Entry<K, V>) o;
			V value = QMap.this.get(e.getKey());
			return Java7.Objects.equals(value, e.getValue());
		}

		@Override
		public Iterator<Map.Entry<K, V>> iterator() {
			return new EntryIterator();
		}

		@Override
		public boolean remove(Object o) {
			if (o instanceof Map.Entry) {
				if (contains(o)) {
					QMap.this.remove(((Map.Entry) o).getKey());
					return true;
				}
			}
			return false;
		}

		@Override
		public int size() {
			return QMap.this.size();
		}
	}

	private abstract class HashIterator<E> implements Iterator<E> {
		Iterator<Map.Entry<K, V>> entryIter = map.entrySet().iterator();
		Map.Entry<K, V> last;

		@Override
		public boolean hasNext() {
			return entryIter.hasNext();
		}

		public Map.Entry<K, V> nextEntry() {
			return last = entryIter.next();
		}

		@Override
		public void remove() {
			QMap.this.remove(last.getKey());
		}
	}

	private class KeyIterator extends HashIterator<K> {
		@Override
		public K next() {
			return nextEntry().getKey();
		}
	}

	private final class KeySet extends AbstractSet<K> {
		@Override
		public void clear() {
			QMap.this.clear();
		}

		@Override
		public boolean contains(Object o) {
			return containsKey(o);
		}

		@Override
		public Iterator<K> iterator() {
			return new KeyIterator();
		}

		@Override
		public boolean remove(Object o) {
			boolean removed = containsKey(o);
			QMap.this.remove(o);
			return removed;
		}

		@Override
		public int size() {
			return QMap.this.size();
		}
	}

	private class ValueIterator extends HashIterator<V> {
		@Override
		public V next() {
			return nextEntry().getValue();
		}
	}

	private final class Values extends AbstractCollection<V> {
		@Override
		public void clear() {
			QMap.this.clear();
		}

		@Override
		public boolean contains(Object o) {
			return containsValue(o);
		}

		@Override
		public Iterator<V> iterator() {
			return new ValueIterator();
		}

		@Override
		public int size() {
			return QMap.this.size();
		}
	}

	protected final C map = createMap();

	transient volatile Set<K> keySet = null;

	transient volatile Collection<V> values = null;

	transient volatile Set<Map.Entry<K, V>> entrySet = null;

	@Override
	public void clear() {
		Map<K, V> clone = new LinkedHashMap<K, V>(map);
		map.clear();
		for (Map.Entry<K, V> entry : clone.entrySet()) {
			if (entry.getValue() != null) {
				changeSupport.firePropertyChange(this, entry.getKey(), entry.getValue(), null);
			}
		}
		clearChildren();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	protected abstract C createMap();

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		Set<java.util.Map.Entry<K, V>> es = entrySet;
		return es != null ? es : (entrySet = new EntrySet());
	}

	@Override
	public V get(Object key) {
		return map.get(key);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		Set<K> ks = keySet;
		return ks != null ? ks : (keySet = new KeySet());
	}

	@Override
	public V put(K key, V value) {
		V prev = map.put(key, value);
		if (prev != value) {
			if (prev != null) {
				removeChild(prev);
			}
			if (value != null) {
				addChild(value);
			}
			changeSupport.firePropertyChange(this, key, prev, value);
		}
		return prev;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		List<Object> added = new ArrayList<Object>();
		List<Object> removed = new ArrayList<Object>();

		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			K key = entry.getKey();
			V value = entry.getValue();
			V prev = map.put(key, value);
			if (prev != null) {
				removed.add(prev);
			}
			if (value != null) {
				added.add(prev);
			}
			if (prev != value) {
				changeSupport.firePropertyChange(this, key, prev, value);
			}
		}

		removeChildren(removed);
		addChildren(added);
	}

	@Override
	public V remove(Object key) {
		V prev = map.remove(key);
		if (prev != null) {
			removeChild(prev);
			changeSupport.firePropertyChange(this, key, prev, null);
		}
		return prev;
	}

	@Override
	public void set(Object key, Object newValue) {
		put((K) key, (V) newValue);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<V> values() {
		Collection<V> vs = values;
		return vs != null ? vs : (values = new Values());
	}
}
