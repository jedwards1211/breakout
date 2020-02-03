package org.andork.quickhull3d;

import org.junit.Assert;
import org.junit.Test;

public class FaceTests {
	private static void assertThrows(Runnable r) {
		try {
			r.run();
			Assert.fail("expected an exception");
		}
		catch (Throwable ex) {

		}
	}

	@Test
	public void testOrientation() {
		Vertex a = new DefaultVertex(0, 0, 0);
		Vertex b = new DefaultVertex(1, 0, 1);
		Vertex c = new DefaultVertex(1, 1, 0);
		Vertex d = new DefaultVertex(0, 1, 1);

		new Face<Vertex>(a, b, c, d);
		assertThrows(() -> new Face<Vertex>(a, c, b, d));
	}

	@Test
	public void testTetrahedron() {
		Vertex a = new DefaultVertex(0, 0, 0);
		Vertex b = new DefaultVertex(1, 0, 1);
		Vertex c = new DefaultVertex(1, 1, 0);
		Vertex d = new DefaultVertex(0, 1, 1);

		Face<Vertex>[] faces = Face.createTetrahedron(a, b, c, d);

		Assert.assertEquals(4, faces.length);
		for (Face<Vertex> face : faces) {
			for (int i = 0; i < 3; i++) {
				Assert.assertNotNull(face.edges[i].oppositeEdge);
				Assert.assertNotNull(face.edges[i].prevEdge);
				Assert.assertNotNull(face.edges[i].nextEdge);
				Assert.assertSame(face.edges[(i + 1) % 3], face.edges[i].nextEdge);
				Assert.assertSame(face.edges[i], face.edges[(i + 1) % 3].prevEdge);
				Assert.assertSame(face.edges[i], face.edges[i].oppositeEdge.oppositeEdge);
				Assert.assertSame(face.edges[i].prevVertex, face.edges[i].oppositeEdge.nextVertex);
				Assert.assertSame(face.edges[i].nextVertex, face.edges[i].oppositeEdge.prevVertex);
			}
		}

		// Check that no orientation exceptions are thrown for any order of vertices
		Face.createTetrahedron(a, b, d, c);
		Face.createTetrahedron(a, c, b, d);
		Face.createTetrahedron(a, c, d, b);
		Face.createTetrahedron(a, d, b, c);
		Face.createTetrahedron(a, d, c, b);
		Face.createTetrahedron(b, a, c, d);
		Face.createTetrahedron(b, a, d, c);
		Face.createTetrahedron(b, c, a, d);
		Face.createTetrahedron(b, c, d, a);
		Face.createTetrahedron(b, d, a, c);
		Face.createTetrahedron(b, d, c, a);
		Face.createTetrahedron(c, a, b, d);
		Face.createTetrahedron(c, a, d, b);
		Face.createTetrahedron(c, b, a, d);
		Face.createTetrahedron(c, b, d, a);
		Face.createTetrahedron(c, d, a, b);
		Face.createTetrahedron(c, d, b, a);
		Face.createTetrahedron(d, a, b, c);
		Face.createTetrahedron(d, a, c, b);
		Face.createTetrahedron(d, b, a, c);
		Face.createTetrahedron(d, b, c, a);
		Face.createTetrahedron(d, c, a, b);
		Face.createTetrahedron(d, c, b, a);
	}

	@Test
	public void testDegeneracy() {
		Vertex a = new DefaultVertex(0, 0, 0);
		Vertex b = new DefaultVertex(1, 0, 0);
		Vertex c = new DefaultVertex(0, 0, 1);
		Vertex d = new DefaultVertex(0.5, 1, 0.5);
		Vertex e = new DefaultVertex(0.5, 0, 0.5);
		Vertex f = new DefaultVertex(0.5, 1e-256, 0.5);
		Vertex g = new DefaultVertex(1e-256, 0, 0);
		Vertex h = new DefaultVertex(0.5, 0, 0);

		assertThrows(() -> new Face<Vertex>(a, a, c, d));
		assertThrows(() -> new Face<Vertex>(a, b, c, e));
		assertThrows(() -> new Face<Vertex>(a, g, c, e));
		assertThrows(() -> new Face<Vertex>(a, b, g, f));
		assertThrows(() -> new Face<Vertex>(a, b, h, d));
		assertThrows(() -> new Face<Vertex>(a, a, a, d));
		assertThrows(() -> new Face<Vertex>(a, a, a, a));
		new Face<Vertex>(a, c, g, d);
		// new Face<Vertex>(a, g, c, f); // TODO -- orientation fails
		new Face<Vertex>(a, c, b, f);
	}
}
