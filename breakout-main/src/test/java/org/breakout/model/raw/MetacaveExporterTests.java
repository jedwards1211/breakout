package org.breakout.model.raw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MetacaveExporterTests {
	public static void assertEquals(JsonArray array, int index, String expected) {
		Assert.assertEquals(expected, array.get(index).getAsString());
	}

	public static void assertEquals(JsonObject obj, String property, int expected) {
		Assert.assertEquals(expected, obj.get(property).getAsInt());
	}

	public static void assertEquals(JsonObject obj, String property, String expected) {
		Assert.assertEquals(expected, obj.get(property).getAsString());
	}

	public static void assertFalse(JsonObject obj, String property) {
		Assert.assertFalse(obj.get(property).getAsBoolean());
	}

	public static void assertTrue(JsonObject obj, String property) {
		Assert.assertTrue(obj.get(property).getAsBoolean());
	}

	@Test
	public void testMetacave1() {
		List<SurveyRow> rows = new ArrayList<SurveyRow>();

		SurveyTrip _trip =
			new MutableSurveyTrip()
				.setCave("Fisher Ridge")
				.setName("SurveyTrip 1")
				.setDate("2016-01-01")
				.setSurveyors(Arrays.asList("Andy Edwards", "Sean Lewis"))
				.setDistanceUnit(Length.feet)
				.setAngleUnit(Angle.degrees)
				.setBackAzimuthsCorrected(false)
				.setBackInclinationsCorrected(true)
				.setDeclination("1.0")
				.setOverrideFrontAzimuthUnit(Angle.gradians)
				.setOverrideBackAzimuthUnit(Angle.milsNATO)
				.setOverrideFrontInclinationUnit(Angle.degrees)
				.setOverrideBackInclinationUnit(Angle.percentGrade)
				.setDistanceCorrection("2.0")
				.setFrontAzimuthCorrection("3.0")
				.setBackAzimuthCorrection("4.0")
				.setFrontInclinationCorrection("5.0")
				.setBackInclinationCorrection("6.0")
				.toImmutable();

		SurveyRow _r0 =
			new MutableSurveyRow()
				.setTrip(_trip)
				.setOverrideFromCave(null)
				.setFromStation("A1")
				.setOverrideToCave("Mammoth")
				.setToStation("A2")
				.setLeft("1.0")
				.setRight("2.0")
				.setUp("3.0")
				.setDown("4.0")
				.setDistance("1.0")
				.setFrontAzimuth("180")
				.setFrontInclination("-5")
				.setBackAzimuth("0")
				.setBackInclination("5")
				.toImmutable();

		SurveyRow _r3 =
			new MutableSurveyRow()
				.setTrip(_trip)
				.setOverrideFromCave(null)
				.setFromStation("A1")
				.setOverrideToCave("Mammoth")
				.setToStation("A2")
				.setLeft(null)
				.setRight(null)
				.setUp(null)
				.setDown(null)
				.setDistance("3.0")
				.setFrontAzimuth("90")
				.setFrontInclination("-10")
				.setBackAzimuth(null)
				.setBackInclination(null)
				.toImmutable();

		SurveyRow _r2 =
			new MutableSurveyRow()
				.setTrip(_trip)
				.setOverrideFromCave("Mammoth")
				.setFromStation("A2")
				.setOverrideToCave(null)
				.setToStation(null)
				.setLeft("1.0")
				.setRight("2.0")
				.setUp("3.0")
				.setDown("4.0")
				.setDistance("5.0")
				.setFrontAzimuth("23.5")
				.setFrontInclination("48.2")
				.setBackAzimuth(null)
				.setBackInclination(null)
				.toImmutable();

		rows.add(_r0);
		rows.add(null);
		rows.add(_r2);
		rows.add(_r3);

		MetacaveExporter exporter = new MetacaveExporter();
		exporter.export(rows);

		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(exporter.getRoot()));

		JsonObject trip =
			exporter
				.getRoot()
				.getAsJsonObject("caves")
				.getAsJsonObject("Fisher Ridge")
				.getAsJsonArray("trips")
				.get(0)
				.getAsJsonObject();

		assertEquals(trip, "name", "SurveyTrip 1");
		assertEquals(trip, "date", "2016-01-01");
		JsonObject surveyors = trip.getAsJsonObject("surveyors");
		Assert.assertTrue(surveyors.has("Andy Edwards"));
		Assert.assertTrue(surveyors.has("Sean Lewis"));
		assertEquals(trip, "distUnit", "ft");
		assertEquals(trip, "angleUnit", "deg");
		assertFalse(trip, "azmBacksightsCorrected");
		assertTrue(trip, "incBacksightsCorrected");
		assertEquals(trip, "declination", "1.0");
		assertEquals(trip, "azmFsUnit", "grad");
		assertEquals(trip, "azmBsUnit", "mil");
		assertEquals(trip, "incFsUnit", "deg");
		assertEquals(trip, "incBsUnit", "%");
		assertEquals(trip, "distCorrection", "2.0");
		assertEquals(trip, "azmFsCorrection", "3.0");
		assertEquals(trip, "azmBsCorrection", "4.0");
		assertEquals(trip, "incFsCorrection", "5.0");
		assertEquals(trip, "incBsCorrection", "6.0");

		JsonArray survey = trip.getAsJsonArray("survey");
		Assert.assertEquals(survey.size(), 3);

		JsonObject sta1 = survey.get(0).getAsJsonObject();
		Assert.assertFalse(sta1.has("cave"));
		assertEquals(sta1, "station", "A1");
		JsonArray lrud = sta1.getAsJsonArray("lrud");
		assertEquals(lrud, 0, "1.0");
		assertEquals(lrud, 1, "2.0");
		assertEquals(lrud, 2, "3.0");
		assertEquals(lrud, 3, "4.0");
		assertEquals(sta1, "breakoutRow", 0);
		Assert.assertFalse(sta1.has("splays"));

		JsonObject sh1 = survey.get(1).getAsJsonObject();
		JsonArray measurements = sh1.getAsJsonArray("measurements");
		JsonObject m0 = measurements.get(0).getAsJsonObject();
		assertEquals(m0, "dir", "fs");
		assertEquals(m0, "dist", "1.0");
		assertEquals(m0, "azm", "180");
		assertEquals(m0, "inc", "-5");
		assertEquals(m0, "breakoutRow", 0);

		JsonObject m1 = measurements.get(1).getAsJsonObject();
		assertEquals(m1, "dir", "bs");
		Assert.assertFalse(m1.has("dist"));
		assertEquals(m1, "azm", "0");
		assertEquals(m1, "inc", "5");
		assertEquals(m1, "breakoutRow", 0);

		JsonObject m2 = measurements.get(2).getAsJsonObject();
		assertEquals(m0, "dir", "fs");
		assertEquals(m2, "dist", "3.0");
		assertEquals(m2, "azm", "90");
		assertEquals(m2, "inc", "-10");
		assertEquals(m2, "breakoutRow", 3);

		JsonObject sta2 = survey.get(2).getAsJsonObject();
		assertEquals(sta2, "cave", "Mammoth");
		assertEquals(sta2, "station", "A2");
		lrud = sta2.getAsJsonArray("lrud");
		assertEquals(lrud, 0, "1.0");
		assertEquals(lrud, 1, "2.0");
		assertEquals(lrud, 2, "3.0");
		assertEquals(lrud, 3, "4.0");
		assertEquals(sta2, "breakoutRow", 2);

		JsonArray spl2 = sta2.getAsJsonArray("splays");
		m0 = spl2.get(0).getAsJsonObject();
		assertEquals(m0, "dir", "fs");
		assertEquals(m0, "dist", "5.0");
		assertEquals(m0, "azm", "23.5");
		assertEquals(m0, "inc", "48.2");
		assertEquals(m0, "breakoutRow", 2);
	}
}
