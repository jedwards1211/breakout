package org.andork.quickhull3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Quickhull {
	/**
	 * @return an array of six points:<br>
	 *         {@code [0]} - a point with minimum x ({@code [0]})<br>
	 *         {@code [1]} - a point with minimum y ({@code [1]})<br>
	 *         {@code [2]} - a point with minimum z ({@code [2]})<br>
	 *         {@code [3]} - a point with maximum x ({@code [1]})<br>
	 *         {@code [4]} - a point with maximum y ({@code [2]})<br>
	 *         {@code [5]} - a point with maximum z ({@code [3]})<br>
	 *         <br>
	 *         If the given iterable produces no points, all elements of the
	 *         result will be {@code null}. The same point may occur up to 3
	 *         times in the array, or more in cases that quickhull is not
	 *         applicable to (less than 4 points, or all points have same x, y,
	 *         or z).
	 */
	static double[][] getFarthestPointsOnEachAxis(Iterable<double[]> points) {
		double[][] result = new double[6][];

		Iterator<double[]> i = points.iterator();
		if (!i.hasNext()) {
			return result;
		}
		Arrays.fill(result, i.next());

		while (i.hasNext()) {
			double[] point = i.next();
			if (point[0] < result[0][0]) {
				result[0] = point;
			}
			if (point[1] < result[1][1]) {
				result[1] = point;
			}
			if (point[2] < result[2][2]) {
				result[2] = point;
			}
			if (point[0] > result[3][0]) {
				result[3] = point;
			}
			if (point[1] > result[4][1]) {
				result[4] = point;
			}
			if (point[2] > result[5][2]) {
				result[5] = point;
			}
		}

		return result;
	}

	/**
	 * Chooses four vertices the given points that make a good starting
	 * tetrahedron for the quickhull algorithm.
	 *
	 * @param points
	 *            the points the user wants to ultimately make a convex hull out
	 *            of.
	 * @return an array containing the four {@link Face}s of the tetrahedron.
	 */
	static double[][] pickInitialTetrahedronVertices(Iterable<double[]> points) {
		double[][] extremePoints = getFarthestPointsOnEachAxis(points);
		double[][] vertices = new double[4][];

		// pick the farthest-apart extreme points for the first two vertices
		double baseLineDistance = 0;
		for (int i = 0; i < extremePoints.length; i++) {
			for (int j = i + 1; j < extremePoints.length; j++) {
				if (extremePoints[i] == extremePoints[j]) {
					continue;
				}
				double distance = VectorMath.distanceSquared(extremePoints[i], extremePoints[j]);
				if (distance > baseLineDistance) {
					vertices[0] = extremePoints[i];
					vertices[1] = extremePoints[j];
					baseLineDistance = distance;
				}
			}
		}

		// pick the extreme point farthest from the base line for the third vertex
		double thirdPointDistance = 0;
		for (int i = 0; i < extremePoints.length; i++) {
			double[] point = extremePoints[i];
			if (point == vertices[0] || point == vertices[1]) {
				continue;
			}
			double distance = VectorMath.distanceSquared(point, vertices[0])
					+ VectorMath.distanceSquared(point, vertices[1]);
			if (distance > thirdPointDistance) {
				vertices[2] = point;
				thirdPointDistance = distance;
			}
		}
		// if there are only two extreme points (because each was at the min or max of 3 axes)
		// then pick a third vertex from all of the input points instead
		if (vertices[2] == null) {
			for (double[] point : points) {
				double distance = VectorMath.distanceSquared(point, vertices[0])
						+ VectorMath.distanceSquared(point, vertices[1]);
				if (distance > thirdPointDistance) {
					vertices[2] = point;
					thirdPointDistance = distance;
				}
			}
		}

		// finally, pick the point farthest from the triangle as the fourth vertex
		double fourthPointDistance = 0;
		double[] normal = VectorMath.triangleNormal(vertices[0], vertices[1], vertices[2]);
		double[] difference = new double[3];
		VectorMath.normalize(normal);
		for (double[] point : points) {
			difference[0] = point[0] - vertices[0][0];
			difference[1] = point[1] - vertices[0][1];
			difference[2] = point[2] - vertices[0][2];
			double distance = Math.abs(VectorMath.dotProduct(normal, difference));
			if (distance > fourthPointDistance) {
				vertices[3] = point;
				fourthPointDistance = distance;
			}
		}

		return vertices;
	}

	/**
	 * Given a viewpoint and a starting {@link Face}, finds an edge in the
	 * horizon of the viewpoint.
	 *
	 * @param viewpoint
	 *            a point outside the convex hull.
	 * @param startingFace
	 *            a {@code Face}
	 *            {@linkplain Face#isWithinHorizonForPoint(double[]) within the
	 *            horizon} of {@code viewpoint}.
	 * @return a {@link Edge} {@link Edge#isInHorizonOfViewPoint(double[]) in
	 *         the horizon} of {@code viewpoint}.
	 */
	static Edge findEdgeInHorizonOfViewPoint(double[] viewpoint, Face startingFace) {
		if (Switches.PERFORM_SANITY_CHECKS) {
			assert startingFace.isVisibleFromPoint(viewpoint);
		}
		class PriorityNode implements Comparable<PriorityNode> {
			final Edge edge;
			final double distance;

			public PriorityNode(Edge edge) {
				this.edge = edge;
				distance = edge.face.distanceToPoint(viewpoint);
			}

			@Override
			public int compareTo(PriorityNode o) {
				return Double.compare(distance, o.distance);
			}
		}

		// Search outward from startingFace trying edges whose face has the least perpendicular
		// distance to viewpoint first, because those should be closer to the horizon
		PriorityQueue<PriorityNode> queue = new PriorityQueue<>();
		Set<Edge> visited = new HashSet<>();
		for (Edge edge : startingFace.edges) {
			queue.add(new PriorityNode(edge));
			visited.add(edge);
		}

		while (!queue.isEmpty()) {
			PriorityNode node = queue.poll();
			Edge edge = node.edge;
			if (edge.isInHorizonOfViewPoint(viewpoint)) {
				return edge;
			}
			if (visited.add(edge.oppositeEdge.prevEdge)) {
				queue.add(new PriorityNode(edge.oppositeEdge.prevEdge));
			}
			if (visited.add(edge.oppositeEdge.nextEdge)) {
				queue.add(new PriorityNode(edge.oppositeEdge.nextEdge));
			}
		}

		return null;
	}

	/**
	 * Given a viewpoint and an edge known to be in its horizon, finds the next
	 * edge of the horizon, which is one of the edges connected to the given
	 * edge's {@link Edge#nextVertex nextVertex}.
	 */
	static Edge findNextEdgeInHorizonOfViewPoint(double[] viewpoint, Edge currentEdge) {
		Edge candidateEdge = currentEdge.nextEdge;
		while (!candidateEdge.isInHorizonOfViewPoint(viewpoint)) {
			if (candidateEdge == currentEdge.oppositeEdge || candidateEdge == currentEdge.oppositeEdge) {
				throw new RuntimeException("failed to trace horizon!");
			}
			candidateEdge = candidateEdge.oppositeEdge.nextEdge;
		}
		return candidateEdge;
	}

	/**
	 * Given a viewpoint and an edge known to be in its horizon, finds the rest
	 * of the horizon for the viewpoint.
	 *
	 * @param viewpoint
	 *            a point outside the convex hull
	 * @param startingEdge
	 *            an edge known to be in the horizon of {@code viewpoint}
	 * @return a list of all edges in the horizon for {@code viewpoint}, which
	 *         is a closed loop: each edge's {@link Edge#nextVertex nextVertex}
	 *         is the {@link Edge#prevVertex prevVertex} of the next edge in the
	 *         list, and the {@code nextVertex} of the last edge in the list is
	 *         the {@code prevVertex} of the first.
	 */
	static List<Edge> traceHorizonOfViewPoint(double[] viewpoint, Edge startingEdge) {
		List<Edge> result = new ArrayList<>();
		if (Switches.PERFORM_SANITY_CHECKS) {
			assert startingEdge.isInHorizonOfViewPoint(viewpoint);
		}

		Edge currentEdge = startingEdge;
		do {
			result.add(currentEdge);
			currentEdge = findNextEdgeInHorizonOfViewPoint(viewpoint, currentEdge);
		} while (currentEdge != startingEdge);

		return result;
	}

	static List<Edge> findHorizonOfViewPoint(double[] viewpoint, Face startingFace) {
		Edge startingEdge = findEdgeInHorizonOfViewPoint(viewpoint, startingFace);
		return traceHorizonOfViewPoint(viewpoint, startingEdge);
	}

	/**
	 * Creates new {@link Face}s connecting (copies of) the {@link Edge}s of the
	 * given horizon to the given viewpoint, forming a pyramidlike shape.
	 * Connects the new faces into the mesh, while disconnecting all of the
	 * preexisting faces within the horizon from the mesh (they will still be
	 * accessible via the horizon edges afterward, which will no longer be part
	 * of the mesh either).
	 *
	 * @param horizon
	 *            a consecutive loop of edges (of the form returned by
	 *            {@link #traceHorizonOfViewPoint(double[], Edge)}
	 * @param viewpoint
	 *            the point outside the convex hull that {@code horizon} is the
	 *            horizon of
	 * @param inside
	 *            a point on the inside of the convex hull
	 * @return the new {@link Face}s directly corresponding to the edges in
	 *         {@code horizon}. Each face's {@link Face#edges edges}{@code [0]}
	 *         will be new {@link Edge} with the same vertices as the horizon
	 *         edge.
	 */
	static List<Face> connectHorizonToViewPoint(List<Edge> horizon, double[] viewpoint,
			double[] inside) {
		List<Face> newFaces = new ArrayList<>();
		for (Edge edge : horizon) {
			Face newFace = new Face(edge.prevVertex, edge.nextVertex, viewpoint, inside);
			newFaces.add(newFace);
			edge.oppositeEdge.setOppositeEdge(newFace.edges[0]);
		}
		Face prevFace = newFaces.get(newFaces.size() - 1);
		for (Face nextFace : newFaces) {
			prevFace.edges[1].setOppositeEdge(nextFace.edges[2]);
			prevFace = nextFace;
		}
		return newFaces;
	}

	static Set<Face> getAllFacesConnectedTo(Face face) {
		Set<Face> faces = new HashSet<>();
		Queue<Face> queue = new LinkedList<>();
		queue.add(face);
		while (!queue.isEmpty()) {
			face = queue.poll();
			if (faces.add(face)) {
				for (Edge edge : face.edges) {
					if (edge.oppositeEdge == null) {
						continue;
					}
					queue.add(edge.oppositeEdge.face);
				}
			}
		}
		return faces;
	}

	static void groupPointsWithFaces(Iterable<double[]> points, Iterable<Face> faces) {
		nextPoint: for (double[] point : points) {
			for (Face face : faces) {
				if (face.isVisibleFromPoint(point)) {
					if (face.externalPoints == null) {
						face.externalPoints = new ArrayList<>();
					}
					face.externalPoints.add(point);
					continue nextPoint;
				}
			}
		}
	}

	static void transferPointsToNewFaces(Iterable<Face> oldFaces, Iterable<Face> newFaces) {
		for (Face oldFace : oldFaces) {
			if (oldFace.externalPoints != null) {
				groupPointsWithFaces(oldFace.externalPoints, newFaces);
				oldFace.externalPoints = null;
			}
		}
	}

	static Set<Face> createConvexHullFrom4Points(Collection<double[]> points) {
		Iterator<double[]> i = points.iterator();
		double[] vertex0 = i.next();
		double[] vertex1 = i.next();
		double[] vertex2 = i.next();
		double[] vertex3 = i.next();
		Face[] faces = Face.createTetrahedron(vertex0, vertex1, vertex2, vertex3);
		return new HashSet<>(Arrays.asList(faces));
	}

	/**
	 * Creates a convex hull containing all of the given points using the
	 * quickhull algorithm.
	 *
	 * @param points
	 *            four or more distinct, non-coplanar points, each of which is a
	 *            3-element {@code [x, y, z]} array containing no {@code NaN} or
	 *            infinite values.
	 * @return the {@link Face}s of the convex hull.
	 */
	public static Set<Face> createConvexHull(Collection<double[]> points) {
		if (points.size() < 4) {
			throw new IllegalArgumentException("points.size() must be at least 4");
		}
		if (points.size() == 4) {
			return createConvexHullFrom4Points(points);
		}

		double[][] initialVertices = pickInitialTetrahedronVertices(points);
		double[] inside = VectorMath.centroid(initialVertices);
		List<Face> initialFaces = Arrays.asList(Face.createTetrahedron(
				initialVertices[0], initialVertices[1],
				initialVertices[2], initialVertices[3]));

		groupPointsWithFaces(points, initialFaces);
		Queue<Face> remainingFaces = new LinkedList<>(initialFaces);

		Face referenceFace = remainingFaces.peek();

		while (!remainingFaces.isEmpty()) {
			Face face = remainingFaces.poll();
			if (face.externalPoints == null) {
				continue;
			}

			double[] viewpoint = face.findFarthestPoint(face.externalPoints);
			if (viewpoint == null) {
				continue;
			}
			List<Edge> horizon = findHorizonOfViewPoint(viewpoint, face);
			List<Face> newFaces = connectHorizonToViewPoint(horizon, viewpoint, inside);

			Set<Face> oldFaces = getAllFacesConnectedTo(face);
			transferPointsToNewFaces(oldFaces, newFaces);
			remainingFaces.addAll(newFaces);
			referenceFace = newFaces.get(0);
		}

		Set<Face> convexHull = getAllFacesConnectedTo(referenceFace);

		if (Switches.PERFORM_SANITY_CHECKS) {
			for (double[] point : points) {
				for (Face face : convexHull) {
					assert !face.isVisibleFromPoint(point);
				}
			}
		}

		return convexHull;
	}
}
