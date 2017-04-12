package org.andork.unit;

import org.junit.Assert;
import org.junit.Test;

public class AngleTests {
	@Test
	public void testNormalize() {
		Assert.assertEquals(
				new UnitizedDouble<>(300.0, Angle.degrees),
				Angle.normalize(new UnitizedDouble<>(300.0, Angle.degrees)));
		Assert.assertEquals(
				new UnitizedDouble<>(300.0, Angle.degrees),
				Angle.normalize(new UnitizedDouble<>(-60.0, Angle.degrees)));
		Assert.assertEquals(
				new UnitizedDouble<>(Math.PI * 3 / 2, Angle.radians),
				Angle.normalize(new UnitizedDouble<>(-Math.PI / 2, Angle.radians)));
	}

	public void testOpposite() {
		Assert.assertEquals(
				new UnitizedDouble<>(120.0, Angle.degrees),
				Angle.opposite(new UnitizedDouble<>(300.0, Angle.degrees)));
		Assert.assertEquals(
				new UnitizedDouble<>(300.0, Angle.degrees),
				Angle.opposite(new UnitizedDouble<>(120.0, Angle.degrees)));
		Assert.assertEquals(
				new UnitizedDouble<>(Math.PI * 3 / 2, Angle.radians),
				Angle.opposite(new UnitizedDouble<>(Math.PI / 2, Angle.radians)));
		Assert.assertEquals(
				new UnitizedDouble<>(Math.PI / 2, Angle.radians),
				Angle.opposite(new UnitizedDouble<>(Math.PI * 3 / 2, Angle.radians)));
	}
}
