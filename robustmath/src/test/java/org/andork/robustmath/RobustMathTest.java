package org.andork.robustmath;


import static org.andork.robustmath.RobustMath.add;
import static org.andork.robustmath.RobustMath.multiply;
import static org.junit.Assert.assertArrayEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class RobustMathTest {
	static double[] n(double... values) {
		return values;
	}

	static void same(double[] actual, double[] expected) {
		assertArrayEquals(expected, actual, 0.0);
	}

	@Test
	public void testAdd() {
		same(add(n(1, 64), n(-1e-64, 1e64)), n(-1e-64, 65, 1e64));
		same(add(n(0), n(0)), n(0));
		same(add(n(0), n(1)), n(1));
		same(add(n(1, 1e64), n(1e-64, 2)), n(1e-64, 3, 1e64));

		same(add(n(1), n(1e-64, 1e-16)), n(1e-64, 1e-16, 1));

		same(add(n(0), n(1)), n(1));

		for (double i = -10; i <= 10; ++i) {
			for (double j = -10; j <= 10; ++j) {
				same(add(n(i), n(j)), n(i + j));
			}
		}

		//		ok(validate(sum(n(5.711861227349496e-133, 1e-116), n(5.711861227349496e-133, 1e-116))));

		double[] nois = new double[10];
		double[] expect = new double[10];
		for (int i = 0; i < 10; ++i) {
			nois[i] = Math.pow(2, -1000 + 53 * i);
			expect[i] = Math.pow(2, -999 + 53 * i);
		}
		double[] x = add(nois, nois);
		same(x, expect);
		//		ok(validate(x));

		same(add(n(0), n(1, 1e64)), n(1, 1e64));

		double[] s = n(0);
		double[] q = n(0);
		ArrayList<Double> seq = new ArrayList<>();
		for (int i = 0; i < 1000; ++i) {
			double h = Math.random() * Math.pow(2, Math.random() * 1800 - 900);
			seq.add(h);
			s = add(s, n(h));
			//			ok(validate(s));
			q = add(n(-h), q);
			//			ok(validate(q));
			double[] r = add(s, q);
			same(r, n(0));
		}

		double[] r = n(0);
		double[] p = n(0);
		for (int i = 999; i >= 0; --i) {
			double h = seq.get(i);
			r = add(r, n(-h));
			//			ok(validate(r));
			p = add(n(h), p);
			//			ok(validate(p));
			same(add(r, p), n(0));
		}
		same(add(r, s), n(0));
		same(add(q, p), n(0));
	}

	@Test
	public void testAddTwo() {
		double DBL_EPSILON = Math.pow(2, -53);

		same(add(1e64, 1), n(1, 1e64));
		same(add(1, 1), n(0, 2));
		same(add(0, -1415), n(0, -1415));
		same(add(1e-64, 1e64), n(1e-64, 1e64));
		same(add(0, 0), n(0, 0));
		same(add(9e15 + 1, 9e15), n(1, 18000000000000000.0));
		same(add(DBL_EPSILON, 1.0), n(DBL_EPSILON, 1.0));

		for (int i = 0; i < 100; ++i) {
			double a = Math.random() - 0.5;
			double b = Math.random() - 0.5;
			double[] s = add(a, b);
			same(add(b, a), s);
		}
	}

	@Test
	public void testMultiplyTwo() {
		ArrayList<Double> testValues = new ArrayList<>(Arrays.asList(
				0.0,
				1.0,
				Math.pow(2, -52),
				Math.pow(2, -53),
				1.0 - Math.pow(2, -53),
				1.0 + Math.pow(2, -52),
				Math.pow(2, -500),
				Math.pow(2, 500),
				2.0,
				0.5,
				3.0,
				1.5,
				0.1,
				0.3,
				0.7));
		for (int i = 0; i < 20; ++i) {
			testValues.add(Math.random());
			testValues.add(Math.random() * Math.pow(2, 1000 * Math.random() - 500));
		}
		for (int i = testValues.size() - 1; i > 0; --i) {
			testValues.add(-testValues.get(i));
		}

		for (int j = 0; j < testValues.size(); ++j) {
			double a = testValues.get(j);
			same(multiply(0, a), n(0, 0));
			same(multiply(1, a), n(0, a));
			same(multiply(-1, a), n(0, -a));

			for (int k = 0; k < testValues.size(); ++k) {
				double b = testValues.get(k);

				double[] s = multiply(a, b);

				//			      ok(!testOverlap(s[0], s[1]), "overlapping")
				Assert.assertTrue(Math.abs(s[0]) <= Math.abs(s[1]));
				Assert.assertEquals(s[1], a * b, 0.0);

				double[] r = multiply(b, a);
				same(s, r);

				if (Math.getExponent(s[0]) > -300 && Math.getExponent(s[1]) > -300) {
					BigDecimal ba = new BigDecimal(a).multiply(new BigDecimal(b));
					BigDecimal bb = new BigDecimal(s[0]).add(new BigDecimal(s[1]));
					Assert.assertEquals(0, ba.compareTo(bb));
				}
			}
		}

		same(multiply(
				1.0 + Math.pow(2, -52),
				1.0 + Math.pow(2, -52)),
				n(Math.pow(2, -104), 1.0 + Math.pow(2, -51)));
	}

	@Test
	public void testMultiply() {
		same(multiply(n(4), 2), n(8));
		same(multiply(n(1, 1e64), 2), n(2, 2e64));
		same(multiply(n(1), 1), n(1));

		for (int i = -50; i < 50; ++i) {
			for (int j = -50; j < 50; ++j) {
				same(multiply(n(i), j), n(i * j));
			}
		}
	}
}
