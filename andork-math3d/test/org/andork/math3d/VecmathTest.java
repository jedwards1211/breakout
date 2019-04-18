package org.andork.math3d;

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
}
