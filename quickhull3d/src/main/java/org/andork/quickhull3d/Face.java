package org.andork.quickhull3d;

import java.util.Arrays;
import java.util.List;

import org.andork.robustorientation.RobustOrientation;

/**
 * A triangular face that's part of a convex hull.<br>
 *
 * Quickhull begins by creating a tetrahedral mesh of 4 {@code Face}s, and then
 *
 * @author andy
 */
public class Face {
	/**
	 * The three half edges of this face. The neighboring faces are
	 * {@code edges[0].oppositeEdge.face}, {@code edges[1].oppositeEdge.face},
	 * and {@code edges[2].oppositeEdge.face}, provided none of the
	 * {@code oppositeEdge}s are {@code null}.
	 */
	public final Edge[] edges;
	/**
	 * The unit normal vector for this face, pointing toward the outside of the
	 * convex hull.
	 */
	public final double[] normal;
	/**
	 * Some, not necessarily all, points outside of the convex hull that this
	 * face {@linkplain #isVisibleFromPoint(double[]) is visible from}.
	 */
	List<double[]> externalPoints;

	/**
	 * Creates a face with the given 3 vertices. They must be ordered so that
	 * the orientation of the tuple (vertex0, vertex1, vertex2, inside) is
	 * negative.
	 *
	 * @param vertex0
	 *            the first vertex.
	 * @param vertex1
	 *            the second vertex.
	 * @param vertex2
	 *            the third vertex.
	 * @param inside
	 *            a point that is toward the inside of the convex hull from this
	 *            face (it is only used to check if the vertices need to be
	 *            reordered -- it will not be stored).
	 */
	Face(double[] vertex0, double[] vertex1, double[] vertex2, double[] inside) {
		double orientation = RobustOrientation.orientation4(vertex0, vertex1, vertex2, inside);
		if (orientation == 0) {
			throw new IllegalArgumentException("inside must not be coplanar with vertices");
		}
		if (Switches.PERFORM_SANITY_CHECKS) {
			assert orientation < 0;
		}
		edges = new Edge[] {
				new Edge(this, vertex0, vertex1),
				new Edge(this, vertex1, vertex2),
				new Edge(this, vertex2, vertex0) };
		edges[0].prevEdge = edges[1].nextEdge = edges[2];
		edges[1].prevEdge = edges[2].nextEdge = edges[0];
		edges[2].prevEdge = edges[0].nextEdge = edges[1];

		normal = computeNormalVector(vertex0, vertex1, vertex2, inside);
	}

	static double[] computeNormalVector(
			double[] vertex0, double[] vertex1, double[] vertex2, double[] inside) {
		double[] normal = VectorMath.triangleNormal(vertex0, vertex1, vertex2);
		if (Switches.PERFORM_SANITY_CHECKS) {
			// check that normal is facing away from inside point
			double dotProduct = normal[0] * (vertex0[0] - inside[0])
					+ normal[1] * (vertex0[1] - inside[1])
					+ normal[2] * (vertex0[2] - inside[2]);
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
	public static Face[] createTetrahedron(
			double[] vertex0, double[] vertex1, double[] vertex2, double[] vertex3) {
		double orientation = RobustOrientation.orientation4(vertex0, vertex1, vertex2, vertex3);
		if (orientation == 0) {
			throw new RuntimeException("vertices must not be coplanar");
		}
		if (orientation > 0) {
			double[] swap = vertex2;
			vertex2 = vertex3;
			vertex3 = swap;
		}
		Face[] faces = {
				new Face(vertex0, vertex1, vertex2, vertex3),
				new Face(vertex1, vertex3, vertex2, vertex0),
				new Face(vertex2, vertex3, vertex0, vertex1),
				new Face(vertex3, vertex1, vertex0, vertex2) };
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
	public boolean isVisibleFromPoint(double[] point) {
		return RobustOrientation.orientation4(
				edges[0].prevVertex, edges[1].prevVertex, edges[2].prevVertex, point) > 0;
	}

	/**
	 * Determines if the outside of this face is visible from the given point
	 * <strong>or this face is coplanar with the given point</strong>. The
	 * latter helps minimize the number of coplanar faces created during the
	 * execution of quickhull if there are numerous coplanar points.
	 */
	public boolean isWithinHorizonForPoint(double[] point) {
		return RobustOrientation.orientation4(
				edges[0].prevVertex, edges[1].prevVertex, edges[2].prevVertex, point) >= 0;
	}

	/**
	 * Finds the point among the given points that's the farthest in the
	 * direction of this face's {@link #normal} vector.
	 *
	 * @param points
	 *            the points to search.
	 * @return the point that's farthest away, or {@code null} if the outside of
	 *         this face is not visible from any of the {@code points}.
	 */
	public double[] findFarthestPoint(Iterable<double[]> points) {
		double farthestDistance = 0;
		double[] farthest = null;
		for (double[] point : points) {
			double distance = distanceToPoint(point);
			if (distance > farthestDistance) {
				farthest = point;
				farthestDistance = distance;
			}
		}
		if (farthest == null) {
			for (double[] point : points) {
				double orientation = RobustOrientation.orientation4(
						edges[0].prevVertex, edges[1].prevVertex, edges[2].prevVertex, point);
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
	 * @param point
	 *            a point.
	 * @return the distance of {@code point} from this face.
	 */
	public double distanceToPoint(double[] point) {
		double[] vertex = edges[0].prevVertex;
		double dx = point[0] - vertex[0];
		double dy = point[1] - vertex[1];
		double dz = point[2] - vertex[2];
		return normal[0] * dx + normal[1] * dy + normal[2] * dz;
	}

	@Override
	public String toString() {
		return "Face[" + Arrays.toString(edges[0].prevVertex)
				+ ", " + Arrays.toString(edges[1].prevVertex)
				+ ", " + Arrays.toString(edges[2].prevVertex)
				+ "]";
	}
}
