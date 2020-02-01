package org.andork.quickhull3d;

import org.junit.Assert;
import org.junit.Test;

public class EdgeTests {
	@Test
	public void testIsInHorizonOfViewPoint() {
		double[] a = { 0, 0, 0 };
		double[] b = { 0, 0, 1 };
		double[] c = { 1, 0, 0.5 };
		double[] d = { 0.5, 1, 0.5 };

		Face[] tetrahedron = Face.createTetrahedron(a, b, c, d);
		Assert.assertSame(b, tetrahedron[0].edges[0].nextVertex);
		Assert.assertSame(c, tetrahedron[0].edges[1].nextVertex);

		Assert.assertFalse(tetrahedron[0].edges[0].isInHorizonOfViewPoint(new double[] { -1, 0, 0 }));
		Assert.assertFalse(tetrahedron[0].edges[0].isInHorizonOfViewPoint(new double[] { -1, -1, 0 }));
		Assert.assertFalse(tetrahedron[0].edges[0].isInHorizonOfViewPoint(new double[] { -1, 1, 0 }));
		Assert.assertTrue(tetrahedron[0].edges[0].isInHorizonOfViewPoint(new double[] { 2, 0, 0 }));
		Assert.assertTrue(tetrahedron[0].edges[0].isInHorizonOfViewPoint(new double[] { 2, -1, 0 }));
		Assert.assertFalse(tetrahedron[0].edges[1].isInHorizonOfViewPoint(new double[] { 1, 0, 1 }));
		Assert.assertTrue(tetrahedron[0].edges[1].isInHorizonOfViewPoint(new double[] { -1, 0, -1 }));

		double[] above = new double[] { 0.5, 5, 0.5 };
		Assert.assertTrue(tetrahedron[0].edges[0].oppositeEdge.isInHorizonOfViewPoint(above));
		Assert.assertTrue(tetrahedron[0].edges[1].oppositeEdge.isInHorizonOfViewPoint(above));
		Assert.assertTrue(tetrahedron[0].edges[2].oppositeEdge.isInHorizonOfViewPoint(above));
		Assert.assertFalse(tetrahedron[0].edges[0].isInHorizonOfViewPoint(above));
		Assert.assertFalse(tetrahedron[0].edges[1].isInHorizonOfViewPoint(above));
		Assert.assertFalse(tetrahedron[0].edges[2].isInHorizonOfViewPoint(above));

		double[] below = new double[] { 0.5, -1, 0.5 };
		Assert.assertTrue(tetrahedron[0].edges[0].isInHorizonOfViewPoint(below));
		Assert.assertTrue(tetrahedron[0].edges[1].isInHorizonOfViewPoint(below));
		Assert.assertTrue(tetrahedron[0].edges[2].isInHorizonOfViewPoint(below));
		Assert.assertFalse(tetrahedron[0].edges[0].oppositeEdge.isInHorizonOfViewPoint(below));
		Assert.assertFalse(tetrahedron[0].edges[1].oppositeEdge.isInHorizonOfViewPoint(below));
		Assert.assertFalse(tetrahedron[0].edges[2].oppositeEdge.isInHorizonOfViewPoint(below));

		Assert.assertTrue(tetrahedron[0].edges[0].oppositeEdge.isInHorizonOfViewPoint(d));
		Assert.assertTrue(tetrahedron[0].edges[1].oppositeEdge.isInHorizonOfViewPoint(d));
		Assert.assertTrue(tetrahedron[0].edges[2].oppositeEdge.isInHorizonOfViewPoint(d));
	}
}
