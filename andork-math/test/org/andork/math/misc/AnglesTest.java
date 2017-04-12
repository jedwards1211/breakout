package org.andork.math.misc;

import org.junit.Assert;
import org.junit.Test;

public class AnglesTest {
	@Test
	public void testDifference() {
		Assert.assertEquals(
				Math.toRadians(90),
				Angles.difference(Math.toRadians(30), Math.toRadians(300)),
				1e-12);
		Assert.assertEquals(
				Math.toRadians(90),
				Angles.difference(Math.toRadians(300), Math.toRadians(30)),
				1e-12);
		Assert.assertEquals(
				Math.toRadians(90),
				Angles.difference(Math.toRadians(300 - 360), Math.toRadians(30 - 360)),
				1e-12);
		Assert.assertEquals(
				Math.toRadians(90),
				Angles.difference(Math.toRadians(300 - 720), Math.toRadians(30 + 720)),
				1e-12);
		Assert.assertEquals(
				Math.toRadians(120),
				Angles.difference(Math.toRadians(150), Math.toRadians(30)),
				1e-12);
		Assert.assertEquals(
				Math.toRadians(120),
				Angles.difference(Math.toRadians(30), Math.toRadians(150)),
				1e-12);
		Assert.assertEquals(
				Math.toRadians(120),
				Angles.difference(Math.toRadians(150 - 720), Math.toRadians(30 + 720)),
				1e-12);
	}
}
