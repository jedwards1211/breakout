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
package org.andork.spatial;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RTraversal {
	/**
	 * Searches down through the nodes with the best (lowest) score at every
	 * level, and returns the node with the best score found.
	 */
	public static <R, T, C extends Comparable<C>> RNode<R, T> traverse(RNode<R, T> node,
			Function<RNode<R, T>, C> getScore) {
		if (node instanceof RBranch) {
			RNode<R, T> bestChild = null;
			C bestScore = null;
			RBranch<R, T> branch = (RBranch<R, T>) node;
			for (int i = 0; i < branch.numChildren(); i++) {
				RNode<R, T> child = branch.childAt(i);
				C score = getScore.apply(child);
				if (bestChild == null || score.compareTo(bestScore) < 0) {
					bestChild = child;
					bestScore = score;
				}
			}
			if (bestChild != null) {
				RNode<R, T> result = traverse(bestChild, getScore);
				return getScore.apply(result).compareTo(getScore.apply(node)) <= 0 ? result : node;
			}
		}
		return node;
	}

	public static <R, T, V> V traverse(RNode<R, T> node, Predicate<RNode<R, T>> onNodes,
			Function<RLeaf<R, T>, V> onLeaves, Supplier<V> initialValue, BinaryOperator<V> combiner) {
		if (onNodes.test(node)) {
			if (node instanceof RBranch) {
				RBranch<R, T> branch = (RBranch<R, T>) node;
				V value = initialValue.get();
				for (int i = 0; i < branch.numChildren(); i++) {
					V childValue = traverse(branch.childAt(i), onNodes, onLeaves, initialValue, combiner);
					value = combiner.apply(value, childValue);
				}
				return value;
			} else if (node instanceof RLeaf) {
				return onLeaves.apply((RLeaf<R, T>) node);
			}
		}
		return initialValue.get();
	}

	public static <R, T> boolean traverse(RNode<R, T> root, Predicate<RNode<R, T>> onNodes,
			Predicate<RLeaf<R, T>> onLeaves) {
		if (onNodes.test(root)) {
			if (root instanceof RBranch) {
				RBranch<R, T> branch = (RBranch<R, T>) root;
				for (int i = 0; i < branch.numChildren(); i++) {
					if (!traverse(branch.childAt(i), onNodes, onLeaves)) {
						return false;
					}
				}
			} else if (root instanceof RLeaf) {
				return onLeaves.test((RLeaf<R, T>) root);
			}
		}
		return true;
	}
}
