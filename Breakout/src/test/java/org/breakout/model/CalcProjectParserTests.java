package org.breakout.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;
import org.junit.Assert;
import org.junit.Test;

public class CalcProjectParserTests {
	@Test
	public void testParseDistance() {
		CalcProjectParser parser = new CalcProjectParser();
		SurveyTrip trip = new MutableSurveyTrip()
				.setDistanceUnit(Length.meters).toImmutable();

		SurveyRow row = new MutableSurveyRow().setDistance("3.54").setTrip(trip).toImmutable();
		CalcShot shot = parser.parse(row);
		Assert.assertEquals(new UnitizedDouble<>(3.54, Length.meters), shot.distance);

		SurveyTrip trip2 = trip.setDistanceCorrection("2.5");

		row = new MutableSurveyRow().setDistance("3 m 5 cm").setTrip(trip2).toImmutable();
		shot = parser.parse(row);
		Assert.assertEquals(new UnitizedDouble<>(5.55, Length.meters), shot.distance);

		row = new MutableSurveyRow().setTrip(trip2).toImmutable();
		shot = parser.parse(row);
		Assert.assertNull(shot.distance);

		row = new MutableSurveyRow().setTrip(trip2).setDistance("   ").toImmutable();
		shot = parser.parse(row);
		Assert.assertNull(shot.distance);

		row = new MutableSurveyRow().setTrip(trip2).setDistance(" g  ").toImmutable();
		shot = parser.parse(row);
		Assert.assertNull(shot.distance);
		Assert.assertEquals(
				Arrays.asList(ParseMessages.error("invalid number: g")),
				parser.messages.get(row, SurveyRow.Properties.distance));
	}

	@Test
	public void testParseAzimuth() {
		CalcProjectParser parser = new CalcProjectParser();
		SurveyTrip trip = new MutableSurveyTrip()
				.setBackAzimuthsCorrected(true)
				.setOverrideFrontAzimuthUnit(Angle.gradians).toImmutable();
		SurveyRow row = new MutableSurveyRow()
				.setFrontAzimuth("3.54")
				.setBackAzimuth("2")
				.setTrip(trip)
				.toImmutable();

		CalcShot shot = parser.parse(row);
		Assert.assertEquals(
				new UnitizedDouble<>(3.54, Angle.gradians)
						.add(new UnitizedDouble<>(2, Angle.degrees)).mul(0.5),
				shot.azimuth);

		SurveyTrip trip2 = trip.setBackAzimuthCorrection("2.5");

		row = new MutableSurveyRow().setBackAzimuth("3.05").setTrip(trip2).toImmutable();
		shot = parser.parse(row);
		Assert.assertEquals(new UnitizedDouble<>(5.55, Angle.degrees), shot.azimuth);

		row = new MutableSurveyRow().setTrip(trip2).toImmutable();
		shot = parser.parse(row);
		Assert.assertNull(shot.azimuth);

		row = new MutableSurveyRow().setTrip(trip2).setFrontAzimuth("   ").toImmutable();
		shot = parser.parse(row);
		Assert.assertNull(shot.azimuth);

		row = new MutableSurveyRow().setTrip(trip2).setFrontAzimuth(" g  ").toImmutable();
		shot = parser.parse(row);
		Assert.assertNull(shot.azimuth);
		System.out.println(row);
		System.out.println(parser.messages.entrySet());
		Assert.assertEquals(
				Arrays.asList(ParseMessages.error("invalid number: g")),
				parser.messages.get(row, SurveyRow.Properties.frontAzimuth));
	}

	@Test
	public void testParseInclination() {
		CalcProjectParser parser = new CalcProjectParser();
		SurveyTrip trip = new MutableSurveyTrip()
				.setBackInclinationsCorrected(true)
				.setOverrideFrontInclinationUnit(Angle.gradians).toImmutable();
		SurveyRow row = new MutableSurveyRow()
				.setFrontInclination("3.54")
				.setBackInclination("2")
				.setTrip(trip)
				.toImmutable();

		CalcShot shot = parser.parse(row);
		Assert.assertEquals(
				new UnitizedDouble<>(3.54, Angle.gradians)
						.add(new UnitizedDouble<>(2, Angle.degrees)).mul(0.5),
				shot.inclination);

		SurveyTrip trip2 = trip.setBackInclinationCorrection("2.5");

		row = new MutableSurveyRow().setBackInclination("3.05").setTrip(trip2).toImmutable();
		shot = parser.parse(row);
		Assert.assertEquals(new UnitizedDouble<>(5.55, Angle.degrees), shot.inclination);

		row = new MutableSurveyRow().setTrip(trip2).toImmutable();
		shot = parser.parse(row);
		Assert.assertNull(shot.inclination);

		row = new MutableSurveyRow().setTrip(trip2).setFrontInclination("   ").toImmutable();
		shot = parser.parse(row);
		Assert.assertNull(shot.inclination);

		row = new MutableSurveyRow().setTrip(trip2).setFrontInclination(" g  ").toImmutable();
		shot = parser.parse(row);
		Assert.assertNull(shot.inclination);
		Assert.assertEquals(
				Arrays.asList(ParseMessages.error("invalid number: g")),
				parser.messages.get(row, SurveyRow.Properties.frontInclination));
	}

	@Test
	public void testParseLruds() {
		SurveyTrip trip = new MutableSurveyTrip()
				.setDistanceUnit(Length.meters).toImmutable();
		CalcProjectParser parser = new CalcProjectParser();

		SurveyRow row = new MutableSurveyRow().setFromStation("A").setToStation("B").setLeft("1").setRight("2")
				.setUp("3").setDown("4")
				.setTrip(trip).toImmutable();
		CalcShot shot = parser.parse(row);
		UnitizedDouble<Length>[] lruds = shot.fromStation.crossSections.get(shot.toStation.key()).measurements;
		Assert.assertEquals(new UnitizedDouble<>(1, Length.meters), lruds[0]);
		Assert.assertEquals(new UnitizedDouble<>(2, Length.meters), lruds[1]);
		Assert.assertEquals(new UnitizedDouble<>(3, Length.meters), lruds[2]);
		Assert.assertEquals(new UnitizedDouble<>(4, Length.meters), lruds[3]);

		row = new MutableSurveyRow().setFromStation("B").setLeft("5").setRight("6").setUp("7").setDown("8")
				.setTrip(trip).toImmutable();
		parser.parse(row);
		lruds = shot.toStation.crossSections.get(shot.fromStation.key()).measurements;
		Assert.assertEquals(new UnitizedDouble<>(5, Length.meters), lruds[0]);
		Assert.assertEquals(new UnitizedDouble<>(6, Length.meters), lruds[1]);
		Assert.assertEquals(new UnitizedDouble<>(7, Length.meters), lruds[2]);
		Assert.assertEquals(new UnitizedDouble<>(8, Length.meters), lruds[3]);

		// row = new MutableSurveyRow().setTrip(trip).toImmutable();
		// shot = parser.parse(row);
		// Assert.assertNull(shot.left);
		// Assert.assertNull(shot.right);
		// Assert.assertNull(shot.up);
		// Assert.assertNull(shot.down);
		//
		// row = new MutableSurveyRow().setLeft("
		// ").setRight("\t").setUp("\f\r").setDown(" \n")
		// .setTrip(trip).toImmutable();
		// shot = parser.parse(row);
		// Assert.assertNull(shot.left);
		// Assert.assertNull(shot.right);
		// Assert.assertNull(shot.up);
		// Assert.assertNull(shot.down);
		//
		// row = new
		// MutableSurveyRow().setLeft("a").setRight("b").setUp("c").setDown("d")
		// .setTrip(trip).toImmutable();
		// shot = parser.parse(row);
		// Assert.assertNull(shot.left);
		// Assert.assertNull(shot.right);
		// Assert.assertNull(shot.up);
		// Assert.assertNull(shot.down);
		// Assert.assertEquals(
		// Arrays.asList(ParseMessage.error("invalid number: a")),
		// shot.messages.get(SurveyRow.Properties.left));
		// Assert.assertEquals(
		// Arrays.asList(ParseMessage.error("invalid number: b")),
		// shot.messages.get(SurveyRow.Properties.right));
		// Assert.assertEquals(
		// Arrays.asList(ParseMessage.error("invalid number: c")),
		// shot.messages.get(SurveyRow.Properties.up));
		// Assert.assertEquals(
		// Arrays.asList(ParseMessage.error("invalid number: d")),
		// shot.messages.get(SurveyRow.Properties.down));
		// }
		//
		// @Test
		// public void testParseNev() {
		// CalcProjectParser parser = new CalcProjectParser();
		// SurveyTrip trip = new MutableSurveyTrip()
		// .setDistanceUnit(Length.meters).toImmutable();
		//
		// SurveyRow row = new MutableSurveyRow().setFromStation("A")
		// .setNorthing("1").setEasting("2").setElevation("3")
		// .setTrip(trip).toImmutable();
		// CalcRow shot = parser.parse(row);
		// Assert.assertEquals(new UnitizedDouble<>(1, Length.meters),
		// shot.fromStation.northing);
		// Assert.assertEquals(new UnitizedDouble<>(2, Length.meters),
		// shot.fromStation.easting);
		// Assert.assertEquals(new UnitizedDouble<>(3, Length.meters),
		// shot.fromStation.elevation);
		//
		// // make sure there's no NPE when there's no station name
		// row = new MutableSurveyRow()
		// .setNorthing("1").setEasting("2").setElevation("3")
		// .setTrip(trip).toImmutable();
		// parser.parse(row);
		//
		// row = new
		// MutableSurveyRow().setFromStation("B").setTrip(trip).toImmutable();
		// shot = parser.parse(row);
		// Assert.assertNull(shot.fromStation.northing);
		// Assert.assertNull(shot.fromStation.easting);
		// Assert.assertNull(shot.fromStation.elevation);
		//
		// row = new MutableSurveyRow().setFromStation("C").setNorthing("
		// ").setEasting("\t").setElevation("\f\r")
		// .setTrip(trip).toImmutable();
		// shot = parser.parse(row);
		// Assert.assertNull(shot.fromStation.northing);
		// Assert.assertNull(shot.fromStation.easting);
		// Assert.assertNull(shot.fromStation.elevation);
		//
		// row = new
		// MutableSurveyRow().setFromStation("D").setNorthing("a").setEasting("b").setElevation("c")
		// .setTrip(trip).toImmutable();
		// shot = parser.parse(row);
		// Assert.assertNull(shot.fromStation.northing);
		// Assert.assertNull(shot.fromStation.easting);
		// Assert.assertNull(shot.fromStation.elevation);
		// Assert.assertEquals(
		// Arrays.asList(ParseMessage.error("invalid number: a")),
		// shot.messages.get(SurveyRow.Properties.northing));
		// Assert.assertEquals(
		// Arrays.asList(ParseMessage.error("invalid number: b")),
		// shot.messages.get(SurveyRow.Properties.easting));
		// Assert.assertEquals(
		// Arrays.asList(ParseMessage.error("invalid number: c")),
		// shot.messages.get(SurveyRow.Properties.elevation));
	}

	@Test
	public void testLinkRow() {
		CalcProjectParser parser = new CalcProjectParser();

		SurveyTrip trip = new SurveyTrip();

		List<CalcShot> rows = Arrays.asList(
				parser.parse(
						new MutableSurveyRow().setTrip(trip).setFromStation("A1").setToStation("A2").toImmutable()),
				parser.parse(
						new MutableSurveyRow().setTrip(trip).setFromStation("A2").setToStation("A3").toImmutable()),
				parser.parse(
						new MutableSurveyRow().setTrip(trip).setFromStation("A3").setToStation("A4").toImmutable()),
				parser.parse(new MutableSurveyRow().setTrip(trip).setFromStation("A4").toImmutable()),
				parser.parse(
						new MutableSurveyRow().setTrip(trip).setFromStation("A2").setToStation("B1").toImmutable()),
				parser.parse(
						new MutableSurveyRow().setTrip(trip).setFromStation("B1").setToStation("B2").toImmutable()),
				parser.parse(
						new MutableSurveyRow().setTrip(trip).setFromStation("B2").setToStation("A1").toImmutable()),
				parser.parse(
						new MutableSurveyRow().setTrip(trip).setFromStation("A3").setToStation("A2").toImmutable()));

		Assert.assertEquals(rows.get(7), rows.get(1).overriddenBy);
		Assert.assertEquals(rows.get(1), rows.get(7).overrides);

		CalcProject project = parser.project;

		Assert.assertEquals(8, project.rows.size());
		Assert.assertEquals(6, project.shots.size());
		Assert.assertEquals(6, project.stations.size());

		Assert.assertEquals(rows, project.rows);

		Map<ShotKey, CalcShot> shots = new HashMap<>();
		shots.put(new ShotKey(null, "A1", null, "A2"), rows.get(0));
		shots.put(new ShotKey(null, "A3", null, "A4"), rows.get(2));
		shots.put(new ShotKey(null, "A2", null, "B1"), rows.get(4));
		shots.put(new ShotKey(null, "B1", null, "B2"), rows.get(5));
		shots.put(new ShotKey(null, "B2", null, "A1"), rows.get(6));
		shots.put(new ShotKey(null, "A2", null, "A3"), rows.get(7));

		Assert.assertEquals(shots, project.shots);

		Map<StationKey, CalcStation> stations = new HashMap<>();
		stations.put(new StationKey(null, "A1"), rows.get(0).fromStation);
		stations.put(new StationKey(null, "A2"), rows.get(1).fromStation);
		stations.put(new StationKey(null, "A3"), rows.get(2).fromStation);
		stations.put(new StationKey(null, "A4"), rows.get(3).fromStation);
		stations.put(new StationKey(null, "B1"), rows.get(5).fromStation);
		stations.put(new StationKey(null, "B2"), rows.get(6).fromStation);

		Assert.assertEquals(stations, project.stations);

		for (CalcShot shot : shots.values()) {
			Assert.assertEquals(shot.fromStation.shots.get(new StationKey(null, shot.toStation.name)), shot);
			Assert.assertEquals(shot.toStation.shots.get(new StationKey(null, shot.fromStation.name)), shot);
		}

		for (CalcStation station : stations.values()) {
			for (Map.Entry<StationKey, CalcShot> entry : station.shots.entrySet()) {
				CalcShot shot = entry.getValue();
				Assert.assertTrue(station == shot.fromStation || station == shot.toStation);
				if (station == shot.fromStation) {
					Assert.assertEquals(entry.getKey().station, shot.toStation.name);
				} else {
					Assert.assertEquals(entry.getKey().station, shot.fromStation.name);
				}
			}
		}
	}
}