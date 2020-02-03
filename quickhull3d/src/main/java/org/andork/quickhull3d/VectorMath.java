package org.andork.quickhull3d;

public class VectorMath {
	public static double dotProduct(double[] vector0, double[] vector1) {
		return vector0[0] * vector1[0] + vector0[1] * vector1[1] + vector0[2] * vector1[2];
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
}
