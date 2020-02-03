package org.andork.quickhull3d;

import java.util.List;

import org.andork.robustorientation.RobustOrientation;

/**
 * A triangular face that's part of a convex hull.<br>
 *
 * Quickhull begins by creating a tetrahedral mesh of 4 {@code Face}s, and then
 *
 * @author andy
 */
public class Face<V extends Vertex> {
	/**
	 * The three half edges of this face. The neighboring faces are
	 * {@code edges[0].oppositeEdge.face}, {@code edges[1].oppositeEdge.face}, and
	 * {@code edges[2].oppositeEdge.face}, provided none of the
	 * {@code oppositeEdge}s are {@code null}.
	 */
	public final Edge<V>[] edges;
	/**
	 * The unit normal vector for this face, pointing toward the outside of the
	 * convex hull.
	 */
	public final double[] normal;
	/**
	 * Some, not necessarily all, points outside of the convex hull that this face
	 * {@linkplain #isVisibleFromPoint(V) is visible from}.
	 */
	List<V> externalPoints;

	/**
	 * Creates a face with the given 3 vertices. They must be ordered so that the
	 * orientation of the tuple (vertex0, vertex1, vertex2, inside) is negative.
	 *
	 * @param vertex0 the first vertex.
	 * @param vertex1 the second vertex.
	 * @param vertex2 the third vertex.
	 * @param inside  a point that is toward the inside of the convex hull from this
	 *                face (it is only used to check if the vertices need to be
	 *                reordered -- it will not be stored).
	 */
	@SuppressWarnings("unchecked")
	Face(V vertex0, V vertex1, V vertex2, V inside) {
		double orientation =
			RobustOrientation
				.orientation4(
					vertex0.x(),
					vertex0.y(),
					vertex0.z(),
					vertex1.x(),
					vertex1.y(),
					vertex1.z(),
					vertex2.x(),
					vertex2.y(),
					vertex2.z(),
					inside.x(),
					inside.y(),
					inside.z());
		if (orientation == 0) {
			throw new IllegalArgumentException("inside must not be coplanar with vertices");
		}
		if (Switches.PERFORM_SANITY_CHECKS) {
			assert orientation < 0;
		}
		edges =
			new Edge[]
			{
				new Edge<V>(this, vertex0, vertex1),
				new Edge<V>(this, vertex1, vertex2),
				new Edge<V>(this, vertex2, vertex0) };
		edges[0].prevEdge = edges[1].nextEdge = edges[2];
		edges[1].prevEdge = edges[2].nextEdge = edges[0];
		edges[2].prevEdge = edges[0].nextEdge = edges[1];

		normal = computeNormalVector(vertex0, vertex1, vertex2, inside);
	}

	/**
	 * Creates a face with the given 3 vertices. They must be ordered so that the
	 * orientation of the tuple (vertex0, vertex1, vertex2, inside) is negative.
	 *
	 * @param vertex0 the first vertex.
	 * @param vertex1 the second vertex.
	 * @param vertex2 the third vertex.
	 * @param inside  a point that is toward the inside of the convex hull from this
	 *                face (it is only used to check if the vertices need to be
	 *                reordered -- it will not be stored).
	 */
	@SuppressWarnings("unchecked")
	Face(V vertex0, V vertex1, V vertex2, double[] inside) {
		double orientation =
			RobustOrientation
				.orientation4(
					vertex0.x(),
					vertex0.y(),
					vertex0.z(),
					vertex1.x(),
					vertex1.y(),
					vertex1.z(),
					vertex2.x(),
					vertex2.y(),
					vertex2.z(),
					inside[0],
					inside[1],
					inside[2]);
		if (orientation == 0) {
			throw new IllegalArgumentException("inside must not be coplanar with vertices");
		}
		if (Switches.PERFORM_SANITY_CHECKS) {
			assert orientation < 0;
		}
		edges =
			new Edge[]
			{
				new Edge<V>(this, vertex0, vertex1),
				new Edge<V>(this, vertex1, vertex2),
				new Edge<V>(this, vertex2, vertex0) };
		edges[0].prevEdge = edges[1].nextEdge = edges[2];
		edges[1].prevEdge = edges[2].nextEdge = edges[0];
		edges[2].prevEdge = edges[0].nextEdge = edges[1];

		normal = computeNormalVector(vertex0, vertex1, vertex2, inside);
	}

	static <V extends Vertex> double[] computeNormalVector(V vertex0, V vertex1, V vertex2, double[] inside) {
		double[] normal = Vertex.triangleNormal(vertex0, vertex1, vertex2);
		if (Switches.PERFORM_SANITY_CHECKS) {
			// check that normal is facing away from inside point
			double dotProduct =
				normal[0] * (vertex0.x() - inside[0])
					+ normal[1] * (vertex0.y() - inside[1])
					+ normal[2] * (vertex0.z() - inside[2]);
			assert dotProduct > 0;
		}
		VectorMath.normalize(normal);
		return normal;
	}

	static <V extends Vertex> double[] computeNormalVector(V vertex0, V vertex1, V vertex2, V inside) {
		double[] normal = Vertex.triangleNormal(vertex0, vertex1, vertex2);
		if (Switches.PERFORM_SANITY_CHECKS) {
			// check that normal is facing away from inside point
			double dotProduct =
				normal[0] * (vertex0.x() - inside.x())
					+ normal[1] * (vertex0.y() - inside.y())
					+ normal[2] * (vertex0.z() - inside.z());
			assert dotProduct > 0;
		}
		VectorMath.normalize(normal);
		return normal;
	}

	/**
	 * Creates a tetrahedral mesh of four faces from the given four vertices.
	 *
	 * @return the four interconnected {@link Face}s of the tetrahedron.
	 */
	@SuppressWarnings("unchecked")
	public static <V extends Vertex> Face<V>[] createTetrahedron(V vertex0, V vertex1, V vertex2, V vertex3) {
		double orientation =
			RobustOrientation
				.orientation4(
					vertex0.x(),
					vertex0.y(),
					vertex0.z(),
					vertex1.x(),
					vertex1.y(),
					vertex1.z(),
					vertex2.x(),
					vertex2.y(),
					vertex2.z(),
					vertex3.x(),
					vertex3.y(),
					vertex3.z());
		if (orientation == 0) {
			throw new RuntimeException("vertices must not be coplanar");
		}
		if (orientation > 0) {
			V swap = vertex2;
			vertex2 = vertex3;
			vertex3 = swap;
		}
		@SuppressWarnings("rawtypes")
		Face[] faces =
			{
				new Face<V>(vertex0, vertex1, vertex2, vertex3),
				new Face<V>(vertex1, vertex3, vertex2, vertex0),
				new Face<V>(vertex2, vertex3, vertex0, vertex1),
				new Face<V>(vertex3, vertex1, vertex0, vertex2) };
		faces[0].edges[0].setOppositeEdge(faces[3].edges[1]);
		faces[1].edges[0].setOppositeEdge(faces[3].edges[0]);
		faces[2].edges[0].setOppositeEdge(faces[1].edges[1]);
		faces[0].edges[1].setOppositeEdge(faces[1].edges[2]);
		faces[0].edges[2].setOppositeEdge(faces[2].edges[2]);
		faces[3].edges[2].setOppositeEdge(faces[2].edges[1]);
		return faces;
	}

	/**
	 * Determines if the outside of this face is visible from the given point.
	 */
	public boolean isVisibleFromPoint(V point) {
		return RobustOrientation
			.orientation4(
				edges[0].prevVertex.x(),
				edges[0].prevVertex.y(),
				edges[0].prevVertex.z(),
				edges[1].prevVertex.x(),
				edges[1].prevVertex.y(),
				edges[1].prevVertex.z(),
				edges[2].prevVertex.x(),
				edges[2].prevVertex.y(),
				edges[2].prevVertex.z(),
				point.x(),
				point.y(),
				point.z()) > 0;
	}

	/**
	 * Determines if the outside of this face is visible from the given point
	 * <strong>or this face is coplanar with the given point</strong>. The latter
	 * helps minimize the number of coplanar faces created during the execution of
	 * quickhull if there are numerous coplanar points.
	 */
	public boolean isWithinHorizonForPoint(V point) {
		return RobustOrientation
			.orientation4(
				edges[0].prevVertex.x(),
				edges[0].prevVertex.y(),
				edges[0].prevVertex.z(),
				edges[1].prevVertex.x(),
				edges[1].prevVertex.y(),
				edges[1].prevVertex.z(),
				edges[2].prevVertex.x(),
				edges[2].prevVertex.y(),
				edges[2].prevVertex.z(),
				point.x(),
				point.y(),
				point.z()) >= 0;
	}

	/**
	 * Finds the point among the given points that's the farthest in the direction
	 * of this face's {@link #normal} vector.
	 *
	 * @param points the points to search.
	 * @return the point that's farthest away, or {@code null} if the outside of
	 *         this face is not visible from any of the {@code points}.
	 */
	public V findFarthestPoint(Iterable<V> points) {
		double farthestDistance = 0;
		V farthest = null;
		for (V point : points) {
			double distance = distanceToPoint(point);
			if (distance > farthestDistance) {
				farthest = point;
				farthestDistance = distance;
			}
		}
		if (farthest == null) {
			for (V point : points) {
				double orientation =
					RobustOrientation
						.orientation4(
							edges[0].prevVertex.x(),
							edges[0].prevVertex.y(),
							edges[0].prevVertex.z(),
							edges[1].prevVertex.x(),
							edges[1].prevVertex.y(),
							edges[1].prevVertex.z(),
							edges[2].prevVertex.x(),
							edges[2].prevVertex.y(),
							edges[2].prevVertex.z(),
							point.x(),
							point.y(),
							point.z());
				if (orientation > farthestDistance) {
					farthest = point;
					farthestDistance = orientation;
				}
			}
		}
		if (Switches.PERFORM_SANITY_CHECKS) {
			if (farthest != null) {
				assert isVisibleFromPoint(farthest);
			}
		}
		return farthest;
	}

	/**
	 * Computes the distance along the normal vector to the given point.
	 *
	 * @param point a point.
	 * @return the distance of {@code point} from this face.
	 */
	public double distanceToPoint(V point) {
		V vertex = edges[0].prevVertex;
		double dx = point.x() - vertex.x();
		double dy = point.y() - vertex.y();
		double dz = point.z() - vertex.z();
		return normal[0] * dx + normal[1] * dy + normal[2] * dz;
	}

	@Override
	public String toString() {
		return "Face[" + edges[0].prevVertex + ", " + edges[1].prevVertex + ", " + edges[2].prevVertex + "]";
	}
}
