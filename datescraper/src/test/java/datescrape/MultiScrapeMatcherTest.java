package datescrape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.andork.datescraper.DateMatcher;
import org.andork.datescraper.DatePatterns;
import org.junit.Test;

public class MultiScrapeMatcherTest {
	@Test
	public void test001() {
		String input =
			"04-02-18   04-02-29th,Aug 03, 2012 2 Sep 1964 2  blah 1394/02/3   2019.jul.31 0 2k389 182  23 1 29  38482 29 3 28 3-4-2019 jan12,2013\t2019jul.20 2016aug03 5 mar. 14 2018 20190822T03:45";
		DateMatcher m = DatePatterns.en_US.matcher(input);

		assertTrue(m.find());
		assertEquals("04-02-18", m.matchText());
		assertEquals(new Date("Apr 2 2018"), m.match());

		assertTrue(m.find());
		assertEquals("04-02-29th", m.matchText());
		assertEquals(new Date("Feb 29 2004"), m.match());

		assertTrue(m.find());
		assertEquals("Aug 03, 2012", m.matchText());
		assertEquals(new Date("Aug 3, 2012"), m.match());

		assertTrue(m.find());
		assertEquals("2 Sep 1964", m.matchText());
		assertEquals(new Date("Sep 2 1964"), m.match());

		assertTrue(m.find());
		assertEquals("1394/02/3", m.matchText());
		assertEquals(new Date("Feb 3 1394"), m.match());

		assertTrue(m.find());
		assertEquals("2019.jul.31", m.matchText());
		assertEquals(new Date("jul 31 2019"), m.match());

		assertTrue(m.find());
		assertEquals("3-4-2019", m.matchText());
		assertEquals(new Date("Mar 4 2019"), m.match());

		assertTrue(m.find());
		assertEquals("jan12,2013", m.matchText());
		assertEquals(new Date("Jan 12 2013"), m.match());

		assertTrue(m.find());
		assertEquals("2019jul.20", m.matchText());
		assertEquals(new Date("jul 20 2019"), m.match());

		assertTrue(m.find());
		assertEquals("2016aug03", m.matchText());
		assertEquals(new Date("aug 3 2016"), m.match());

		assertTrue(m.find());
		assertEquals("mar. 14 2018", m.matchText());
		assertEquals(new Date("mar 14 2018"), m.match());

		assertTrue(m.find());
		assertEquals("20190822T03:45", m.matchText());
		assertEquals(new Date("aug 22 2019 03:45 am"), m.match());
	}
}
