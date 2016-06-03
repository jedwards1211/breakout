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
package org.andork.collect;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StringTrieMap<T> {
	private Map<Character, StringTrieMap<T>> children;
	private Set<T> values;

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

	public Set<T> get(String s) {
		Set<T> result = new HashSet<T>();
		get(s, 0, result);
		return result;
	}

	public void get(String s, Collection<? super T> result) {
		get(s, 0, result);
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
				} else {
					StringTrieMap<T> child = children.get(nextChar);
					if (child != null) {
						child.get(s, index + 1, result);
					}
				}
			}
		}
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
				} else {
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

	public void get(String s, Visitor<? super T> visitor) {
		get(s, 0, visitor);
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

	public void put(String s, T value) {
		put(s, 0, value);
	}
}
