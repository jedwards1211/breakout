package org.andork.spatial;

import java.util.ArrayList;
import java.util.List;

import org.andork.generic.Ref;
import org.junit.Assert;
import org.junit.Test;

public class ClosestLeafNodeTest {
	@Test
	public void testClosestLeafNode() {
		RfStarTree<Integer> tree = new RfStarTree<Integer>(2, 8, 3, 3);

		List<RfStarTree.Leaf<Integer>> leaves = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			float[] mbr = new float[4];
			mbr[0] = (float) Math.random();
			mbr[1] = (float) Math.random();
			mbr[2] = mbr[0] + (float) Math.random();
			mbr[3] = mbr[1] + (float) Math.random();
			RfStarTree.Leaf<Integer> leaf = tree.createLeaf(mbr, i);
			tree.insert(leaf);
			leaves.add(leaf);
		}

		float[] p = { 0.5f, 0.5f, 0.5f };
		Ref<Float> distance = new Ref<>();
		
		RLeaf<float[], Integer> closest = RTraversal.closestLeafNode(tree.getRoot(), p, 
				ClosestLeafNodeTest::getMinDistance, 
				ClosestLeafNodeTest::getMaxDistance, null, distance);
		System.out.println("closest: " + closest);
		System.out.println("distance: " + distance.value);
		
		Assert.assertTrue(distance.value < 2);
		Assert.assertEquals(getMinDistance(closest, p), distance.value, 0.0);
		
		for (RfStarTree.Leaf<Integer> leaf : leaves) {
			System.out.println("leaf: " + leaf);
			System.out.println("distance: " + getMinDistance(leaf, p));
			Assert.assertTrue(getMinDistance(leaf, p) >= distance.value);
		}
	}

	static float getMinDistance(RNode<float[], ?> node, float[] p) {
		return Rectmath.distanceToClosestCornerSquared2(node.mbr(), p);
	}

	static float getMaxDistance(RNode<float[], ?> node, float[] p) {
		return Rectmath.distanceToFarthestCornerSquared2(node.mbr(), p);
	}
}
