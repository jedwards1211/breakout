package org.breakout;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

public class LechuguillaStationSetsTests {
	@SafeVarargs
	private static <E> void assertSetEquals(Set<E> actual, E... expectedArray) {
		Set<E> expected = new LinkedHashSet<>(Arrays.asList(expectedArray));
		Set<E> missing = new LinkedHashSet<>(expected);
		missing.removeAll(actual);
		Set<E> extraneous = new LinkedHashSet<>(actual);
		extraneous.removeAll(expected);
		if (!missing.isEmpty() || !extraneous.isEmpty()) {
			StringBuilder message = new StringBuilder("set didn't match expectation;");
			if (!missing.isEmpty()) {
				message.append("\n\nMissing elements:");
				missing.forEach(elem -> message.append("\n  ").append(elem));
			}
			if (!extraneous.isEmpty()) {
				message.append("\n\nExtraneous elements:");
				extraneous.forEach(elem -> message.append("\n  ").append(elem));
			}
			throw new AssertionError(message.toString());
		}
	}

	private static void assertStations(String stationSetStr, String... expected) {
		assertSetEquals(LechuguillaStationSets.parse(stationSetStr), expected);
	}
	
	@Test
	public void testBase26() {
		assertEquals(1, LechuguillaStationSets.parseBase26("A", 0));
		assertEquals(27, LechuguillaStationSets.parseBase26("AA", 0));
		assertEquals(28, LechuguillaStationSets.parseBase26("AB", 0));
		assertEquals(54, LechuguillaStationSets.parseBase26("BB", 0));
		
		assertEquals("A", LechuguillaStationSets.stringifyBase26(1));
		assertEquals("AA", LechuguillaStationSets.stringifyBase26(27));
		assertEquals("AB", LechuguillaStationSets.stringifyBase26(28));
		assertEquals("BB", LechuguillaStationSets.stringifyBase26(54));
	}

	@Test
	public void lechTest001() {
		assertStations("M7Q1,1A-B,2!-3!,3A-B,5!-6!,7,8!-14!,15-16,11A M7R7-8",
			"M7Q1",
			"M7Q1A",
			"M7Q1B",
			"M7Q2!",
			"M7Q3!",
			"M7Q3A",
			"M7Q3B",
			"M7Q5!",
			"M7Q6!",
			"M7Q7",
			"M7Q8!",
			"M7Q9!",
			"M7Q10!",
			"M7Q11!",
			"M7Q12!",
			"M7Q13!",
			"M7Q14!",
			"M7Q15",
			"M7Q16",
			"M7Q11A",
			"M7R7",
			"M7R8"
		);
	}

	@Test
	public void lechTest002() {
		assertStations("EYG1 EYA 2A-2H EY63",
			"EYG1",
			"EYA",
			"EYA2A",
			"EYA2B",
			"EYA2C",
			"EYA2D",
			"EYA2E",
			"EYA2F",
			"EYA2G",
			"EYA2H",
			"EY63"
		);
	}

	@Test
	public void lechTest003() {
		
		assertStations("BIG1-5 FKK27 FLD21A-F,23",
			"BIG1",
			"BIG2",
			"BIG3",
			"BIG4",
			"BIG5",
			"FKK27",
			"FLD21A",
			"FLD21B",
			"FLD21C",
			"FLD21D",
			"FLD21E",
			"FLD21F",
			"FLD23"
		);
		
	}

	@Test
	public void lechTest004() {
		// R-EY is a typo in filename, should be REY
		// (many notes have dashes in station names but they are not included in Compass data)
		assertStations("EY92A-D,94-95,101 R-EY1A-6A",
			"EY92A",
			"EY92B",
			"EY92C",
			"EY92D",
			"EY94",
			"EY95",
			"EY101",
			"REY1A",
			"REY2A",
			"REY3A",
			"REY4A",
			"REY5A",
			"REY6A"
		);
		
	}

	@Test
	public void lechTest005() {
		// ECA2,A-J means ECA2 ECA2A-J
		assertStations("EY20G-L ECA2,A-J EC32 EXE1-6",
			"EY20G",
			"EY20H",
			"EY20I",
			"EY20J",
			"EY20K",
			"EY20L",
			"ECA2",
			"ECA2A",
			"ECA2B",
			"ECA2C",
			"ECA2D",
			"ECA2E",
			"ECA2F",
			"ECA2G",
			"ECA2H",
			"ECA2I",
			"ECA2J",
			"EC32",
			"EXE1",
			"EXE2",
			"EXE3",
			"EXE4",
			"EXE5",
			"EXE6"
		);
		
	}

	@Test
	public void lechTest006() {
		// original filename: MPD!1-7 MPD8-55 MN24
		// Make sure MPD!1-7 is interpreted correctly
		assertStations("MPD!1-4 MPD8-12 MN24",
			"MPD!1",
			"MPD!2",
			"MPD!3",
			"MPD!4",
			"MPD8",
			"MPD9",
			"MPD10",
			"MPD11",
			"MPD12",
			"MN24"
		);
	}

	@Test
	public void lechTest007() {
		// original filename: EY52A-L EY52A!-C! EYB1  EY56A-3
		// Make sure EY52A!-C! is interpreted correctly
		// EY56A-3 means EY56A EY56A1-3
		assertStations("EY52A-D EY52A!-C! EYB1  EY56A-3",
			"EY52A",
			"EY52B",
			"EY52C",
			"EY52D",
			"EY52A!",
			"EY52B!",
			"EY52C!",
			"EYB1",
			"EY56A",
			"EY56A1",
			"EY56A2",
			"EY56A3"
		);
	}

	@Test
	public void lechTest008() {
		// This means EYA15 EYA15A-F
		assertStations("EYA15 A-F",
			"EYA15",
			"EYA15A",
			"EYA15B",
			"EYA15C",
			"EYA15D",
			"EYA15E",
			"EYA15F"
		);
	}

	@Test
	public void lechTest009() {
		// This means EY49-52 EY52A-C
		assertStations("EY49-52A-C",
			"EY49",
			"EY50",
			"EY51",
			"EY52",
			"EY52A",
			"EY52B",
			"EY52C"
		);
	}

	@Test
	public void lechTest010() {
		// original filename: EY55A-R,D1-5,L1-36
		// Make sure this gets interpreted correctly
		assertStations("EY55A-C,D1-5,L1-3",
			"EY55A",
			"EY55B",
			"EY55C",
			"EY55D1",
			"EY55D2",
			"EY55D3",
			"EY55D4",
			"EY55D5",
			"EY55L1",
			"EY55L2",
			"EY55L3"
		);
	}

	@Test
	public void lechTest011() {
		assertStations("EC37-44,51,37A,38A-C,AA-AI,43A-C,AA-AB,44A-F",
			"EC37",
			"EC38",
			"EC39",
			"EC40",
			"EC41",
			"EC42",
			"EC43",
			"EC44",
			"EC51",
			"EC37A",
			"EC38A",
			"EC38B",
			"EC38C",
			"EC38AA",
			"EC38AB",
			"EC38AC",
			"EC38AD",
			"EC38AE",
			"EC38AF",
			"EC38AG",
			"EC38AH",
			"EC38AI",
			"EC43A",
			"EC43B",
			"EC43C",
			"EC43AA",
			"EC43AB",
			"EC44A",
			"EC44B",
			"EC44C",
			"EC44D",
			"EC44E",
			"EC44F"
		);
	}

	@Test
	public void lechTest012() {
		// A-LD is a typo, should be A-L
		assertStations("FD33WA-WH,45A-LD",
			"FD33WA",
			"FD33WB",
			"FD33WC",
			"FD33WD",
			"FD33WE",
			"FD33WF",
			"FD33WG",
			"FD33WH",
			"FD45A",
			"FD45B",
			"FD45C",
			"FD45D",
			"FD45E",
			"FD45F",
			"FD45G",
			"FD45H",
			"FD45I",
			"FD45J",
			"FD45K",
			"FD45L",
			"FD45LD"
		);
		
	}

	@Test
	public void lechTest013() {
		// Includes EYKC5A and EYKC5D but not EYKC5B-C
		assertStations("EYKC1-7,5A,D",
			"EYKC1",
			"EYKC2",
			"EYKC3",
			"EYKC4",
			"EYKC5",
			"EYKC6",
			"EYKC7",
			"EYKC5A",
			"EYKC5D"
		);
	}

	@Test
	public void lechTest014() {
		// original filename: H1-15 GDV66 H4! H5! H7! H8!
		assertStations("H1-4 GDV66 H4! H5! H7! H8!",
			"H1",
			"H2",
			"H3",
			"H4",
			"GDV66",
			"H4!",
			"H5!",
			"H7!",
			"H8!"
		);
	}

	@Test
	public void lechTest015() {
		// bogus commas, but need to tolerate this
		assertStations("D6!, D7!, D7A-F D5",
			"D6!",
			"D7!",
			"D7A",
			"D7B",
			"D7C",
			"D7D",
			"D7E",
			"D7F",
			"D5"
		);
	}

	@Test
	public void lechTest016() {
		// original filename: FKJ_R17-18,17A,21,25-32,26A-C FKJ7B,X FJF14,16,22 FJF_R1-6,5A FFN87,90-94,91A FFP2
		// underscores are part of the station names
		assertStations("FKJ_R17-18,17A,21,25-28,26A-C FKJ7B,X FJF14,16,22 FJF_R1-6,5A FFN87,90-94,91A FFP2",
			"FKJ_R17",
			"FKJ_R18",
			"FKJ_R17A",
			"FKJ_R21",
			"FKJ_R25",
			"FKJ_R26",
			"FKJ_R27",
			"FKJ_R28",
			"FKJ_R26A",
			"FKJ_R26B",
			"FKJ_R26C",
			"FKJ7B",
			"FKJ7X",
			"FJF14",
			"FJF16",
			"FJF22",
			"FJF_R1",
			"FJF_R2",
			"FJF_R3",
			"FJF_R4",
			"FJF_R5",
			"FJF_R6",
			"FJF_R5A",
			"FFN87",
			"FFN90",
			"FFN91",
			"FFN92",
			"FFN93",
			"FFN94",
			"FFN91A",
			"FFP2"
		);
		
	}

	@Test
	public void lechTest017() {
		// someone cut corners on this file name, the actual stations are
		// 17A!, 17D, 17B!, 17C
		// not sure I can handle this intelligently
		assertStations("MQPA15-20,17A!-D MQPA51-60",
			"MQPA15",
			"MQPA16",
			"MQPA17",
			"MQPA18",
			"MQPA19",
			"MQPA20",
			"MQPA17A!",
			"MQPA17D",
			"MQPA51",
			"MQPA52",
			"MQPA53",
			"MQPA54",
			"MQPA55",
			"MQPA56",
			"MQPA57",
			"MQPA58",
			"MQPA59",
			"MQPA60"
		);
	}

}
