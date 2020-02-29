package org.andork.tilebelt;

import java.util.Arrays;
import java.util.List;

import org.breakout.SummarizeTripStations;
import org.junit.Assert;
import org.junit.Test;

public class SummarizeTripStationsTest {
	@Test
	public void test001() {
		assertSummaryEquals("CY5-9,11,13-15 CY5!-8!", "CY5 CY6 CY7 CY8 CY9 CY11 CY13 CY14 CY15 CY5! CY6! CY7! CY8!");
	}

	private static void assertSummaryEquals(String expected, String stationsStr) {
		List<String> stations = Arrays.asList(stationsStr.split("\\s+"));
		String summary = SummarizeTripStations.summarizeTripStations(stations);
		Assert.assertEquals(expected, summary);
	}
}
