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

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.andork.ref.Ref;

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

	/**
	 * Finds the closest leaf node to the given query (which can be a point,
	 * bounding rectangle, or whatever you want -- you provide the functions for
	 * getting its distance from a node).
	 * 
	 * @param root
	 *            the root of the spatial index
	 * @param query
	 *            the thing to find the closest leaf node to
	 * @param getMinDistance
	 *            function that takes a node and the query and returns the
	 *            minimum distance. On a branch node, it should return the
	 *            distance to the closest corner of its bounding rectangle. For
	 *            leaf nodes, it can do the same thing, or it can get the
	 *            minimum distance to whatever underlying geometry you've
	 *            associated with the leaf node.
	 * @param getMaxDistance
	 *            function that takes a node and the query and returns the
	 *            maximum distance. On a branch node, it should return the
	 *            distance to the farthest corner of its bounding rectangle. For
	 *            leaf nodes, it can do the same thing, or it can get the
	 *            maximum distance to whatever underlying geometry you've
	 *            associated with the leaf node.
	 * @param maxDistance
	 *            the maximum distance from {@code query} to search.
	 * @param distance
	 *            output -- the distance to the closest leaf node will be stored
	 *            here
	 * @return the closest leaf node to {@code query}, or {@code null} if
	 *         {@code maxDistance} is not {@code null} and no leaf nodes were
	 *         closer than {@code maxDistance}.
	 */
	public static <R, T, Q, D extends Comparable<D>> RLeaf<R, T> closestLeafNode(RNode<R, T> root, Q query,
			BiFunction<RNode<R, T>, Q, D> getMinDistance,
			BiFunction<RNode<R, T>, Q, D> getMaxDistance,
			D maxDistance, Ref<D> distance) {
		if (!(root instanceof RBranch)) {
			distance.value = getMinDistance.apply(root, query);
			if (maxDistance != null && distance.value.compareTo(maxDistance) > 0) {
				return null;
			}
			return (RLeaf<R, T>) root;
		}

		RBranch<R, T> branch = (RBranch<R, T>) root;
		if (branch.numChildren() == 0) {
			throw new IllegalArgumentException("every branch should have at least 1 child");
		}

		// Whatever the closest leaf is, it's not going to be farther away than the farthest
		// corner of any child node; we can use this fact to whittle down maxDistance.

		RLeaf<R, T> closest = null;

		for (int i = 0; i < branch.numChildren(); i++) {
			RNode<R, T> child = branch.childAt(i);
			D childMaxDistance = getMaxDistance.apply(child, query);
			if (maxDistance == null || childMaxDistance.compareTo(maxDistance) < 0) {
				maxDistance = childMaxDistance;
			}
		}

		for (int i = 0; i < branch.numChildren(); i++) {
			RLeaf<R, T> closestForChild = closestLeafNode(branch.childAt(i), query,
					getMinDistance, getMaxDistance, maxDistance, distance);
			if (closestForChild != null) {
				assert maxDistance == null || distance.value.compareTo(maxDistance) <= 0;
				closest = closestForChild;
				maxDistance = distance.value;
			}
		}
		distance.value = maxDistance;
		return closest;
	}
}
