package org.andork.unit;

import org.junit.Assert;
import org.junit.Test;

public class UnitTests {
	@Test
	public void unitTests() {
		Assert.assertEquals(1.609344, Length.type.convert(1.0, Length.miles, Length.kilometers), 0.0);
		Assert.assertEquals(0.9144, Length.type.convert(1.0, Length.yards, Length.meters), 0.0);
		Assert.assertEquals(45.0, Angle.type.convert(100.0, Angle.percentGrade, Angle.degrees), 0.0);
		Assert.assertEquals(-45.0, Angle.type.convert(-100.0, Angle.percentGrade, Angle.degrees), 0.0);
		Assert.assertEquals(90.0, Angle.type.convert(Math.PI / 2.0, Angle.radians, Angle.degrees), 0.0);
		Assert.assertEquals(90.0, Angle.type.convert(100.0, Angle.gradians, Angle.degrees), 0.0);
		Assert.assertEquals(90.0, Angle.type.convert(1600.0, Angle.milsNATO, Angle.degrees), 0.0);
	}
}
