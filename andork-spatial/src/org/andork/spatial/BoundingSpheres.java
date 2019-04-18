package org.andork.spatial;

import java.util.Arrays;

public class BoundingSpheres {

	private BoundingSpheres() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Determines if a sphere contains a point.
	 * @param sphere the sphere in form [(center) x, y, z, radiusSquared]
	 * @param point the point in form [x, y, z]
	 * @return {@code true} iff the point is inside the sphere
	 */
	public static boolean contains(float[] sphere, float[] point) {
		float dx = point[0] - sphere[0];
		float dy = point[1] - sphere[1];
		float dz = point[2] - sphere[2];
		return dx * dx + dy * dy + dz * dz <= point[3];
	}

	/**
	 * Computes the bounding sphere of the given points using Jack Ritter's algorithm.
	 * Not the exact smallest bounding sphere but, much more practical speed than exact
	 * methods.
	 * @param points the points to get the bounding sphere of
	 * @return the resulting bounding sphere
	 */	
	public static float[] ritterBoundingSphere(float[][] points) {
		return ritterBoundingSphere(Arrays.asList(points));
	}

	/**
	 * Computes the bounding sphere of the given points using Jack Ritter's algorithm.
	 * Not the exact smallest bounding sphere but, much more practical speed than exact
	 * methods.
	 * @param points the points to get the bounding sphere of
	 * @return the resulting bounding sphere
	 */
	public static float[] ritterBoundingSphere(Iterable<float[]> points) {
		float[] sphere = new float[4];
		return ritterBoundingSphere(points, sphere);
	}

	/**
	 * Computes the bounding sphere of the given points using Jack Ritter's algorithm.
	 * Not the exact smallest bounding sphere but, much more practical speed than exact
	 * methods.
	 * @param points the points to get the bounding sphere of
	 * @param out the array to store the output sphere in, in the form [(center) x, y, z, radiusSquared]
	 * @return {@code out}, for convenience
	 */
	public static float[] ritterBoundingSphere(float[][] points, float[] out) {
		return ritterBoundingSphere(Arrays.asList(points), out);
	}
	
	/**
	 * Computes the bounding sphere of the given points using Jack Ritter's algorithm.
	 * Not the exact smallest bounding sphere but, much more practical speed than exact
	 * methods.
	 * @param points the points to get the bounding sphere of
	 * @param out the array to store the output sphere in, in the form [(center) x, y, z, radiusSquared]
	 * @return {@code out}, for convenience
	 */
	public static float[] ritterBoundingSphere(Iterable<float[]> points, float[] out) {
		float[] first = null;
		for (float[] point : points) {
			first = point;
			break;
		}
		if (first == null) {
			Arrays.fill(out, Float.NaN);
			return out;
		}
		
		float x0 = first[0];
		float y0 = first[1];
		float z0 = first[2];

		// find point x1 farthest from x0 
		float x1 = Float.NaN, y1 = Float.NaN, z1 = Float.NaN, d1 = 0;
		for (float[] point : points) {
			float dx = point[0] - x0;
			float dy = point[1] - y0;
			float dz = point[2] - z0;
			float d = dx * dx + dy * dy + dz * dz;
			if (d > d1) {
				x1 = point[0];
				y1 = point[1];
				z1 = point[2];
				d1 = d;
			}
		}
		if (d1 == 0) {
			out[0] = (float) x0;
			out[1] = (float) y0;
			out[2] = (float) z0;
			out[3] = 0;
			return out;
		}

		
		// find point x2 farthest from x1 
		float x2 = Float.NaN, y2 = Float.NaN, z2 = Float.NaN, d2 = 0;
		for (float[] point : points) {
			float dx = point[0] - x1;
			float dy = point[1] - y1;
			float dz = point[2] - z1;
			float d = dx * dx + dy * dy + dz * dz;
			if (d > d2) {
				x2 = point[0];
				y2 = point[1];
				z2 = point[2];
				d2 = d;
			}
		
		}

		// start with a sphere with diameter x1 to x2
		double x = (x1 + x2) / 2;
		double y = (y1 + y2) / 2;
		double z = (z1 + z2) / 2;
		double rsq = d2 / 4;
		double r = Math.sqrt(rsq);
		
		boolean enlarged = false;
		
		// check if each point is inside sphere
		// if a point is outside, enlarge sphere to contain it
		for (float[] point : points) {
			double dx = point[0] - x;
			double dy = point[1] - y;
			double dz = point[2] - z;
			double dsq = dx * dx + dy * dy + dz * dz;
			if (dsq > rsq) {
				enlarged = true;
				double d = Math.sqrt(dsq);
				r = (r + d) / 2;
				double factor = r / d;
				x = point[0] - dx * factor;
				y = point[1] - dy * factor;
				z = point[2] - dz * factor;
				rsq = r * r;
			}
		}
		
		if (enlarged) {
			// correct for floating point error
			for (float[] point : points) {
				float dx = point[0] - (float) x;
				float dy = point[1] - (float) y;
				float dz = point[2] - (float) z;
				float dsq = dx * dx + dy * dy + dz * dz;
				if (dsq > rsq) rsq = dsq;
			}
		}
		
		out[0] = (float) x;
		out[1] = (float) y;
		out[2] = (float) z;
		out[3] = (float) rsq;
		
		return out;
	}
}
