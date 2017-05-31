package org.breakout.model.parsed;

import org.andork.unit.Angle;
import org.andork.unit.UnitizedDouble;
import org.breakout.model.parsed.ParseMessage.Severity;
import org.junit.Assert;
import org.junit.Test;

public class MetacaveAngleParserTests {
	@Test
	public void testNumberWithDefaultUnit() {
		ParsedField<UnitizedDouble<Angle>> parsed = MetacaveAngleParser.parse(" 35.4   ", Angle.degrees);

		Assert.assertNull(parsed.message);
		Assert.assertEquals(new UnitizedDouble<>(35.4, Angle.degrees), parsed.value);
	}

	@Test
	public void testWhitespace() {
		ParsedField<UnitizedDouble<Angle>> parsed = MetacaveAngleParser.parse("    ", Angle.degrees);
		Assert.assertNull(parsed);
	}

	@Test
	public void testInvalidUnit() {
		ParsedField<UnitizedDouble<Angle>> parsed = MetacaveAngleParser.parse("  24 k  ", Angle.degrees);

		Assert.assertNull(parsed.value);
		Assert.assertEquals(Severity.ERROR, parsed.message.severity);
		Assert.assertEquals("invalid unit: k", parsed.message.text);
	}

	@Test
	public void testInvalidUnitAfterValidUnit() {
		ParsedField<UnitizedDouble<Angle>> parsed = MetacaveAngleParser.parse("  32 deg 24 k  ", Angle.degrees);

		Assert.assertNull(parsed.value);
		Assert.assertEquals(Severity.ERROR, parsed.message.severity);
		Assert.assertEquals("invalid unit: k", parsed.message.text);
	}

	@Test
	public void testNumberWithUnit() {
		ParsedField<UnitizedDouble<Angle>> parsed = MetacaveAngleParser.parse("   35.4 rad ", Angle.degrees);

		Assert.assertNull(parsed.message);
		Assert.assertEquals(new UnitizedDouble<>(35.4, Angle.radians), parsed.value);
	}

	@Test
	public void testMultipleNumbersAndUnits() {
		ParsedField<UnitizedDouble<Angle>> parsed = MetacaveAngleParser.parse("   35 deg 23 grad 2 rad   ",
				Angle.radians);

		Assert.assertNull(parsed.message);
		Assert.assertEquals(new UnitizedDouble<>(35, Angle.degrees)
				.add(new UnitizedDouble<>(23, Angle.gradians))
				.add(new UnitizedDouble<>(2, Angle.radians)), parsed.value);
	}
}
