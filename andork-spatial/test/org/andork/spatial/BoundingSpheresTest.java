package org.andork.spatial;

import static org.andork.spatial.BoundingSpheres.ritterBoundingSphere;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class BoundingSpheresTest {
	@Test
	public void testZeroPoints() {
		Assert.assertArrayEquals(
			ritterBoundingSphere(new float[][] {}),
			new float[] { Float.NaN, Float.NaN, Float.NaN, Float.NaN },
			0f);
	}
	
	@Test
	public void testOnePoint() {
		Assert.assertArrayEquals(
			ritterBoundingSphere(new float[][] {{1, 2, 3}}),
			new float[] { 1, 2, 3, 0 },
			0f);	
	}
	
	@Test
	public void testTwoPoints() {
		Assert.assertArrayEquals(
			ritterBoundingSphere(new float[][] {{0, 0, 0}, {1, 1, 1}}),
			new float[] { 0.5f, 0.5f, 0.5f, 0.8660254f },
			0f);	
	}
	
	@Test
	public void testThreePointsEasy() {
		Assert.assertArrayEquals(
			ritterBoundingSphere(new float[][] {{0, 0, 0}, {1, 1, 1}, {0.6f, 0.4f, 0.3f}}),
			new float[] { 0.5f, 0.5f, 0.5f, 0.8660254f },
			0f);
	}
	
	public void sanityCheck(float[][] points) {
		float[] sphere = ritterBoundingSphere(points);
		
		float farthestDist = -1;
		float[] farthestPoint = null;
		for (float[] point : points) {
			float dx = sphere[0] - point[0];
			float dy = sphere[1] - point[1];
			float dz = sphere[2] - point[2];
			float d = dx * dx + dy * dy + dz * dz;
			if (d > farthestDist) {
				farthestDist = d;
				farthestPoint = point;
			}
		}
		if (farthestDist > sphere[3] * sphere[3]) {
			Assert.fail("expected farthest point " + Arrays.toString(farthestPoint) + 
				" distance sq " + farthestDist +
				" to be inside sphere " + Arrays.toString(sphere) +
				" radius sq " + sphere[3] * sphere[3]);
		}
		if (farthestDist < sphere[3] * sphere[3] * 0.99 * 0.99) {
			Assert.fail("expected farthest point " + Arrays.toString(farthestPoint) + 
				" distance squared " + farthestDist +
				" to be within 1% of " + Arrays.toString(sphere));
		}
	}
	
	@Test
	public void testTetrahedron() {
		sanityCheck(new float[][] {
			{0, 0, 0},
			{1, 1, 0},
			{1, 0, 1},
			{0, 1, 1}
		});
	}
	
	@Test
	public void testRandomPoints() {
		for (int rep = 0; rep < 100; rep++) {
			float[][] points = new float[100][3];
			for (int i = 0; i < points.length; i++) {
				float[] point = points[i];
				for (int k = 0; k < point.length; k++) {
					point[k] = (float) Math.random();
				}
			}
			sanityCheck(points);
		}
	}
}
