package org.andork.collect;

import java.util.Map;

import org.andork.generic.Ref;

public class PairingHeap<K extends Comparable<K>, V> {
	public static class Entry<K extends Comparable<K>, V> implements Map.Entry<K, V> {
		private K key;
		private V value;
		/**
		 * Using an indirect reference to the heap enables us to make
		 * {@link #remove()} and {@link #decreaseKey(Comparable)} safe while
		 * keeping {@link PairingHeap#clear()} a constant-time operation. All
		 * nodes in the heap share the same {@code heapRef} instance.<br>
		 * <br>
		 * When a node is removed, we set its {@code heapRef} to {@code null},
		 * but when the heap is cleared, we set the shared {@code heapRef.value}
		 * to {@code null}, so that all nodes that were in the heap are
		 * instantly marked as not belonging to a heap anymore.
		 */
		private Ref<PairingHeap<K, V>> heapRef;
		Entry<K, V> parent;
		Entry<K, V> left;
		Entry<K, V> right;

		public Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * Merges two heaps, destructively.
		 *
		 * @param a
		 *            the root of the first heap (may be null to represent an
		 *            empty heap)
		 * @param b
		 *            the root of the second heap (may be null to represent an
		 *            empty heap)
		 * @returns the root of the merged heap
		 */
		private Entry<K, V> merge(Entry<K, V> b) {
			Entry<K, V> a = this;
			if (b == null) {
				return a;
			}
			if (a.key.compareTo(b.key) >= 0) {
				Entry<K, V> swap = a;
				a = b;
				b = swap;
			}
			Entry<K, V> al = a.left;
			b.right = al;
			if (al != null) {
				al.parent = b;
			}
			b.parent = a;
			a.left = b;
			return a;
		}

		/**
		 * Used by deleteMin; splays the tree at this entry.
		 *
		 * @return this or the entry that was moved into its place
		 *
		 *         See Fig.9 in
		 *         http://www.cs.cmu.edu/~sleator/papers/pairing-heaps.pdf. I
		 *         chose the entry names according to it.
		 */
		private Entry<K, V> link() {
			/*
			 * Before:
			 *   parent
			 *    \
			 *    this
			 *    / \
			 *   a   y
			 *      / \
			 *     b   c
			 *
			 * The outcome depends on whether this < y.
			 *
			 * After (this < y):
			 *     parent
			 *      \
			 *      this
			 *      / \
			 *     y   c
			 *    / \
			 *   b   a
			 *
			 * After (y < this):
			 *     parent
			 *      \
			 *       y
			 *      / \
			 *   this  c
			 *    / \
			 *   a   b
			 */
			Entry<K, V> y = right;
			if (y == null) {
				return this;
			}
			Entry<K, V> a = left;
			Entry<K, V> b = y.left;
			Entry<K, V> c = y.right;
			if (this.key.compareTo(y.key) < 0) {
				this.left = y;
				y.parent = this;
				this.right = c;
				if (c != null) {
					c.parent = this;
				}
				y.right = a;
				if (a != null) {
					a.parent = y;
				}
				return this;
			} else {
				Entry<K, V> parent = this.parent;
				y.parent = parent;
				if (parent != null) {
					parent.right = y;
				}
				y.left = this;
				this.parent = y;
				this.right = b;
				if (b != null) {
					b.parent = this;
				}
				return y;
			}
		}

		private void updateHeapAfterRemoval(Entry<K, V> replacement) {
			if (heapRef != null && heapRef.value != null) {
				heapRef.value.size--;
				if (heapRef.value.root == this) {
					heapRef.value.root = replacement;
				}
			}
			heapRef = null;
		}

		/**
		 * Removes this entry from the heap and rearranges the children to
		 * amortize heap operations.
		 *
		 * @return the entry that takes this entry's place
		 */
		public void remove() {
			Entry<K, V> parent = this.parent;
			boolean wasRight = parent != null && parent.right == this;
			Entry<K, V> p = this.left;

			// remove the root entry
			this.left = null;
			if (p == null) {
				updateHeapAfterRemoval(null);
				return;
			}
			p.parent = null;

			// walk down the right subtrees, calling link on each entry.
			// this essentially combines the children of this into pairs.
			Entry<K, V> r = p;
			while (r != null) {
				p = r.link();
				r = p.right;
			}
			// walk back up, again calling link on each entry.
			while (p.parent != null) {
				p = p.parent.link();
			}

			p.parent = parent;
			if (parent != null) {
				if (wasRight) {
					parent.right = p;
				} else {
					parent.left = p;
				}
			}
			updateHeapAfterRemoval(p);
		}

		public void decreaseKey(K newKey) {
			int comparison = newKey.compareTo(key);
			if (comparison > 0) {
				throw new IllegalArgumentException("newKey must be <= p.key");
			}
			if (comparison == 0) {
				return;
			}
			key = newKey;

			// remove descendant subtree from its parent
			Entry<K, V> prev = parent;
			Entry<K, V> next = right;
			parent = null;
			right = null;
			if (prev != null) {
				prev.right = next;
			}
			if (next != null) {
				next.parent = prev;
			}
			if (heapRef != null && heapRef.value != null && this != heapRef.value.root) {
				heapRef.value.root = heapRef.value.root.merge(this);
			}
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			V oldValue = this.value;
			this.value = value;
			return oldValue;
		}
	}

	private Entry<K, V> root = null;
	private int size = 0;

	public boolean isEmpty() {
		return root == null;
	}

	public int size() {
		return size;
	}

	public void clear() {
		if (root != null) {
			root.heapRef.value = null;
			root = null;
			size = 0;
		}
	}

	/**
	 * Inserts the given key-value pair into the heap.
	 *
	 * @return the new {@link Entry}.
	 */
	public Entry<K, V> insert(K key, V value) {
		Entry<K, V> newEntry = new Entry<>(key, value);
		newEntry.heapRef = root == null ? new Ref<>(this) : root.heapRef;
		root = root == null ? newEntry : root.merge(newEntry);
		size++;
		return newEntry;
	}

	/**
	 * Merges another heap into this heap, then clears the other heap.
	 */
	public void merge(PairingHeap<K, V> other) {
		if (other == this || other.root == null) {
			return;
		}
		other.root.heapRef.value = this;
		root = root == null ? other.root : root.merge(other.root);
		size += other.size;
		other.root = null;
		other.size = 0;
	}

	/**
	 * @return The {@link Entry} with the minimum {@link Entry#getKey() key}.
	 */
	public Entry<K, V> findMin() {
		return root;
	}

	/**
	 * Removes and returns the {@link Entry} with the minimum
	 * {@link Entry#getKey() key}.
	 *
	 * @return The entry (with the minimum {@link Entry#getKey() key}) that was
	 *         removed.
	 */
	public Entry<K, V> removeMin() {
		Entry<K, V> min = root;
		if (root != null) {
			root.remove();
		}
		return min;
	}
}
