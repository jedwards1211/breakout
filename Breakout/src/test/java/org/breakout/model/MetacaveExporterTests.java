package org.breakout.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.breakout.model.SurveyTableModel.Row;
import org.breakout.model.SurveyTableModel.Trip;
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
		List<Row> rows = new ArrayList<Row>();

		Trip _trip = new Trip();
		_trip.setCave("Fisher Ridge");
		_trip.setName("Trip 1");
		_trip.setDate("2016-01-01");
		_trip.setSurveyors(Arrays.asList("Andy Edwards", "Sean Lewis"));
		_trip.setDistanceUnit(Length.feet);
		_trip.setAngleUnit(Angle.degrees);
		_trip.setBackAzimuthsCorrected(false);
		_trip.setBackInclinationsCorrected(true);
		_trip.setDeclination("1.0");
		_trip.setOverrideFrontAzimuthUnit(Angle.gradians);
		_trip.setOverrideBackAzimuthUnit(Angle.milsNATO);
		_trip.setOverrideFrontInclinationUnit(Angle.degrees);
		_trip.setOverrideBackInclinationUnit(Angle.percentGrade);
		_trip.setDistanceCorrection("2.0");
		_trip.setFrontAzimuthCorrection("3.0");
		_trip.setBackAzimuthCorrection("4.0");
		_trip.setFrontInclinationCorrection("5.0");
		_trip.setBackInclinationCorrection("6.0");

		Row _r0 = new Row();
		_r0.setTrip(_trip);
		_r0.setOverrideFromCave(null);
		_r0.setFromStation("A1");
		_r0.setOverrideToCave("Mammoth");
		_r0.setToStation("A2");
		_r0.setLeft("1.0");
		_r0.setRight("2.0");
		_r0.setUp("3.0");
		_r0.setDown("4.0");
		_r0.setDistance("1.0");
		_r0.setFrontAzimuth("180");
		_r0.setFrontInclination("-5");
		_r0.setBackAzimuth("0");
		_r0.setBackInclination("5");

		Row _r3 = new Row();
		_r3.setTrip(_trip);
		_r3.setOverrideFromCave(null);
		_r3.setFromStation("A1");
		_r3.setOverrideToCave("Mammoth");
		_r3.setToStation("A2");
		_r3.setLeft(null);
		_r3.setRight(null);
		_r3.setUp(null);
		_r3.setDown(null);
		_r3.setDistance("3.0");
		_r3.setFrontAzimuth("90");
		_r3.setFrontInclination("-10");
		_r3.setBackAzimuth(null);
		_r3.setBackInclination(null);

		Row _r2 = new Row();
		_r2.setTrip(_trip);
		_r2.setOverrideFromCave("Mammoth");
		_r2.setFromStation("A2");
		_r2.setOverrideToCave(null);
		_r2.setToStation(null);
		_r2.setLeft("1.0");
		_r2.setRight("2.0");
		_r2.setUp("3.0");
		_r2.setDown("4.0");
		_r2.setDistance("5.0");
		_r2.setFrontAzimuth("23.5");
		_r2.setFrontInclination("48.2");
		_r2.setBackAzimuth(null);
		_r2.setBackInclination(null);

		rows.add(_r0);
		rows.add(null);
		rows.add(_r2);
		rows.add(_r3);

		MetacaveExporter exporter = new MetacaveExporter();
		exporter.export(rows);

		System.out.println(new GsonBuilder().setPrettyPrinting().create()
				.toJson(exporter.getRoot()));

		JsonObject trip = exporter.getRoot()
				.getAsJsonObject("caves")
				.getAsJsonObject("Fisher Ridge")
				.getAsJsonArray("trips")
				.get(0).getAsJsonObject();

		assertEquals(trip, "name", "Trip 1");
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
