package org.breakout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class StationSetTests {
	@Test
	public void testToString() {
		assertEquals("AB2-5 AB7-9 AB9C-E B3 B'3 JK", new StationSet("JK AB2-5 B3,AB7-9 AB9C-E B'3,").toString());
	}
	
	@Test
	public void testContains() {
		StationSet set = new StationSet("JK AB2-5 B3 B'3 AB7-9 AB9C-E");
		for (String station : Arrays.asList(
				"JK",
				"JK1",
				"JK2",
				"JK2000",
				"AB2", 
				"AB3", 
				"AB4", 
				"AB5", 
				"B3",
				"B'3",
				"AB7",
				"AB8",
				"AB9",
				"AB9C",
				"AB9D",
				"AB9C"
			)) {
			assertTrue("contains " + station, set.contains(station));
		}
		assertTrue(set.contains("AB5"));
		
		for (String station : Arrays.asList(
				"AB1", 
				"AB6", 
				"AB10", 
				"AB11", 
				"B2",
				"B4",
				"AB", 
				"B",
				"B'",
				"AB9A",
				"AB9B",
				"AB9F",
				"AB9G"
			)) {
			assertFalse("doesn't contain " + station, set.contains(station));
		}
	}
}
