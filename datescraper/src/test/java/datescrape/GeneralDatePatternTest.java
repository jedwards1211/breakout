package datescrape;

import java.util.Date;

import org.andork.datescraper.DateField;
import org.andork.datescraper.DateMatcher;
import org.andork.datescraper.GeneralDatePattern;
import org.junit.Assert;
import org.junit.Test;

public class GeneralDatePatternTest {
	@Test
	public void testBug001() {
		DateMatcher m = new GeneralDatePattern().order(DateField.FULL_YEAR, DateField.MONTH, DateField.DAY).matcher("1989-12-17");
		Assert.assertTrue(m.find());
		Assert.assertEquals(new Date("Dec 17 1989"), m.match());

		m = new GeneralDatePattern().order(DateField.MONTH, DateField.DAY, DateField.FULL_YEAR).matcher("jan12,2013");
		Assert.assertTrue(m.find());
		Assert.assertEquals(new Date("Jan 12 2013"), m.match());
	}
}
