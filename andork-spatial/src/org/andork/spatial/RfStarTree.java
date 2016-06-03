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

import static org.andork.spatial.Rectmath.nmax;
import static org.andork.spatial.Rectmath.nmin;
import static org.andork.spatial.Rectmath.union;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;

public class RfStarTree<T> implements SpatialIndex<float[], T> {
	public static class Branch<T> extends Node<T> implements RBranch<float[], T> {
		/**
		 * The level of this branch in the tree. 0 for the level above leaves, 1
		 * for the level above that, etc.
		 */
		final int level;
		int numChildren;
		Node<T>[] children;

		public Branch(int dimension, int level, int numChildren) {
			super(Rectmath.voidRectf(dimension));
			this.level = level;
			this.children = new Node[numChildren];
		}

		@Override
		public Node<T> childAt(int index) {
			return children[index];
		}

		@Override
		public int level() {
			return level;
		}

		@Override
		public int numChildren() {
			return numChildren;
		}

		void recalcMbr() {
			Arrays.fill(mbr, Float.NaN);
			for (int i = 0; i < numChildren; i++) {
				union(mbr, children[i].mbr, mbr);
			}
		}
	}

	class CenterDistanceComparator implements Comparator<Node<?>> {
		float[] otherMbr;

		public CenterDistanceComparator(float[] otherMbr) {
			super();
			this.otherMbr = otherMbr;
		}

		@Override
		public int compare(Node<?> o1, Node<?> o2) {
			return -Float.compare(centerDistSq(o1.mbr, otherMbr), centerDistSq(o2.mbr, otherMbr));
		}
	}

	class EnlargementComparator implements Comparator<Node<?>> {
		float[] newMbr;

		public EnlargementComparator(float[] newMbr) {
			super();
			this.newMbr = newMbr;
		}

		@Override
		public int compare(Node<?> o1, Node<?> o2) {
			return Float.compare(enlargement(o1.mbr, newMbr), enlargement(o2.mbr, newMbr));
		}
	}

	public static class Leaf<T> extends Node<T> implements RLeaf<float[], T> {
		final T object;

		public Leaf(float[] mbr, T object) {
			super(mbr);
			this.object = object;
		}

		@Override
		public int level() {
			return 0;
		}

		@Override
		public T object() {
			return object;
		}
	}

	static class LowerUpperComparator implements Comparator<Node<?>> {
		final int axis;
		final int dimension;

		public LowerUpperComparator(int axis, int dimension) {
			super();
			this.axis = axis;
			this.dimension = dimension;
		}

		@Override
		public int compare(Node<?> o1, Node<?> o2) {
			int result = Float.compare(o1.mbr[axis], o2.mbr[axis]);
			if (result != 0) {
				return result;
			}
			return Float.compare(o1.mbr[axis + dimension], o2.mbr[axis + dimension]);
		}
	}

	public static abstract class Node<T> implements RNode<float[], T> {
		Branch<T> parent;
		final float[] mbr;

		public Node(float[] mbr) {
			super();
			this.mbr = mbr;
		}

		public abstract int level();

		@Override
		public float[] mbr() {
			return mbr;
		}
	}

	static <T> void addChild(Branch<T> parent, Node<T> node) {
		if (parent.numChildren > 0 && parent.children[0] instanceof Leaf != node instanceof Leaf) {
			throw new IllegalArgumentException("Cannot mix leaf and non-leaf nodes in the same branch");
		}
		if (parent.numChildren == parent.children.length) {
			parent.children = Arrays.copyOf(parent.children, parent.numChildren + 1);
		}
		node.parent = parent;
		parent.children[parent.numChildren++] = node;
	}

	static <T> void recalcMbrs(Branch<T> target) {
		while (target != null) {
			target.recalcMbr();
			target = target.parent;
		}
	}

	static <T> void removeFromParent(Node<T> node) {
		if (node.parent != null) {
			int index = -1;
			for (int i = 0; i < node.parent.numChildren; i++) {
				if (node.parent.children[i] == node) {
					index = i;
					break;
				}
			}

			if (index >= 0) {
				if (index == node.parent.numChildren - 1) {
					node.parent.children[index] = null;
				} else {
					node.parent.children[index] = node.parent.children[node.parent.numChildren - 1];
				}
				node.parent.numChildren--;
				node.parent = null;
			}
		}
	}

	int dimension;

	Branch<T> root;

	LowerUpperComparator[] chooseSplitAxisComparators;

	int maxChildrenPerBranch, minSplitSize, numToReinsert;

	float[] rt;

	float[][] rt0;

	float[][] rt1;

	int maxLevel = 0;

	public RfStarTree(int dimension, int maxChildrenPerBranch, int minSplitSize, int numToReinsert) {
		this.dimension = dimension;
		this.maxChildrenPerBranch = maxChildrenPerBranch;
		this.minSplitSize = minSplitSize;
		this.numToReinsert = numToReinsert;

		rt = Rectmath.voidRectf(dimension);
		rt0 = new float[maxChildrenPerBranch - minSplitSize + 2][dimension * 2];
		rt1 = new float[maxChildrenPerBranch - minSplitSize + 2][dimension * 2];

		chooseSplitAxisComparators = new LowerUpperComparator[dimension];
		for (int axis = 0; axis < dimension; axis++) {
			chooseSplitAxisComparators[axis] = new LowerUpperComparator(axis, dimension);
		}

		root = new Branch<T>(dimension, 1, maxChildrenPerBranch);
	}

	float area(float[] mbr) {
		float area = 1f;
		for (int axis = 0; axis < dimension; axis++) {
			float span = mbr[axis + dimension] - mbr[axis];
			if (span == 0) {
				span = Math.ulp(mbr[axis]);
			}
			area *= span;
		}
		return Float.isNaN(area) ? 0f : area;
	}

	float centerDistSq(float[] a, float[] b) {
		float distSq = 0f;

		for (int i = 0; i < dimension; i++) {
			float d = (a[i + dimension] + a[i] - b[i + dimension] - b[i]) * 0.5f;
			distSq += d * d;
		}
		return distSq;
	}

	int chooseSplitAxis(Branch<T> branch) {
		float bestMargin = 0f;
		int bestAxis = 0;

		for (int axis = 0; axis < dimension; axis++) {
			Arrays.sort(branch.children, chooseSplitAxisComparators[axis]);

			float totalMargin = 0f;

			for (int i = 0; i < minSplitSize - 1; i++) {
				union(rt, branch.children[i].mbr, rt);
			}
			for (int k = minSplitSize - 1; k < maxChildrenPerBranch + 1 - minSplitSize; k++) {
				union(rt, branch.children[k].mbr, rt);
				totalMargin += margin(rt);
			}

			Arrays.fill(rt, Float.NaN);
			for (int i = maxChildrenPerBranch; i > maxChildrenPerBranch + 1 - minSplitSize; i--) {
				union(rt, branch.children[i].mbr, rt);
			}
			for (int k = maxChildrenPerBranch + 1 - minSplitSize; k >= minSplitSize; k--) {
				union(rt, branch.children[k].mbr, rt);
				totalMargin += margin(rt);
			}

			if (axis == 0 || totalMargin < bestMargin) {
				bestAxis = axis;
				bestMargin = totalMargin;
			}
		}

		return bestAxis;
	}

	int chooseSplitIndex(Branch<T> branch, int axis) {
		float bestOverlap = 0f;
		float bestArea = 0f;
		int bestIndex = 0;

		Arrays.sort(branch.children, chooseSplitAxisComparators[axis]);

		Arrays.fill(rt0[0], Float.NaN);
		Arrays.fill(rt1[0], Float.NaN);

		for (int index = 1; index <= maxChildrenPerBranch + 1 - minSplitSize; index++) {
			union(rt0[index - 1], branch.children[index - 1].mbr, rt0[index]);
			union(rt1[index - 1], branch.children[maxChildrenPerBranch + 1 - index].mbr, rt1[index]);
		}

		for (int index = minSplitSize; index <= maxChildrenPerBranch + 1 - minSplitSize; index++) {
			float overlap = overlap(rt0[index], rt1[maxChildrenPerBranch + 1 - index]);
			float area = area(rt0[index]) + area(rt1[maxChildrenPerBranch + 1 - index]);

			if (index == minSplitSize || overlap < bestOverlap || overlap == bestOverlap && area < bestArea) {
				bestIndex = index;
				bestOverlap = overlap;
				bestArea = area;
			}
		}

		return bestIndex;
	}

	Branch<T> chooseSubtree(Node<T> toInsert, Branch<T> node) {
		while (node.level > toInsert.level() + 1) {
			int bestIndex = 0;

			if (node.level == 2) {
				Arrays.sort(node.children, 0, node.numChildren, new EnlargementComparator(toInsert.mbr));

				float bestOverlapEnlargement = 0;
				float bestAreaEnlargement = 0;
				float bestArea = 0;

				for (int i = 0; i < Math.min(numToReinsert, node.numChildren); i++) {
					float[] current = node.children[i].mbr;
					float[] enlarged = rt0[0];
					union(current, toInsert.mbr, enlarged);

					float overlap = 0;
					float enlargedOverlap = 0;

					for (int k = 0; k < node.numChildren; k++) {
						if (k == i) {
							continue;
						}

						overlap += overlap(current, node.children[k].mbr);
						enlargedOverlap += overlap(enlarged, node.children[k].mbr);
					}

					float overlapEnlargement = enlargedOverlap - overlap;
					float area = area(current);
					float areaEnlargement = area(enlarged) - area;

					if (i == 0 || overlapEnlargement < bestOverlapEnlargement ||
							overlapEnlargement == bestOverlapEnlargement && areaEnlargement < bestAreaEnlargement ||
							overlapEnlargement == bestOverlapEnlargement && areaEnlargement == bestAreaEnlargement &&
									area < bestArea) {
						bestIndex = i;
						bestOverlapEnlargement = overlapEnlargement;
						bestAreaEnlargement = areaEnlargement;
						bestArea = area;
					}
				}
			} else {
				float bestAreaEnlargement = 0;
				float bestArea = 0;

				for (int i = 0; i < node.numChildren; i++) {
					float[] current = node.children[i].mbr;
					float[] enlarged = rt0[0];
					union(current, toInsert.mbr, enlarged);

					float area = area(current);
					float areaEnlargement = area(enlarged) - area;

					if (i == 0 || areaEnlargement < bestAreaEnlargement ||
							areaEnlargement == bestAreaEnlargement && area < bestArea) {
						bestIndex = i;
						bestAreaEnlargement = areaEnlargement;
						bestArea = area;
					}
				}
			}

			if (node.children[bestIndex] instanceof Leaf) {
				System.out.println("TEST");
			}

			node = (Branch<T>) node.children[bestIndex];
		}

		return node;
	}

	@Override
	public Leaf<T> createLeaf(float[] mbr, T object) {
		if (mbr.length != dimension * 2) {
			throw new IllegalArgumentException("mbr.length must equal " + dimension * 2);
		}
		return new Leaf<T>(mbr, object);
	}

	void doReinsert(Branch<T> overflowed, BitSet reinsertedLevels) {
		reinsertedLevels.set(overflowed.level - 1);

		Arrays.sort(overflowed.children, new CenterDistanceComparator(overflowed.mbr));

		Node<T>[] pendingReinsertion = new Node[numToReinsert];

		System.arraycopy(overflowed.children, 0, pendingReinsertion, 0, numToReinsert);
		System.arraycopy(overflowed.children, maxChildrenPerBranch + 1 - numToReinsert, overflowed.children, 0,
				numToReinsert);
		overflowed.children = Arrays.copyOf(overflowed.children, maxChildrenPerBranch);
		overflowed.numChildren = maxChildrenPerBranch + 1 - numToReinsert;
		recalcMbrs(overflowed);

		for (Node<T> node : pendingReinsertion) {
			node.parent = null;
			insert(node, reinsertedLevels);
		}
	}

	void doSplit(Branch<T> overflowed, BitSet reinsertedLevels) {
		Branch<T> parent = overflowed.parent;
		removeFromParent(overflowed);

		Branch<T>[] split = split(overflowed);

		if (overflowed == root) {
			maxLevel++;
			root = new Branch<T>(dimension, split[0].level + 1, maxChildrenPerBranch);
			addChild(root, split[0]);
			addChild(root, split[1]);
			root.recalcMbr();
		} else {
			addChild(parent, split[0]);
			addChild(parent, split[1]);
			parent.recalcMbr();
		}
	}

	float enlargement(float[] r, float[] radded) {
		float volume = 1f;
		float result = 1f;

		for (int i = 0; i < dimension; i++) {
			result *= nmax(r[i + dimension], radded[i + dimension]) - nmin(r[i], radded[i]);
			volume *= r[i + dimension] - r[i];
		}

		return Float.isNaN(volume) ? result : result - volume;
	}

	@Override
	public Branch<T> getRoot() {
		return root;
	}

	public void insert(Leaf<T> newLeaf) {
		if (newLeaf.mbr.length != dimension * 2) {
			throw new IllegalArgumentException("newLeaf does not match the dimension of this tree");
		}
		if (newLeaf.parent != null) {
			throw new IllegalArgumentException("newLeaf is already in a tree");
		}

		insert(newLeaf, new BitSet());
	}

	void insert(Node<T> toInsert, BitSet reinsertedLevels) {
		Branch<T> target = chooseSubtree(toInsert, root);

		if (target.numChildren < maxChildrenPerBranch) {
			toInsert.parent = target;
			target.children[target.numChildren++] = toInsert;
			recalcMbrs(target);
		} else {
			overflowTreatment(toInsert, target, reinsertedLevels);
		}
	}

	float margin(float[] mbr) {
		float margin = 0f;
		for (int axis = 0; axis < dimension; axis++) {
			margin += mbr[axis + dimension] - mbr[axis];
		}
		return Float.isNaN(margin) ? 0f : margin;
	}

	void overflowTreatment(Node<T> toInsert, Branch<T> overflowed, BitSet reinsertedLevels) {
		addChild(overflowed, toInsert);

		while (overflowed != null && overflowed.numChildren > maxChildrenPerBranch) {
			if (!reinsertedLevels.get(toInsert.level())) {
				doReinsert(overflowed, reinsertedLevels);
				break;
			} else {
				Branch<T> nextParent = overflowed.parent;
				doSplit(overflowed, reinsertedLevels);
				overflowed = nextParent;
			}
		}
	}

	float overlap(float[] r1, float[] r2) {
		float overlap = 1f;
		for (int axis = 0; axis < dimension; axis++) {
			float hi1 = r1[axis + dimension];
			float hi2 = r2[axis + dimension];
			if (hi1 == r1[axis]) {
				hi1 += Math.ulp(hi1);
			}
			if (hi2 == r2[axis]) {
				hi2 += Math.ulp(hi2);
			}
			float span = nmin(hi1, hi2) - nmax(r1[axis], r2[axis]);
			if (span <= 0) {
				return 0;
			}
			overlap *= span;
		}
		return overlap;
	}

	Branch<T>[] split(Branch<T> overflowed) {
		int axis = chooseSplitAxis(overflowed);
		int index = chooseSplitIndex(overflowed, axis);

		Branch<T>[] result = new Branch[2];
		result[0] = new Branch<T>(dimension, overflowed.level, maxChildrenPerBranch);
		result[1] = new Branch<T>(dimension, overflowed.level, maxChildrenPerBranch);

		result[0].numChildren = index;
		result[1].numChildren = maxChildrenPerBranch + 1 - index;
		System.arraycopy(overflowed.children, 0, result[0].children, 0, index);
		for (int i = 0; i < result[0].numChildren; i++) {
			result[0].children[i].parent = result[0];
		}
		System.arraycopy(overflowed.children, index, result[1].children, 0, maxChildrenPerBranch + 1 - index);
		for (int i = 0; i < result[1].numChildren; i++) {
			result[1].children[i].parent = result[1];
		}

		Arrays.fill(overflowed.children, null);
		overflowed.numChildren = 0;

		result[0].recalcMbr();
		result[1].recalcMbr();

		return result;
	}
}
