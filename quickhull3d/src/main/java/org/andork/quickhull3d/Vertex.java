package org.andork.quickhull3d;

public interface Vertex {
	public double x();

	public double y();

	public double z();

	/**
	 * Creates a normal from the vertices of a triangle. Doesn't ensure that the
	 * normal has unit length.
	 */
	static <V extends Vertex> double[] triangleNormal(V vertex0, V vertex1, V vertex2) {
		double ux = vertex2.x() - vertex0.x();
		double uy = vertex2.y() - vertex0.y();
		double uz = vertex2.z() - vertex0.z();
		double vx = vertex1.x() - vertex0.x();
		double vy = vertex1.y() - vertex0.y();
		double vz = vertex1.z() - vertex0.z();
		double[] normal = new double[] { uy * vz - uz * vy, uz * vx - ux * vz, ux * vy - uy * vx };
		return normal;
	}

	public static double[] centroid(Vertex... points) {
		double[] centroid = { 0, 0, 0 };
		for (Vertex point : points) {
			centroid[0] += point.x();
			centroid[1] += point.y();
			centroid[2] += point.z();
		}
		double factor = 1.0 / points.length;
		centroid[0] *= factor;
		centroid[1] *= factor;
		centroid[2] *= factor;
		return centroid;
	}

	public static double distanceSquared(Vertex a, Vertex b) {
		double dx = a.x() - b.x();
		double dy = a.y() - b.y();
		double dz = a.z() - b.z();
		return dx * dx + dy * dy + dz * dz;
	}

	public static boolean equals(Vertex a, Vertex b) {
		return a.x() == b.x() && a.y() == b.y() && a.z() == b.z();
	}

}
