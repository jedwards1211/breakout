package org.breakout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.breakout.model.ShotKey;
import org.junit.Test;

public class StationSetTests {
	@Test
	public void testToString() {
		assertEquals("2-12 AB2-5 AB7-9 AB9C-E B3 B'3 JK", new StationSet("JK AB2-5 B3,AB7-9 2-12 AB9C-E B'3,").toString());
	}
	
	@Test
	public void testContains() {
		StationSet set = new StationSet("JK AB2-5 B3 B'3 AB7-9 2-12 AB9C-E");
		for (String station : Arrays.asList(
				"2",
				"3",
				"11",
				"12",
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
				"1",
				"0",
				"13",
				"14",
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
	
	@Test
	public void testForEachStation() {
		StationSet set = new StationSet("JK AB2-5 B3 B'3 AB7-9 2-12 AB9C-E");
		Set<String> stations = new HashSet<>();
		set.forEachStation(stations::add);
		
		assertEquals(new HashSet<>(Arrays.asList(
			"2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "JK", "AB2", "AB3", "AB4", "AB5", "AB7", "AB8", "AB9", "B3", "B'3", "AB9C", "AB9D", "AB9E"
		)), stations);
	}
	
	
	@Test
	public void testForEachShot() {
		StationSet set = new StationSet("JK AB2-5 B3 B'3 AB7-9 2-6 AB9C-E");
		Set<ShotKey> shots = new HashSet<>();
		String cave = "foo";
		set.forEachShot(cave, shots::add);
		Collection<ShotKey> expected = Arrays.asList(
			new ShotKey(cave, "2", cave, "3"),
			new ShotKey(cave, "3", cave, "4"),
			new ShotKey(cave, "4", cave, "5"),
			new ShotKey(cave, "5", cave, "6"),
			new ShotKey(cave, "AB2", cave, "AB3"),
			new ShotKey(cave, "AB3", cave, "AB4"),
			new ShotKey(cave, "AB4", cave, "AB5"),
			new ShotKey(cave, "AB7", cave, "AB8"),
			new ShotKey(cave, "AB8", cave, "AB9"),
			new ShotKey(cave, "AB9C", cave, "AB9D"),
			new ShotKey(cave, "AB9D", cave, "AB9E")
		);
		assertEquals(expected.size(), shots.size());
		for (ShotKey shot : expected) {
			assertTrue("has " + shot, shots.contains(shot));
		}
	}
}
