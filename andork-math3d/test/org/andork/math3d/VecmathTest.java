package org.andork.math3d;

import org.andork.math3d.Vecmath.Circumcircle;
import org.andork.math3d.Vecmath.Circumsphere;
import org.junit.Assert;
import org.junit.Test;

public class VecmathTest {
	
	void assertInterpRot3(float[] a, float[] b, float f, double ex, double ey, double ez, float epsilon) {
		float[] out = {0, 0, 0};
		Vecmath.interpRot3(a, b, f, out);
		Assert.assertArrayEquals(new float[] { (float) ex, (float) ey, (float) ez }, out, epsilon);
	}

	void assertInterpRot3(float[] a, float[] b, float f, double ex, double ey, double ez) {
		assertInterpRot3(a, b, f, ex, ey, ez, 1e-8f);
	}

	void assertInterpRot3(float[] a, float[] b, float f, float[] expected) {
		assertInterpRot3(a, b, f, expected[0], expected[1], expected[2], 1e-8f);
	}

	@Test
	public void testInterpRot3() {
		float[] a = {0, 1, 0};
		float[] b = {(float) Math.sqrt(3) / 2, -.5f, 0};

		assertInterpRot3(a, b, .5f, Math.sqrt(3) / 2, 0.5, 0);
		assertInterpRot3(b, a, .5f, Math.sqrt(3) / 2, 0.5, 0);

		assertInterpRot3(a, b, .25f, 0.5, Math.sqrt(3) / 2, 0);
		assertInterpRot3(b, a, .75f, 0.5, Math.sqrt(3) / 2, 0);
		
		Vecmath.setf(a, 0, 0, 1);
		float[] expected = {0, 0, 0};
		Vecmath.interp3(a, b, .5f, expected);
		Vecmath.normalize3(expected);
		assertInterpRot3(a, b, .5f, expected);
		assertInterpRot3(b, a, .5f, expected);
		assertInterpRot3(a, b, 0, a);
		assertInterpRot3(a, b, 1, b);
		assertInterpRot3(b, a, 0, b);
		assertInterpRot3(b, a, 1, a);
	}
	
	@Test
	public void testCircumcircle() {
		Circumcircle out = new Circumcircle();
		float[] A = {1, 1};
		float[] B = {0, 1};
		float[] C = {1, 0};
		Vecmath.circumcircle(A, B, C, out);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance2sq(A, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance2sq(B, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance2sq(C, out.center), 1e-6f);
		Vecmath.circumcircle(B, A, C, out);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance2sq(A, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance2sq(B, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance2sq(C, out.center), 1e-6f);
		Vecmath.circumcircle(B, C, A, out);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance2sq(A, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance2sq(B, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance2sq(C, out.center), 1e-6f);
		Vecmath.circumcircle(C, B, A, out);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance2sq(A, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance2sq(B, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance2sq(C, out.center), 1e-6f);
		
		float[] E = {0, 0, 0};
		float[] F = {1, 0, 0};
		float[] G = {2, 0, 0};
		Vecmath.circumcircle(E, F, G, out);
		Assert.assertTrue(Float.isNaN(out.center[0]));
		Assert.assertTrue(Float.isNaN(out.center[1]));
		Assert.assertTrue(Float.isNaN(out.radiusSquared));
	}
	
	@Test
	public void testCircumsphere() {
		Circumsphere out = new Circumsphere();
		float[] A = {1, 1, 1};
		float[] B = {0, 1, 1};
		float[] C = {1, 0, 1};
		float[] D = {1, 1, 0};
		Vecmath.circumsphere(A, B, C, D, out);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(A, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(B, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(C, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(D, out.center), 1e-6f);
		Vecmath.circumsphere(B, A, C, D, out);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(A, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(B, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(C, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(D, out.center), 1e-6f);
		Vecmath.circumsphere(B, C, A, D, out);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(A, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(B, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(C, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(D, out.center), 1e-6f);
		Vecmath.circumsphere(B, C, D, A, out);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(A, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(B, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(C, out.center), 1e-6f);
		Assert.assertEquals(out.radiusSquared, Vecmath.distance3sq(D, out.center), 1e-6f);

		float[] E = {0, 0, 0};
		float[] F = {1, 0, 0};
		float[] G = {0, 1, 0};
		float[] H = {1, 1, 0};
		Vecmath.circumsphere(E, F, G, H, out);
		Assert.assertTrue(Float.isNaN(out.center[0]));
		Assert.assertTrue(Float.isNaN(out.center[1]));
		Assert.assertTrue(Float.isNaN(out.center[2]));
		Assert.assertTrue(Float.isNaN(out.radiusSquared));
	}
}
