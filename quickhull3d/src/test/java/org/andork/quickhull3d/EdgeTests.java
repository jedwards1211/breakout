package org.andork.quickhull3d;

import org.junit.Assert;
import org.junit.Test;

public class EdgeTests {
	@Test
	public void testIsInHorizonOfViewPoint() {
		Vertex a = new DefaultVertex(0, 0, 0);
		Vertex b = new DefaultVertex(0, 0, 1);
		Vertex c = new DefaultVertex(1, 0, 0.5);
		Vertex d = new DefaultVertex(0.5, 1, 0.5);

		Face<Vertex>[] tetrahedron = Face.createTetrahedron(a, b, c, d);
		Assert.assertSame(b, tetrahedron[0].edges[0].nextVertex);
		Assert.assertSame(c, tetrahedron[0].edges[1].nextVertex);

		Assert.assertFalse(tetrahedron[0].edges[0].isInHorizonOfViewPoint(new DefaultVertex(-1, 0, 0)));
		Assert.assertFalse(tetrahedron[0].edges[0].isInHorizonOfViewPoint(new DefaultVertex(-1, -1, 0)));
		Assert.assertFalse(tetrahedron[0].edges[0].isInHorizonOfViewPoint(new DefaultVertex(-1, 1, 0)));
		Assert.assertTrue(tetrahedron[0].edges[0].isInHorizonOfViewPoint(new DefaultVertex(2, 0, 0)));
		Assert.assertTrue(tetrahedron[0].edges[0].isInHorizonOfViewPoint(new DefaultVertex(2, -1, 0)));
		Assert.assertFalse(tetrahedron[0].edges[1].isInHorizonOfViewPoint(new DefaultVertex(1, 0, 1)));
		Assert.assertTrue(tetrahedron[0].edges[1].isInHorizonOfViewPoint(new DefaultVertex(-1, 0, -1)));

		Vertex above = new DefaultVertex(0.5, 5, 0.5);
		Assert.assertTrue(tetrahedron[0].edges[0].oppositeEdge.isInHorizonOfViewPoint(above));
		Assert.assertTrue(tetrahedron[0].edges[1].oppositeEdge.isInHorizonOfViewPoint(above));
		Assert.assertTrue(tetrahedron[0].edges[2].oppositeEdge.isInHorizonOfViewPoint(above));
		Assert.assertFalse(tetrahedron[0].edges[0].isInHorizonOfViewPoint(above));
		Assert.assertFalse(tetrahedron[0].edges[1].isInHorizonOfViewPoint(above));
		Assert.assertFalse(tetrahedron[0].edges[2].isInHorizonOfViewPoint(above));

		Vertex below = new DefaultVertex(0.5, -1, 0.5);
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
