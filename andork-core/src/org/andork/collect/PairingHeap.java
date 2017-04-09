package org.andork.collect;

import java.util.Map;

public class PairingHeap<K extends Comparable<K>, V> {
	public static class Node<K extends Comparable<K>, V> implements Map.Entry<K, V> {
		private K key;
		private V value;
		Node<K, V> parent;
		Node<K, V> left;
		Node<K, V> right;

		public Node(K key, V value) {
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
		private Node<K, V> merge(Node<K, V> b) {
			Node<K, V> a = this;
			if (b == null) {
				return a;
			}
			if (a.key.compareTo(b.key) >= 0) {
				Node<K, V> swap = a;
				a = b;
				b = swap;
			}
			Node<K, V> al = a.left;
			b.right = al;
			if (al != null) {
				al.parent = b;
			}
			b.parent = a;
			a.left = b;
			return a;
		}

		/**
		 * Used by deleteMin; splays the tree at this node.
		 *
		 * @return this or the node that was moved into its place
		 *
		 *         See Fig.9 in
		 *         http://www.cs.cmu.edu/~sleator/papers/pairing-heaps.pdf. I
		 *         chose the node names according to it.
		 */
		private Node<K, V> link() {
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
			Node<K, V> y = right;
			if (y == null) {
				return this;
			}
			Node<K, V> a = left;
			Node<K, V> b = y.left;
			Node<K, V> c = y.right;
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
				Node<K, V> parent = this.parent;
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

		/**
		 * Removes this node from the heap and rearranges the children to
		 * amortize heap operations.
		 *
		 * @return the node that takes this node's place
		 */
		private Node<K, V> remove() {
			Node<K, V> parent = this.parent;
			boolean wasRight = parent != null && parent.right == this;

			// remove the root node
			Node<K, V> p = this.left;
			this.left = null;
			if (p == null) {
				return null;
			}
			p.parent = null;

			// walk down the right subtrees, calling link on each node.
			// this essentially combines the children of this into pairs.
			Node<K, V> r = p;
			while (r != null) {
				p = r.link();
				r = p.right;
			}
			// walk back up, again calling link on each node.
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
			return p;
		}

		/**
		 * Decreases the key of a descendant of this node.
		 *
		 * @param descendant
		 *            the descendant (may be this node itself).
		 * @param newKey
		 *            the new key for {@code descendant}.
		 * @return the node that takes this node's place.
		 */
		private Node<K, V> decreaseKeyOfDescendant(Node<K, V> descendant, K newKey) {
			int comparison = newKey.compareTo(descendant.key);
			if (comparison > 0) {
				throw new IllegalArgumentException("newKey must be <= p.key");
			}
			if (comparison == 0) {
				return this;
			}
			descendant.key = newKey;

			// remove descendant subtree from its parent
			Node<K, V> prev = descendant.parent;
			Node<K, V> next = descendant.right;
			descendant.parent = null;
			descendant.right = null;
			if (prev != null) {
				prev.right = next;
			}
			if (next != null) {
				next.parent = prev;
			}

			return descendant == this ? this : merge(descendant);
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

	private Node<K, V> root = null;
	private int size = 0;

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return root == null;
	}

	public void clear() {
		root = null;
		size = 0;
	}

	public Node<K, V> insert(K key, V value) {
		Node<K, V> newNode = new Node<>(key, value);
		root = root == null ? newNode : root.merge(newNode);
		size++;
		return newNode;
	}

	public void merge(PairingHeap<K, V> other) {
		root = root == null ? other.root : root.merge(other.root);
		size += other.size;
		other.clear();
	}

	public Node<K, V> findMin() {
		return root;
	}

	public Node<K, V> removeMin() {
		Node<K, V> min = root;
		if (root != null) {
			root = root.remove();
			size--;
		}
		return min;
	}

	public void decreaseKey(Node<K, V> node, K newKey) {
		root = root == null
				? node.decreaseKeyOfDescendant(node, newKey)
				: root.decreaseKeyOfDescendant(node, newKey);
	}
}
