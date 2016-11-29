package org.breakout.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.junit.Test;

public class MetacaveImporterTests {
	@Test
	public void testMetacave1() throws IOException {
		MetacaveImporter importer = new MetacaveImporter();
		importer.importMetacave(getClass().getResourceAsStream("metacave1.json"));

		List<SurveyRow> rows = importer.getRows();
		for (SurveyRow row : rows) {
			System.out.println(row);
		}
		assertEquals(4, rows.size());

		SurveyTrip trip = rows.get(0).getTrip();
		assertEquals("Fisher Ridge", trip.getCave());
		assertEquals("SurveyTrip 1", trip.getName());
		assertEquals("2016-01-01", trip.getDate());
		assertEquals(Arrays.asList("Andy Edwards", "Sean Lewis"), trip.getSurveyors());
		assertEquals(Length.feet, trip.getDistanceUnit());
		assertEquals(Angle.degrees, trip.getAngleUnit());
		assertFalse(trip.areBackAzimuthsCorrected());
		assertTrue(trip.areBackInclinationsCorrected());
		assertEquals("1.0", trip.getDeclination());
		assertEquals(Angle.gradians, trip.getOverrideFrontAzimuthUnit());
		assertEquals(Angle.milsNATO, trip.getOverrideBackAzimuthUnit());
		assertEquals(Angle.degrees, trip.getOverrideFrontInclinationUnit());
		assertEquals(Angle.percentGrade, trip.getOverrideBackInclinationUnit());
		assertEquals("2.0", trip.getDistanceCorrection());
		assertEquals("3.0", trip.getFrontAzimuthCorrection());
		assertEquals("4.0", trip.getBackAzimuthCorrection());
		assertEquals("5.0", trip.getFrontInclinationCorrection());
		assertEquals("6.0", trip.getBackInclinationCorrection());

		SurveyRow r0 = rows.get(0);
		assertSame(trip, r0.getTrip());
		assertEquals(null, r0.getOverrideFromCave());
		assertEquals("A1", r0.getFromStation());
		assertEquals("Mammoth", r0.getOverrideToCave());
		assertEquals("A2", r0.getToStation());
		assertEquals("1.0", r0.getLeft());
		assertEquals("2.0", r0.getRight());
		assertEquals("3.0", r0.getUp());
		assertEquals("4.0", r0.getDown());
		assertEquals("1.0", r0.getDistance());
		assertEquals("180", r0.getFrontAzimuth());
		assertEquals("-5", r0.getFrontInclination());
		assertEquals("0", r0.getBackAzimuth());
		assertEquals("5", r0.getBackInclination());

		SurveyRow r1 = rows.get(1);
		assertSame(trip, r1.getTrip());
		assertEquals(null, r1.getOverrideFromCave());
		assertEquals(null, r1.getFromStation());
		assertEquals(null, r1.getOverrideToCave());
		assertEquals(null, r1.getToStation());
		assertEquals(null, r1.getLeft());
		assertEquals(null, r1.getRight());
		assertEquals(null, r1.getUp());
		assertEquals(null, r1.getDown());
		assertEquals(null, r1.getDistance());
		assertEquals(null, r1.getFrontAzimuth());
		assertEquals(null, r1.getFrontInclination());
		assertEquals(null, r1.getBackAzimuth());
		assertEquals(null, r1.getBackInclination());

		SurveyRow r3 = rows.get(3);
		assertSame(trip, r3.getTrip());
		assertEquals(null, r3.getOverrideFromCave());
		assertEquals("A1", r3.getFromStation());
		assertEquals("Mammoth", r3.getOverrideToCave());
		assertEquals("A2", r3.getToStation());
		assertEquals(null, r3.getLeft());
		assertEquals(null, r3.getRight());
		assertEquals(null, r3.getUp());
		assertEquals(null, r3.getDown());
		assertEquals("3.0", r3.getDistance());
		assertEquals("90", r3.getFrontAzimuth());
		assertEquals("-10", r3.getFrontInclination());
		assertEquals(null, r3.getBackAzimuth());
		assertEquals(null, r3.getBackInclination());

		SurveyRow r2 = rows.get(2);
		assertSame(trip, r2.getTrip());
		assertEquals("Mammoth", r2.getOverrideFromCave());
		assertEquals("A2", r2.getFromStation());
		assertEquals(null, r2.getOverrideToCave());
		assertEquals(null, r2.getToStation());
		assertEquals("1.0", r2.getLeft());
		assertEquals("2.0", r2.getRight());
		assertEquals("3.0", r2.getUp());
		assertEquals("4.0", r2.getDown());
		assertEquals("5.0", r2.getDistance());
		assertEquals("23.5", r2.getFrontAzimuth());
		assertEquals("48.2", r2.getFrontInclination());
		assertEquals(null, r2.getBackAzimuth());
		assertEquals(null, r2.getBackInclination());
	}
}
