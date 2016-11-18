package org.breakout.compass;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.andork.compass.LengthUnit;
import org.andork.compass.LrudAssociation;
import org.andork.compass.survey.CompassShot;
import org.andork.compass.survey.CompassTrip;
import org.andork.compass.survey.CompassTripHeader;
import org.breakout.model.SurveyTableModel.Row;
import org.breakout.model.SurveyTableModel.Trip;
import org.junit.Assert;
import org.junit.Test;

public class CompassConverterTest {
	@Test
	public void lengthConversionTest() {
		CompassTripHeader header = new CompassTripHeader();
		header.setLengthUnit(LengthUnit.METERS);
		CompassTrip trip = new CompassTrip();
		trip.setHeader(header);
		CompassShot shot = new CompassShot();
		shot.setLength(new BigDecimal(1.0));
		shot.setLeft(new BigDecimal(2.0));
		trip.setShots(Arrays.asList(shot));

		List<Row> rows = CompassConverter.convertShots(trip);
		Assert.assertEquals(1, rows.size());
		Row row = rows.get(0);
		Assert.assertEquals("0.3048", row.getDistance());
		Assert.assertEquals("0.6096", row.getLeft());
	}

	@Test
	public void testConvertTeam() {
		Assert.assertEquals(null, CompassConverter.convertTeam(null));
		Assert.assertEquals(Arrays.asList("Edwards, Andy", "Lewis, Sean"),
				CompassConverter.convertTeam("  \tEdwards, Andy; Lewis, Sean  "));
		Assert.assertEquals(Arrays.asList("Andy", "Sean"),
				CompassConverter.convertTeam("  Andy    , Sean   "));
	}

	@Test
	public void testEmptyShot() {
		CompassTripHeader header = new CompassTripHeader();
		header.setLengthUnit(LengthUnit.METERS);
		CompassTrip trip = new CompassTrip();
		trip.setHeader(header);
		CompassShot shot = new CompassShot();
		trip.setShots(Arrays.asList(shot));

		List<Row> rows = CompassConverter.convertShots(trip);
		Assert.assertEquals(1, rows.size());
		Row row = rows.get(0);
		Assert.assertNull(row.getOverrideFromCave());
		Assert.assertNull(row.getFromStation());
		Assert.assertNull(row.getOverrideToCave());
		Assert.assertNull(row.getToStation());
		Assert.assertNull(row.getDistance());
		Assert.assertNull(row.getFrontAzimuth());
		Assert.assertNull(row.getFrontInclination());
		Assert.assertNull(row.getBackAzimuth());
		Assert.assertNull(row.getBackInclination());
		Assert.assertNull(row.getLeft());
		Assert.assertNull(row.getRight());
		Assert.assertNull(row.getUp());
		Assert.assertNull(row.getDown());
		Assert.assertNull(row.getNorthing());
		Assert.assertNull(row.getEasting());
		Assert.assertNull(row.getElevation());
	}

	@Test
	public void testEmptyTrip() {
		CompassTrip trip = new CompassTrip();
		trip.setHeader(new CompassTripHeader());
		List<Row> rows = CompassConverter.convertShots(trip);
		Assert.assertEquals(0, rows.size());
	}

	@Test
	public void testEmptyTripHeader() {
		CompassTripHeader header = new CompassTripHeader();
		Trip trip = CompassConverter.convertTripHeader(header);
		Assert.assertNull(trip.getCave());
		Assert.assertNull(trip.getName());
		Assert.assertNull(trip.getSurveyors());
		Assert.assertNull(trip.getDate());
	}

	@Test
	public void testFromLruds() {
		CompassTripHeader header = new CompassTripHeader();
		header.setLrudAssociation(LrudAssociation.FROM);
		CompassTrip trip = new CompassTrip();
		trip.setHeader(header);
		CompassShot shot = new CompassShot();
		shot.setLeft(new BigDecimal(3.5));
		trip.setShots(Arrays.asList(shot));

		List<Row> rows = CompassConverter.convertShots(trip);
		Assert.assertEquals(1, rows.size());
		Assert.assertEquals("3.5", rows.get(0).getLeft());
	}

	@Test
	public void testFullShot() {
		CompassTripHeader header = new CompassTripHeader();
		header.setLrudAssociation(LrudAssociation.FROM);
		CompassTrip trip = new CompassTrip();
		trip.setHeader(header);
		CompassShot shot = new CompassShot();
		shot.setFromStationName("A");
		shot.setToStationName("B");
		shot.setLength(new BigDecimal(1.5));
		shot.setFrontsightAzimuth(new BigDecimal(2.5));
		shot.setFrontsightInclination(new BigDecimal(3.5));
		shot.setBacksightAzimuth(new BigDecimal(4.5));
		shot.setBacksightInclination(new BigDecimal(5.5));
		shot.setLeft(new BigDecimal(6.5));
		shot.setRight(new BigDecimal(7.5));
		shot.setUp(new BigDecimal(8.5));
		shot.setDown(new BigDecimal(9.5));
		trip.setShots(Arrays.asList(shot));

		List<Row> rows = CompassConverter.convertShots(trip);
		Assert.assertEquals(1, rows.size());
		Row row = rows.get(0);
		Assert.assertNull(row.getOverrideFromCave());
		Assert.assertEquals("A", row.getFromStation());
		Assert.assertNull(row.getOverrideToCave());
		Assert.assertEquals("B", row.getToStation());
		Assert.assertEquals("1.5", row.getDistance());
		Assert.assertEquals("2.5", row.getFrontAzimuth());
		Assert.assertEquals("3.5", row.getFrontInclination());
		Assert.assertEquals("4.5", row.getBackAzimuth());
		Assert.assertEquals("5.5", row.getBackInclination());
		Assert.assertEquals("6.5", row.getLeft());
		Assert.assertEquals("7.5", row.getRight());
		Assert.assertEquals("8.5", row.getUp());
		Assert.assertEquals("9.5", row.getDown());
		Assert.assertNull(row.getNorthing());
		Assert.assertNull(row.getEasting());
		Assert.assertNull(row.getElevation());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testFullTripHeader() {
		CompassTripHeader header = new CompassTripHeader();
		header.setCaveName("Fisher Ridge");
		header.setSurveyName("MAM");
		header.setComment("The secret connection");
		header.setTeam(" Sean Lewis;    Andy Edwards   ; Ronnie Harrison ");
		header.setDate(new Date(2016 - 1900, 11, 11));
		Trip trip = CompassConverter.convertTripHeader(header);
		Assert.assertEquals("Fisher Ridge", trip.getCave());
		Assert.assertEquals("The secret connection", trip.getName());
		Assert.assertEquals("2016/12/11", trip.getDate());
		Assert.assertEquals(Arrays.asList("Sean Lewis", "Andy Edwards", "Ronnie Harrison"),
				trip.getSurveyors());
	}

	@Test
	public void testToLruds() {
		CompassTripHeader header = new CompassTripHeader();
		header.setLrudAssociation(LrudAssociation.TO);
		CompassTrip trip = new CompassTrip();
		trip.setHeader(header);
		CompassShot shot = new CompassShot();
		shot.setFromStationName("A1");
		shot.setToStationName("A2");
		shot.setLeft(new BigDecimal(3.5));
		trip.setShots(Arrays.asList(shot));

		List<Row> rows = CompassConverter.convertShots(trip);
		Assert.assertEquals(2, rows.size());
		Assert.assertEquals(null, rows.get(0).getLeft());
		Assert.assertEquals("A1", rows.get(0).getFromStation());
		Assert.assertEquals("A2", rows.get(0).getToStation());
		Assert.assertEquals("A2", rows.get(1).getFromStation());
		Assert.assertEquals("3.5", rows.get(1).getLeft());
	}
}
