package org.breakout.model.parsed;

import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;
import org.breakout.model.parsed.ParseMessage.Severity;
import org.junit.Assert;
import org.junit.Test;

public class MetacaveLengthParserTests {
	@Test
	public void testNumberWithDefaultUnit() {
		ParsedField<UnitizedDouble<Length>> parsed = MetacaveLengthParser.parse(" 35.4   ", Length.meters);

		Assert.assertNull(parsed.message);
		Assert.assertEquals(new UnitizedDouble<>(35.4, Length.meters), parsed.value);
	}

	@Test
	public void testWhitespace() {
		ParsedField<UnitizedDouble<Length>> parsed = MetacaveLengthParser.parse("    ", Length.meters);

		Assert.assertNull(parsed);
	}

	@Test
	public void testInvalidUnit() {
		ParsedField<UnitizedDouble<Length>> parsed = MetacaveLengthParser.parse("  24 k  ", Length.meters);

		Assert.assertNull(parsed.value);
		Assert.assertEquals(Severity.ERROR, parsed.message.severity);
		Assert.assertEquals("invalid unit: k", parsed.message.text);
	}

	@Test
	public void testInvalidUnitAfterValidUnit() {
		ParsedField<UnitizedDouble<Length>> parsed = MetacaveLengthParser.parse("  32 m 24 k  ", Length.meters);

		Assert.assertNull(parsed.value);
		Assert.assertEquals(Severity.ERROR, parsed.message.severity);
		Assert.assertEquals("invalid unit: k", parsed.message.text);
	}

	@Test
	public void testNumberWithUnit() {
		ParsedField<UnitizedDouble<Length>> parsed = MetacaveLengthParser.parse("   35.4 Ft ", Length.meters);

		Assert.assertNull(parsed.message);
		Assert.assertEquals(new UnitizedDouble<>(35.4, Length.feet), parsed.value);
	}

	@Test
	public void testMultipleNumbersAndUnits() {
		ParsedField<UnitizedDouble<Length>> parsed = MetacaveLengthParser.parse("   35 m 23 cm 2 cm   ", Length.feet);

		Assert.assertNull(parsed.message);
		Assert.assertEquals(new UnitizedDouble<>(35.25, Length.meters), parsed.value);
	}
}
