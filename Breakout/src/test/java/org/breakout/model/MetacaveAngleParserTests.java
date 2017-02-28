package org.breakout.model;

import org.andork.unit.Angle;
import org.andork.unit.UnitizedDouble;
import org.breakout.model.MetacaveMeasurementParser.Severity;
import org.junit.Assert;
import org.junit.Test;

public class MetacaveAngleParserTests {
	@Test
	public void testNumberWithDefaultUnit() {
		MetacaveAngleParser parser = new MetacaveAngleParser();
		parser.parse(" 35.4   ", Angle.degrees);

		Assert.assertNull(parser.message);
		Assert.assertEquals(new UnitizedDouble<>(35.4, Angle.degrees), parser.angle);
	}

	@Test
	public void testWhitespace() {
		MetacaveAngleParser parser = new MetacaveAngleParser();
		parser.parse("    ", Angle.degrees);

		Assert.assertNull(parser.angle);
		Assert.assertEquals(Severity.ERROR, parser.severity);
		Assert.assertEquals("invalid number: ", parser.message);
	}

	@Test
	public void testInvalidUnit() {
		MetacaveAngleParser parser = new MetacaveAngleParser();
		parser.parse("  24 k  ", Angle.degrees);

		Assert.assertNull(parser.angle);
		Assert.assertEquals(Severity.ERROR, parser.severity);
		Assert.assertEquals("invalid unit: k", parser.message);
	}

	@Test
	public void testInvalidUnitAfterValidUnit() {
		MetacaveAngleParser parser = new MetacaveAngleParser();
		parser.parse("  32 deg 24 k  ", Angle.degrees);

		Assert.assertNull(parser.angle);
		Assert.assertEquals(Severity.ERROR, parser.severity);
		Assert.assertEquals("invalid unit: k", parser.message);
	}

	@Test
	public void testNumberWithUnit() {
		MetacaveAngleParser parser = new MetacaveAngleParser();
		parser.parse("   35.4 rad ", Angle.degrees);

		Assert.assertNull(parser.message);
		Assert.assertEquals(new UnitizedDouble<>(35.4, Angle.radians), parser.angle);
	}

	@Test
	public void testMultipleNumbersAndUnits() {
		MetacaveAngleParser parser = new MetacaveAngleParser();
		parser.parse("   35 deg 23 grad 2 rad   ", Angle.radians);

		Assert.assertNull(parser.message);
		Assert.assertEquals(new UnitizedDouble<>(35, Angle.degrees)
				.add(new UnitizedDouble<>(23, Angle.gradians))
				.add(new UnitizedDouble<>(2, Angle.radians)), parser.angle);
	}
}
