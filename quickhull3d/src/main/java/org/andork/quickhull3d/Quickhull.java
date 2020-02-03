package org.andork.quickhull3d;

import java.lang.reflect.Array;
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
	 *         If the given iterable produces no points, all elements of the result
	 *         will be {@code null}. The same point may occur up to 3 times in the
	 *         array, or more in cases that quickhull is not applicable to (less
	 *         than 4 points, or all points have same x, y, or z).
	 */
	static <V extends Vertex> V[] getFarthestPointsOnEachAxis(Iterable<V> points) {
		Iterator<V> i = points.iterator();
		V first = i.next();

		@SuppressWarnings("unchecked")
		V[] result = (V[]) Array.newInstance(first.getClass(), 6);

		Arrays.fill(result, first);

		while (i.hasNext()) {
			V point = i.next();
			if (point.x() < result[0].x()) {
				result[0] = point;
			}
			if (point.y() < result[1].y()) {
				result[1] = point;
			}
			if (point.z() < result[2].z()) {
				result[2] = point;
			}
			if (point.x() > result[3].x()) {
				result[3] = point;
			}
			if (point.y() > result[4].y()) {
				result[4] = point;
			}
			if (point.z() > result[5].z()) {
				result[5] = point;
			}
		}

		return result;
	}

	/**
	 * Chooses four vertices the given points that make a good starting tetrahedron
	 * for the quickhull algorithm.
	 *
	 * @param points the points the user wants to ultimately make a convex hull out
	 *               of.
	 * @return an array containing the four {@link Face}s of the tetrahedron.
	 */
	static <V extends Vertex> V[] pickInitialTetrahedronVertices(Iterable<V> points) {
		V[] extremePoints = getFarthestPointsOnEachAxis(points);
		@SuppressWarnings("unchecked")
		V[] vertices = (V[]) Array.newInstance(extremePoints.getClass().getComponentType(), 4);

		// pick the farthest-apart extreme points for the first two vertices
		double baseLineDistance = 0;
		for (int i = 0; i < extremePoints.length; i++) {
			for (int j = i + 1; j < extremePoints.length; j++) {
				if (extremePoints[i] == extremePoints[j]) {
					continue;
				}
				double distance = Vertex.distanceSquared(extremePoints[i], extremePoints[j]);
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
			V point = extremePoints[i];
			if (point == vertices[0] || point == vertices[1]) {
				continue;
			}
			double distance = Vertex.distanceSquared(point, vertices[0]) + Vertex.distanceSquared(point, vertices[1]);
			if (distance > thirdPointDistance) {
				vertices[2] = point;
				thirdPointDistance = distance;
			}
		}
		// if there are only two extreme points (because each was at the min or max of 3
		// axes)
		// then pick a third vertex from all of the input points instead
		if (vertices[2] == null) {
			for (V point : points) {
				double distance =
					Vertex.distanceSquared(point, vertices[0]) + Vertex.distanceSquared(point, vertices[1]);
				if (distance > thirdPointDistance) {
					vertices[2] = point;
					thirdPointDistance = distance;
				}
			}
		}

		// finally, pick the point farthest from the triangle as the fourth vertex
		double fourthPointDistance = 0;
		double[] normal = Vertex.triangleNormal(vertices[0], vertices[1], vertices[2]);
		double[] difference = new double[3];
		VectorMath.normalize(normal);
		for (V point : points) {
			difference[0] = point.x() - vertices[0].x();
			difference[1] = point.y() - vertices[0].y();
			difference[2] = point.z() - vertices[0].z();
			double distance = Math.abs(VectorMath.dotProduct(normal, difference));
			if (distance > fourthPointDistance) {
				vertices[3] = point;
				fourthPointDistance = distance;
			}
		}

		return vertices;
	}

	/**
	 * Given a viewpoint and a starting {@link Face}, finds an edge in the horizon
	 * of the viewpoint.
	 *
	 * @param viewpoint    a point outside the convex hull.
	 * @param startingFace a {@code Face}
	 *                     {@linkplain Face#isWithinHorizonForPoint(double[]) within
	 *                     the horizon} of {@code viewpoint}.
	 * @return a {@link Edge} {@link Edge#isInHorizonOfViewPoint(double[]) in the
	 *         horizon} of {@code viewpoint}.
	 */
	static <V extends Vertex> Edge<V> findEdgeInHorizonOfViewPoint(V viewpoint, Face<V> startingFace) {
		if (Switches.PERFORM_SANITY_CHECKS) {
			assert startingFace.isVisibleFromPoint(viewpoint);
		}
		class PriorityNode implements Comparable<PriorityNode> {
			final Edge<V> edge;
			final double distance;

			public PriorityNode(Edge<V> edge) {
				this.edge = edge;
				distance = edge.face.distanceToPoint(viewpoint);
			}

			@Override
			public int compareTo(PriorityNode o) {
				return Double.compare(distance, o.distance);
			}
		}

		// Search outward from startingFace trying edges whose face has the least
		// perpendicular
		// distance to viewpoint first, because those should be closer to the horizon
		PriorityQueue<PriorityNode> queue = new PriorityQueue<>();
		Set<Edge<V>> visited = new HashSet<>();
		for (Edge<V> edge : startingFace.edges) {
			queue.add(new PriorityNode(edge));
			visited.add(edge);
		}

		while (!queue.isEmpty()) {
			PriorityNode node = queue.poll();
			Edge<V> edge = node.edge;
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
	 * Given a viewpoint and an edge known to be in its horizon, finds the next edge
	 * of the horizon, which is one of the edges connected to the given edge's
	 * {@link Edge#nextVertex nextVertex}.
	 */
	static <V extends Vertex> Edge<V> findNextEdgeInHorizonOfViewPoint(V viewpoint, Edge<V> currentEdge) {
		Edge<V> candidateEdge = currentEdge.nextEdge;
		while (!candidateEdge.isInHorizonOfViewPoint(viewpoint)) {
			if (candidateEdge == currentEdge.oppositeEdge || candidateEdge == currentEdge.oppositeEdge) {
				throw new RuntimeException("failed to trace horizon!");
			}
			candidateEdge = candidateEdge.oppositeEdge.nextEdge;
		}
		return candidateEdge;
	}

	/**
	 * Given a viewpoint and an edge known to be in its horizon, finds the rest of
	 * the horizon for the viewpoint.
	 *
	 * @param viewpoint    a point outside the convex hull
	 * @param startingEdge an edge known to be in the horizon of {@code viewpoint}
	 * @return a list of all edges in the horizon for {@code viewpoint}, which is a
	 *         closed loop: each edge's {@link Edge#nextVertex nextVertex} is the
	 *         {@link Edge#prevVertex prevVertex} of the next edge in the list, and
	 *         the {@code nextVertex} of the last edge in the list is the
	 *         {@code prevVertex} of the first.
	 */
	static <V extends Vertex> List<Edge<V>> traceHorizonOfViewPoint(V viewpoint, Edge<V> startingEdge) {
		List<Edge<V>> result = new ArrayList<>();
		if (Switches.PERFORM_SANITY_CHECKS) {
			assert startingEdge.isInHorizonOfViewPoint(viewpoint);
		}

		Edge<V> currentEdge = startingEdge;
		do {
			result.add(currentEdge);
			currentEdge = findNextEdgeInHorizonOfViewPoint(viewpoint, currentEdge);
		} while (currentEdge != startingEdge);

		return result;
	}

	static <V extends Vertex> List<Edge<V>> findHorizonOfViewPoint(V viewpoint, Face<V> startingFace) {
		Edge<V> startingEdge = findEdgeInHorizonOfViewPoint(viewpoint, startingFace);
		return traceHorizonOfViewPoint(viewpoint, startingEdge);
	}

	/**
	 * Creates new {@link Face}s connecting (copies of) the {@link Edge}s of the
	 * given horizon to the given viewpoint, forming a pyramidlike shape. Connects
	 * the new faces into the mesh, while disconnecting all of the preexisting faces
	 * within the horizon from the mesh (they will still be accessible via the
	 * horizon edges afterward, which will no longer be part of the mesh either).
	 *
	 * @param horizon   a consecutive loop of edges (of the form returned by
	 *                  {@link #traceHorizonOfViewPoint(double[], Edge)}
	 * @param viewpoint the point outside the convex hull that {@code horizon} is
	 *                  the horizon of
	 * @param inside    a point on the inside of the convex hull
	 * @return the new {@link Face}s directly corresponding to the edges in
	 *         {@code horizon}. Each face's {@link Face#edges edges}{@code [0]} will
	 *         be new {@link Edge} with the same vertices as the horizon edge.
	 */
	static <V extends Vertex> List<Face<V>> connectHorizonToViewPoint(
		List<Edge<V>> horizon,
		V viewpoint,
		double[] inside) {
		List<Face<V>> newFaces = new ArrayList<>();
		for (Edge<V> edge : horizon) {
			Face<V> newFace = new Face<V>(edge.prevVertex, edge.nextVertex, viewpoint, inside);
			newFaces.add(newFace);
			edge.oppositeEdge.setOppositeEdge(newFace.edges[0]);
		}
		Face<V> prevFace = newFaces.get(newFaces.size() - 1);
		for (Face<V> nextFace : newFaces) {
			prevFace.edges[1].setOppositeEdge(nextFace.edges[2]);
			prevFace = nextFace;
		}
		return newFaces;
	}

	static <V extends Vertex> Set<Face<V>> getAllFacesConnectedTo(Face<V> face) {
		Set<Face<V>> faces = new HashSet<>();
		Queue<Face<V>> queue = new LinkedList<>();
		queue.add(face);
		while (!queue.isEmpty()) {
			face = queue.poll();
			if (faces.add(face)) {
				for (Edge<V> edge : face.edges) {
					if (edge.oppositeEdge == null) {
						continue;
					}
					queue.add(edge.oppositeEdge.face);
				}
			}
		}
		return faces;
	}

	static <V extends Vertex> void groupPointsWithFaces(Iterable<V> points, Iterable<Face<V>> faces) {
		nextPoint: for (V point : points) {
			for (Face<V> face : faces) {
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

	static <V extends Vertex> void transferPointsToNewFaces(Iterable<Face<V>> oldFaces, Iterable<Face<V>> newFaces) {
		for (Face<V> oldFace : oldFaces) {
			if (oldFace.externalPoints != null) {
				groupPointsWithFaces(oldFace.externalPoints, newFaces);
				oldFace.externalPoints = null;
			}
		}
	}

	static <V extends Vertex> Set<Face<V>> createConvexHullFrom4Points(Collection<V> points) {
		Iterator<V> i = points.iterator();
		V vertex0 = i.next();
		V vertex1 = i.next();
		V vertex2 = i.next();
		V vertex3 = i.next();
		Face<V>[] faces = Face.createTetrahedron(vertex0, vertex1, vertex2, vertex3);
		return new HashSet<>(Arrays.asList(faces));
	}

	/**
	 * Creates a convex hull containing all of the given points using the quickhull
	 * algorithm.
	 *
	 * @param points four or more distinct, non-coplanar points, each of which is a
	 *               3-element {@code [x, y, z]} array containing no {@code NaN} or
	 *               infinite values.
	 * @return the {@link Face}s of the convex hull.
	 */
	public static <V extends Vertex> Set<Face<V>> createConvexHull(Collection<V> points) {
		if (points.size() < 4) {
			throw new IllegalArgumentException("points.size() must be at least 4");
		}
		if (points.size() == 4) {
			return createConvexHullFrom4Points(points);
		}

		V[] initialVertices = pickInitialTetrahedronVertices(points);
		double[] inside = Vertex.centroid(initialVertices);
		List<Face<V>> initialFaces =
			Arrays
				.asList(
					Face
						.createTetrahedron(
							initialVertices[0],
							initialVertices[1],
							initialVertices[2],
							initialVertices[3]));

		groupPointsWithFaces(points, initialFaces);
		Queue<Face<V>> remainingFaces = new LinkedList<>(initialFaces);

		Face<V> referenceFace = remainingFaces.peek();

		while (!remainingFaces.isEmpty()) {
			Face<V> face = remainingFaces.poll();
			if (face.externalPoints == null) {
				continue;
			}

			V viewpoint = face.findFarthestPoint(face.externalPoints);
			if (viewpoint == null) {
				continue;
			}
			List<Edge<V>> horizon = findHorizonOfViewPoint(viewpoint, face);
			List<Face<V>> newFaces = connectHorizonToViewPoint(horizon, viewpoint, inside);

			Set<Face<V>> oldFaces = getAllFacesConnectedTo(face);
			transferPointsToNewFaces(oldFaces, newFaces);
			remainingFaces.addAll(newFaces);
			referenceFace = newFaces.get(0);
		}

		Set<Face<V>> convexHull = getAllFacesConnectedTo(referenceFace);

		if (Switches.PERFORM_SANITY_CHECKS) {
			for (V point : points) {
				for (Face<V> face : convexHull) {
					assert !face.isVisibleFromPoint(point);
				}
			}
		}

		return convexHull;
	}
}
