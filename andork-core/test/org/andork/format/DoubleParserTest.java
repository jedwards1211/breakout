package org.andork.format;

import java.text.ParseException;
import java.text.StringCharacterIterator;

import org.junit.Assert;
import org.junit.Test;

public class DoubleParserTest {
	@Test
	public void test001() throws ParseException {
		Assert.assertEquals(0.17, new DoubleParser().applyAsDouble(new StringCharacterIterator("0.17")), 0.0);
		Assert.assertEquals(0.17, new DoubleParser().applyAsDouble(new StringCharacterIterator(".17")), 0.0);
		Assert.assertEquals(0.17, new DoubleParser().applyAsDouble(new StringCharacterIterator("   .17")), 0.0);
		Assert.assertEquals(1.17, new DoubleParser().applyAsDouble(new StringCharacterIterator("   1.17")), 0.0);
		Assert.assertEquals(100.0, new DoubleParser().applyAsDouble(new StringCharacterIterator("   100. 17")), 0.0);
		Assert.assertEquals(100.17, new DoubleParser().applyAsDouble(new StringCharacterIterator("   100.17   ")), 0.0);
		Assert.assertEquals(1000.17, new DoubleParser().applyAsDouble(new StringCharacterIterator("   1,000.17   ")),
				0.0);
		Assert.assertEquals(12345678.17,
				new DoubleParser().applyAsDouble(new StringCharacterIterator("   12,345,678.17   ")), 0.0);

		try {
			new DoubleParser().applyAsDouble(new StringCharacterIterator("   12,345,6678.17   "));
			Assert.fail();
		} catch (Exception ex) {

		}

		try {
			new DoubleParser().applyAsDouble(new StringCharacterIterator("1234,678.17   "));
			Assert.fail();
		} catch (Exception ex) {

		}

		try {
			new DoubleParser().applyAsDouble(new StringCharacterIterator("1234678,.17   "));
			Assert.fail();
		} catch (Exception ex) {

		}

		try {
			new DoubleParser().applyAsDouble(new StringCharacterIterator("1,00.17   "));
			Assert.fail();
		} catch (Exception ex) {

		}
	}
}
