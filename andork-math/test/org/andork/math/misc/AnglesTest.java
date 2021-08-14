package org.andork.math.misc;

import org.junit.Assert;
import org.junit.Test;

public class AnglesTest {
	void rotationToTestCase(double expected, double from, double to) {
		Assert
			.assertEquals(expected, Math.toDegrees(Angles.rotationTo(Math.toRadians(from), Math.toRadians(to))), 1e-12);
	}

	@Test
	public void testRotationTo() {
		rotationToTestCase(180, 0, 180);
		rotationToTestCase(180, 180, 0);

		rotationToTestCase(5, 5, 10);
		rotationToTestCase(5, 5 + 360, 10);

		rotationToTestCase(10, 355, 5);
		rotationToTestCase(10, 355, 5 - 360);
		rotationToTestCase(10, 355 - 360, 5);
		rotationToTestCase(10, 355 - 360, 5 - 360);
		rotationToTestCase(10, 355, 5 + 360);
		rotationToTestCase(10, 355 + 360, 5);
		rotationToTestCase(10, 355 + 360, 5 + 360);

		rotationToTestCase(-10, 5, 355);
		rotationToTestCase(-10, 5 - 360, 355);
		rotationToTestCase(-10, 5, 355 - 360);
		rotationToTestCase(-10, 5 - 360, 355 - 360);
		rotationToTestCase(-10, 5 + 360, 355);
		rotationToTestCase(-10, 5, 355 + 360);
		rotationToTestCase(-10, 5 + 360, 355 + 360);
	}

	@Test
	public void testAbsDifference() {
		Assert.assertEquals(Math.toRadians(90), Angles.absDifference(Math.toRadians(30), Math.toRadians(300)), 1e-12);
		Assert.assertEquals(Math.toRadians(90), Angles.absDifference(Math.toRadians(300), Math.toRadians(30)), 1e-12);
		Assert
			.assertEquals(
				Math.toRadians(90),
				Angles.absDifference(Math.toRadians(300 - 360), Math.toRadians(30 - 360)),
				1e-12);
		Assert
			.assertEquals(
				Math.toRadians(90),
				Angles.absDifference(Math.toRadians(300 - 720), Math.toRadians(30 + 720)),
				1e-12);
		Assert.assertEquals(Math.toRadians(120), Angles.absDifference(Math.toRadians(150), Math.toRadians(30)), 1e-12);
		Assert.assertEquals(Math.toRadians(120), Angles.absDifference(Math.toRadians(30), Math.toRadians(150)), 1e-12);
		Assert
			.assertEquals(
				Math.toRadians(120),
				Angles.absDifference(Math.toRadians(150 - 720), Math.toRadians(30 + 720)),
				1e-12);
		Assert.assertEquals(Math.toRadians(10), Angles.absDifference(Math.toRadians(355), Math.toRadians(5)), 1e-12);
		Assert.assertEquals(Math.toRadians(10), Angles.absDifference(Math.toRadians(5), Math.toRadians(355)), 1e-12);
	}

	void averageTestCase(double expected, double... angles) {
		double[] radians = new double[angles.length];
		for (int i = 0; i < angles.length; i++)
			radians[i] = Math.toRadians(angles[i]);
		Assert.assertEquals(expected, Math.toDegrees(Angles.average(radians)), 1e-12);
	}

	@Test
	public void testAverage() {
		averageTestCase(90, 0, 180);
		averageTestCase(45, 0, 90);
		averageTestCase(45, 90, 0);
		averageTestCase(0, 300, 60);
		averageTestCase(0, 60, 300);
		averageTestCase(270, 180, 360);
		averageTestCase(180, 120, 240);
		averageTestCase(180, 240, 120);
	}
}
