package org.andork.quickhull3d;

public class VectorMath {
	public static double[] centroid(double[]... points) {
		double[] centroid = { 0, 0, 0 };
		for (double[] point : points) {
			centroid[0] += point[0];
			centroid[1] += point[1];
			centroid[2] += point[2];
		}
		double factor = 1.0 / points.length;
		centroid[0] *= factor;
		centroid[1] *= factor;
		centroid[2] *= factor;
		return centroid;
	}

	public static double distanceSquared(double[] point0, double[] point1) {
		double dx = point0[0] - point1[0];
		double dy = point0[1] - point1[1];
		double dz = point0[2] - point1[2];
		return dx * dx + dy * dy + dz * dz;
	}

	public static double dotProduct(double[] vector0, double[] vector1) {
		return vector0[0] * vector1[0] +
				vector0[1] * vector1[1] +
				vector0[2] * vector1[2];
	}

	public static double lengthOf(double[] vector) {
		return Math.sqrt(dotProduct(vector, vector));
	}

	public static void normalize(double[] normal) {
		double inverseLength = 1.0 / lengthOf(normal);
		normal[0] *= inverseLength;
		normal[1] *= inverseLength;
		normal[2] *= inverseLength;
	}

	/**
	 * Creates a normal from the vertices of a triangle. Doesn't ensure that the
	 * normal has unit length.
	 */
	public static double[] triangleNormal(double[] vertex0, double[] vertex1, double[] vertex2) {
		double ux = vertex2[0] - vertex0[0];
		double uy = vertex2[1] - vertex0[1];
		double uz = vertex2[2] - vertex0[2];
		double vx = vertex1[0] - vertex0[0];
		double vy = vertex1[1] - vertex0[1];
		double vz = vertex1[2] - vertex0[2];
		double[] normal = new double[] {
				uy * vz - uz * vy,
				uz * vx - ux * vz,
				ux * vy - uy * vx };
		return normal;
	}
}
