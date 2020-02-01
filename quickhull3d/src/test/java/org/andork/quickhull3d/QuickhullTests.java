package org.andork.quickhull3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;

public class QuickhullTests {
	@Test
	public void testGetFarthestPointsOnEachAxis() {
		List<double[]> points = Arrays.asList(
				new double[] { 2, 3, 7 },
				new double[] { 9, 4, 2 },
				new double[] { 8, 11, 6 },
				new double[] { 1, 3, 8 },
				new double[] { 8, 3, 4 });

		double[][] extrema = Quickhull.getFarthestPointsOnEachAxis(points);

		Assert.assertSame(points.get(3), extrema[0]);
		Assert.assertSame(points.get(0), extrema[1]);
		Assert.assertSame(points.get(1), extrema[2]);
		Assert.assertSame(points.get(1), extrema[3]);
		Assert.assertSame(points.get(2), extrema[4]);
		Assert.assertSame(points.get(3), extrema[5]);
	}

	@Test
	public void testTraceHorizonOfViewPoint() {
		double[] a = { 0, 0, 0 };
		double[] b = { 0, 0, 1 };
		double[] c = { 1, 0, 0.5 };
		double[] d = { 0.5, 1, 0.5 };

		Face[] tetrahedron = Face.createTetrahedron(a, b, c, d);

		// This point can see the top three faces (considering +y as up).
		List<Edge> horizon = Quickhull.traceHorizonOfViewPoint(new double[] { 0.5, 2, 0.5 },
				tetrahedron[0].edges[0].oppositeEdge);
		Assert.assertEquals(Arrays.asList(
				tetrahedron[0].edges[0].oppositeEdge,
				tetrahedron[0].edges[2].oppositeEdge,
				tetrahedron[0].edges[1].oppositeEdge), horizon);

		// This point can see two faces, so the horizon should have four edges.
		horizon = Quickhull.traceHorizonOfViewPoint(new double[] { 0.1, 2, 0.1 },
				tetrahedron[0].edges[0].oppositeEdge);
		Assert.assertEquals(Arrays.asList(
				tetrahedron[0].edges[0].oppositeEdge,
				tetrahedron[0].edges[2].oppositeEdge,
				tetrahedron[0].edges[2].oppositeEdge.nextEdge,
				tetrahedron[0].edges[0].oppositeEdge.prevEdge), horizon);

		// This point is coplanar with the base of the tetrahedron (considering y=0 as the base).
		// The horizon should be a triangle including the far edge of the base.
		horizon = Quickhull.traceHorizonOfViewPoint(new double[] { -0.1, 0, -0.1 },
				tetrahedron[0].edges[1]);
		Assert.assertEquals(Arrays.asList(
				tetrahedron[0].edges[1],
				tetrahedron[0].edges[1].oppositeEdge.nextEdge.nextEdge.oppositeEdge,
				tetrahedron[0].edges[1].oppositeEdge.nextEdge.oppositeEdge), horizon);

		// This point can only see the bottom face (considering -y as down).
		horizon = Quickhull.traceHorizonOfViewPoint(new double[] { 0.5, -1, 0.5 },
				tetrahedron[0].edges[0]);
		Assert.assertEquals(Arrays.asList(
				tetrahedron[0].edges[0],
				tetrahedron[0].edges[1],
				tetrahedron[0].edges[2]), horizon);
	}

	@Test
	public void testConnectHorizonToViewPoint() {
		double[] a = { 0, 0, 0 };
		double[] b = { 0, 0, 1 };
		double[] c = { 1, 0, 0.5 };
		double[] d = { 0.5, 1, 0.5 };

		Face[] tetrahedron = Face.createTetrahedron(a, b, c, d);

		List<Edge> horizon = Arrays.asList(
				tetrahedron[0].edges[0].oppositeEdge,
				tetrahedron[0].edges[2].oppositeEdge,
				tetrahedron[0].edges[1].oppositeEdge);

		double[] viewpoint = { 0.5, 2, 0.5 };
		List<Face> newFaces = Quickhull.connectHorizonToViewPoint(
				horizon, viewpoint, new double[] { 0.5, 0.5, 0.5 });

		for (Edge edge : horizon) {
			Assert.assertNull(edge.oppositeEdge);
		}

		Assert.assertEquals(Arrays.asList(
				tetrahedron[0].edges[0].oppositeEdge.face,
				tetrahedron[0].edges[2].oppositeEdge.face,
				tetrahedron[0].edges[1].oppositeEdge.face), newFaces);

		for (Face face : newFaces) {
			Assert.assertSame(viewpoint, face.edges[1].nextVertex);
			for (Edge edge : face.edges) {
				Assert.assertNotNull(edge.oppositeEdge);
				Assert.assertEquals(edge, edge.oppositeEdge.oppositeEdge);
			}
		}
	}

	@Test
	public void hereGoesNothing() {
		List<double[]> points = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < 100; i++) {
			points.add(new double[] { random.nextInt(100), random.nextInt(100), random.nextInt(100) });
		}

		Set<Face> convexHull = Quickhull.createConvexHull(points);
		assertCorrect(convexHull, points);
	}

	@Test
	public void hereGoesNothing2() {
		System.out.println("RANDOM");
		List<double[]> points = new ArrayList<>();

		for (int i = 0; i < 100000; i++) {
			points.add(new double[] { Math.random(), Math.random(), Math.random() });
		}

		System.out.println(points.size() + " points");

		Set<Face> convexHull = time(() -> Quickhull.createConvexHull(points));
		assertCorrect(convexHull, points);
	}

	@Test
	/**
	 * Tests that lots of coplanar points don't break the algorithm
	 */
	public void testCube() {
		List<double[]> points = new ArrayList<>();
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				for (int z = 0; z < 10; z++) {
					points.add(new double[] { x, y, z });
				}
			}
		}
		Collections.shuffle(points);

		Set<Face> convexHull = Quickhull.createConvexHull(points);
		assertCorrect(convexHull, points);
		Assert.assertEquals(12, convexHull.size());
	}

	@Test
	public void testSmallPerturbations() {
		List<double[]> points = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < 100; i++) {
			points.add(new double[] { random.nextInt(100), random.nextInt(100), random.nextInt(100) });
		}
		for (int i = 0; i < 100; i++) {
			double[] point = points.get(random.nextInt(points.size()));
			double[] newPoint = Arrays.copyOf(point, point.length);
			newPoint[0] += 1e-320 + Math.random() * 1e-256;
			newPoint[1] += 1e-320 + Math.random() * 1e-256;
			newPoint[2] += 1e-320 + Math.random() * 1e-256;
			points.add(newPoint);
		}

		Set<Face> convexHull = Quickhull.createConvexHull(points);
		assertCorrect(convexHull, points);
	}

	@Test
	public void testWorstCase() {
		System.out.println("WORST CASE");
		List<double[]> points = new ArrayList<>();

		for (double longitude = 0; longitude < Math.PI * 2; longitude += Math.PI / 40) {
			for (double latitude = -Math.PI / 2; latitude < Math.PI / 2; latitude += Math.PI / 20) {
				double xy = Math.sin(latitude);
				points.add(new double[] {
						xy * Math.cos(longitude),
						Math.cos(latitude),
						xy * Math.sin(longitude),
				});
			}
		}
		System.out.println(points.size() + " points");

		Set<Face> convexHull = time(() -> Quickhull.createConvexHull(points));
		assertCorrect(convexHull, points);
	}

	static <T> T time(Supplier<T> s) {
		long start = System.currentTimeMillis();
		T result = s.get();
		long elapsed = System.currentTimeMillis() - start;
		System.out.println("took " + elapsed + " ms");
		return result;
	}

	public static void assertSelfConsistent(Edge edge) {
		Assert.assertFalse(Arrays.equals(edge.prevVertex, edge.nextVertex));
		Assert.assertNotNull(edge.oppositeEdge);
		Assert.assertSame(edge, edge.oppositeEdge.oppositeEdge);
		Assert.assertSame(edge.prevVertex, edge.oppositeEdge.nextVertex);
		Assert.assertSame(edge.nextVertex, edge.oppositeEdge.prevVertex);
	}

	public static void assertSelfConsistent(Face face) {
		for (Edge edge : face.edges) {
			assertSelfConsistent(edge);
			Assert.assertSame(face, edge.face);
			Assert.assertSame(edge, edge.nextEdge.prevEdge);
		}
	}

	public static void assertSelfConsistent(Set<Face> hull) {
		for (Face face : hull) {
			assertSelfConsistent(face);
		}
	}

	public static void assertPointsAreInsideHull(Set<Face> hull, Collection<double[]> points) {
		for (Face face : hull) {
			for (double[] point : points) {
				Assert.assertFalse(face.isVisibleFromPoint(point));
			}
		}
	}

	/**
	 * For each vertex of the hull, checks that all faces connected to that
	 * vertex are connected by their edges into a single cycle. If some face
	 * connects to a vertex but not to an edge of any other faces connected to
	 * that vertex, we have a problem.
	 */
	public static void assertWellFormed(Set<Face> hull) {
		class Vertex {
			final double[] coordinates;

			Vertex(double[] coordinates) {
				super();
				this.coordinates = coordinates;
			}

			@Override
			public int hashCode() {
				return Arrays.hashCode(coordinates);
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj) {
					return true;
				}
				if (obj == null) {
					return false;
				}
				if (getClass() != obj.getClass()) {
					return false;
				}
				Vertex other = (Vertex) obj;
				return Arrays.equals(coordinates, other.coordinates);
			}
		}

		Map<Vertex, Set<Face>> facesAroundVertices = new HashMap<>();
		for (Face face : hull) {
			for (Edge edge : face.edges) {
				Vertex key = new Vertex(edge.nextVertex);
				Set<Face> facesAroundVertex = facesAroundVertices.get(key);
				if (facesAroundVertex == null) {
					facesAroundVertex = new HashSet<>();
					Edge currentEdge = edge;
					do {
						Assert.assertTrue(facesAroundVertex.add(currentEdge.face));
						currentEdge = currentEdge.nextEdge.oppositeEdge;
					} while (currentEdge != edge);
					facesAroundVertices.put(key, facesAroundVertex);
				} else {
					Assert.assertTrue(facesAroundVertex.contains(face));
				}
			}
		}
	}

	public static void assertCorrect(Set<Face> hull, Collection<double[]> inputPoints) {
		Assert.assertTrue(hull.size() >= 4);
		assertPointsAreInsideHull(hull, inputPoints);
		assertSelfConsistent(hull);
		assertWellFormed(hull);
	}
}
