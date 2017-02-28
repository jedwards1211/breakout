package org.breakout.model;

import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;
import org.junit.Assert;
import org.junit.Test;

public class MetacaveLengthParserTests {
	@Test
	public void testNumberWithDefaultUnit() {
		MetacaveLengthParser parser = new MetacaveLengthParser();
		parser.parse(" 35.4   ", Length.meters);

		Assert.assertNull(parser.message);
		Assert.assertEquals(new UnitizedDouble<>(35.4, Length.meters), parser.length);
	}

	@Test
	public void testWhitespace() {
		MetacaveLengthParser parser = new MetacaveLengthParser();
		parser.parse("    ", Length.meters);

		Assert.assertNull(parser.length);
		Assert.assertEquals(ParseMessage.error("invalid number: "), parser.message);
	}

	@Test
	public void testInvalidUnit() {
		MetacaveLengthParser parser = new MetacaveLengthParser();
		parser.parse("  24 k  ", Length.meters);

		Assert.assertNull(parser.length);
		Assert.assertEquals(ParseMessage.error("invalid unit: k"), parser.message);
	}

	@Test
	public void testInvalidUnitAfterValidUnit() {
		MetacaveLengthParser parser = new MetacaveLengthParser();
		parser.parse("  32 m 24 k  ", Length.meters);

		Assert.assertNull(parser.length);
		Assert.assertEquals(ParseMessage.error("invalid unit: k"), parser.message);
	}

	@Test
	public void testNumberWithUnit() {
		MetacaveLengthParser parser = new MetacaveLengthParser();
		parser.parse("   35.4 Ft ", Length.meters);

		Assert.assertNull(parser.message);
		Assert.assertEquals(new UnitizedDouble<>(35.4, Length.feet), parser.length);
	}

	@Test
	public void testMultipleNumbersAndUnits() {
		MetacaveLengthParser parser = new MetacaveLengthParser();
		parser.parse("   35 m 23 cm 2 cm   ", Length.feet);

		Assert.assertNull(parser.message);
		Assert.assertEquals(new UnitizedDouble<>(35.25, Length.meters), parser.length);
	}
}
