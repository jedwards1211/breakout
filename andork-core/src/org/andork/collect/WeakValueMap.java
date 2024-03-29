package org.andork.collect;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

public class WeakValueMap<K, V> implements Map<K, V> {
	private class EntryIterator extends HashIterator<Map.Entry<K, V>> {
		@Override
		public Map.Entry<K, V> next() {
			return nextEntry();
		}
	}

	private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
		@Override
		public void clear() {
			WeakValueMap.this.clear();
		}

		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
			V value = get(e.getKey());
			return Objects.equals(value, e.getValue());
		}

		@Override
		public Iterator<Map.Entry<K, V>> iterator() {
			return new EntryIterator();
		}

		@Override
		public boolean remove(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
			V value = get(e.getKey());
			if (Objects.equals(value, e.getValue())) {
				remove(e.getKey());
				return true;
			}
			return false;
		}

		@Override
		public int size() {
			return WeakValueMap.this.size();
		}
	}

	private abstract class HashIterator<T> implements Iterator<T> {
		private Iterator<Entry<K, WeakReference<V>>> mIterator = m.entrySet().iterator();

		private TempEntry lastReturned;
		private TempEntry nextEntry;

		HashIterator() {
		}

		@Override
		public boolean hasNext() {
			while (nextEntry == null) {
				if (!mIterator.hasNext()) {
					return false;
				}

				Entry<K, WeakReference<V>> next = mIterator.next();
				V value = next.getValue().get();

				if (value != null) {
					nextEntry = new TempEntry(next.getKey(), value);
				}
			}
			return true;
		}

		/** The common parts of next() across different types of iterators */
		protected Entry<K, V> nextEntry() {
			if (nextEntry == null && !hasNext()) {
				throw new NoSuchElementException();
			}
			lastReturned = nextEntry;
			nextEntry = null;
			return lastReturned;
		}

		@Override
		public void remove() {
			if (lastReturned == null) {
				throw new IllegalStateException();
			}

			mIterator.remove();
			lastReturned = null;
		}
	}

	private static class KeyedReference<K, V> extends WeakReference<V> {
		final K key;

		public KeyedReference(K key, V referent, ReferenceQueue<V> queue) {
			super(referent, queue);
			this.key = key;
		}
	}

	private class KeyIterator extends HashIterator<K> {
		@Override
		public K next() {
			return nextEntry().getKey();
		}
	}

	private class KeySet extends AbstractSet<K> {
		@Override
		public void clear() {
			WeakValueMap.this.clear();
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
			if (containsKey(o)) {
				WeakValueMap.this.remove(o);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public int size() {
			return WeakValueMap.this.size();
		}
	}

	@SuppressWarnings("serial")
	private class TempEntry extends SimpleEntry<K, V> {
		/**
		 *
		 */
		private static final long serialVersionUID = 4687738547274027050L;

		public TempEntry(K key, V value) {
			super(key, value);
		}

		public TempEntry(Map.Entry<K, V> entry) {
			super(entry);
		}

		@Override
		public V setValue(V value) {
			V result = super.setValue(value);
			put(getKey(), value);
			return result;
		}
	}

	private class ValueIterator extends HashIterator<V> {
		@Override
		public V next() {
			return nextEntry().getValue();
		}
	}

	private class Values extends AbstractCollection<V> {
		@Override
		public void clear() {
			WeakValueMap.this.clear();
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
			return WeakValueMap.this.size();
		}
	}

	public static <K, V> WeakValueMap<K, V> newWeakValueHashMap() {
		return new WeakValueMap<>(() -> new HashMap<>());
	}

	public static <K, V> WeakValueMap<K, V> newWeakValueLinkedHashMap() {
		return new WeakValueMap<>(() -> new LinkedHashMap<>());
	}

	public static <K, V> WeakValueMap<K, V> newWeakValueTreeMap() {
		return new WeakValueMap<>(() -> new TreeMap<>());
	}

	private final Supplier<? extends Map<K, WeakReference<V>>> mapSupplier;

	private final Map<K, WeakReference<V>> m;

	private final ReferenceQueue<V> queue = new ReferenceQueue<>();

	private transient KeySet keySet = null;

	private transient Values values;

	private transient EntrySet entrySet = null;

	public WeakValueMap(Supplier<? extends Map<K, WeakReference<V>>> mapSupplier) {
		super();
		this.mapSupplier = mapSupplier;
		this.m = mapSupplier.get();
	}

	@Override
	public void clear() {
		// clear out ref queue. We don't need to expunge entries
		// since table is getting cleared.
		while (queue.poll() != null) {
			;
		}

		m.clear();

		// Allocation of array may have caused GC, which may have caused
		// additional entries to go stale. Removing these entries from the
		// reference queue will make them eligible for reclamation.
		while (queue.poll() != null) {
			;
		}
	}

	@Override
	public boolean containsKey(Object key) {
		return m.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		for (Object v : values()) {
			if (Objects.equals(v, value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		Set<Entry<K, V>> es = entrySet;
		return es != null ? es : (entrySet = new EntrySet());
	}

	private void expungeStaleEntries() {
		for (Reference<? extends V> x; (x = queue.poll()) != null;) {
			@SuppressWarnings("unchecked")
			KeyedReference<K, V> r = (KeyedReference<K, V>) x;

			if (m.get(r.key) == r) {
				m.remove(r.key);
			}
		}
	}

	@Override
	public V get(Object key) {
		WeakReference<V> ref = m.get(key);
		return ref == null ? null : ref.get();
	}

	@Override
	public boolean isEmpty() {
		expungeStaleEntries();
		return m.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		Set<K> ks = keySet;
		return ks != null ? ks : (keySet = new KeySet());
	}

	@Override
	public V put(K key, V value) {
		if (value == null) {
			throw new IllegalArgumentException("value must be non-null");
		}
		V oldValue = get(key);
		m.put(key, new KeyedReference<K, V>(key, value, queue));
		return oldValue;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		Map<K, WeakReference<V>> tempMap = mapSupplier.get();

		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}
			tempMap.put(entry.getKey(), new KeyedReference<K, V>(entry.getKey(), entry.getValue(), queue));
		}

		this.m.putAll(tempMap);
	}

	@Override
	public V remove(Object key) {
		WeakReference<V> ref = m.remove(key);
		return ref == null ? null : ref.get();
	}

	@Override
	public int size() {
		expungeStaleEntries();
		return m.size();
	}

	@Override
	public Collection<V> values() {
		Collection<V> vs = values;
		return vs != null ? vs : (values = new Values());
	}
}
