package org.andork.collect;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StringTrieMap<T> {
	private Map<Character, StringTrieMap<T>>	children;
	private Set<T>								values;

	public void put(String s, T value) {
		put(s, 0, value);
	}

	private synchronized void put(String s, int index, T value) {
		if (index == s.length()) {
			if (values == null) {
				values = new HashSet<T>();
			}
			values.add(value);
		} else {
			char nextChar = s.charAt(index);
			if (children == null) {
				children = new HashMap<Character, StringTrieMap<T>>();
			}
			StringTrieMap<T> child = children.get(nextChar);
			if (child == null) {
				child = new StringTrieMap<T>();
				children.put(nextChar, child);
			}
			child.put(s, index + 1, value);
		}
	}

	public void get(String s, Collection<? super T> result) {
		get(s, 0, result);
	}

	public Set<T> get(String s) {
		Set<T> result = new HashSet<T>();
		get(s, 0, result);
		return result;
	}

	private synchronized void get(String s, int index, Collection<? super T> result) {
		if (index == s.length()) {
			get(result);
		} else {
			char nextChar = s.charAt(index);
			if (children != null) {
				if (nextChar == '*') {
					for (StringTrieMap<T> child : children.values()) {
						child.get(s, index, result);
					}
					get(s, index + 1, result);
				}
				else {
					StringTrieMap<T> child = children.get(nextChar);
					if (child != null) {
						child.get(s, index + 1, result);
					}
				}
			}
		}
	}

	private void get(Collection<? super T> result) {
		if (values != null) {
			result.addAll(values);
		}
		if (children != null) {
			for (StringTrieMap<T> child : children.values()) {
				child.get(result);
			}
		}
	}

	public void get(String s, Visitor<? super T> visitor) {
		get(s, 0, visitor);
	}

	private synchronized boolean get(String s, int index, Visitor<? super T> visitor) {
		if (index == s.length()) {
			if (!get(visitor)) {
				return false;
			}
		} else {
			char nextChar = s.charAt(index);
			if (children != null) {
				if (nextChar == '*') {
					for (StringTrieMap<T> child : children.values()) {
						if (!child.get(s, index, visitor)) {
							return false;
						}
					}
					if (!get(s, index + 1, visitor)) {
						return false;
					}
				} else if (nextChar == '?') {
					for (StringTrieMap<T> child : children.values()) {
						if (!child.get(s, index + 1, visitor)) {
							return false;
						}
					}
				}
				else {
					StringTrieMap<T> child = children.get(nextChar);
					if (child != null) {
						if (!child.get(s, index + 1, visitor)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	private boolean get(Visitor<? super T> visitor) {
		if (values != null) {
			for (T value : values) {
				if (!visitor.visit(value)) {
					return false;
				}
			}
		}
		if (children != null) {
			for (StringTrieMap<T> child : children.values()) {
				if (!child.get(visitor)) {
					return false;
				}
			}
		}
		return true;
	}
}
