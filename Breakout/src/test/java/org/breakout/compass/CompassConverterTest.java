package org.breakout.compass;

import java.util.Arrays;
import java.util.List;

import org.andork.compass.CompassShot;
import org.andork.compass.CompassTrip;
import org.andork.compass.CompassTripHeader;
import org.andork.compass.LengthUnit;
import org.andork.compass.LrudAssociation;
import org.breakout.model.NewSurveyTableModel.Row;
import org.breakout.model.NewSurveyTableModel.Trip;
import org.junit.Assert;
import org.junit.Test;

public class CompassConverterTest {
	@Test
	public void lengthConversionTest() {
		CompassTripHeader header = new CompassTripHeader();
		header.setLengthUnit(LengthUnit.DECIMAL_FEET);
		CompassTrip trip = new CompassTrip();
		trip.setHeader(header);
		CompassShot shot = new CompassShot();
		shot.setLength(1.0);
		shot.setLeft(2.0);
		trip.setShots(Arrays.asList(shot));

		List<Row> rows = CompassConverter.convertShots(trip, LengthUnit.METERS);
		Assert.assertEquals(1, rows.size());
		Row row = rows.get(0);
		Assert.assertEquals("0.3048", row.getDistance());
		Assert.assertEquals("0.6096", row.getLeft());
	}

	@Test
	public void testEmptyShot() {
		CompassTripHeader header = new CompassTripHeader();
		CompassTrip trip = new CompassTrip();
		trip.setHeader(header);
		CompassShot shot = new CompassShot();
		trip.setShots(Arrays.asList(shot));

		List<Row> rows = CompassConverter.convertShots(trip, LengthUnit.METERS);
		Assert.assertEquals(1, rows.size());
		Row row = rows.get(0);
		Assert.assertNull(row.getFromCave());
		Assert.assertNull(row.getFromStation());
		Assert.assertNull(row.getToCave());
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
		List<Row> rows = CompassConverter.convertShots(trip, LengthUnit.METERS);
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
		shot.setLeft(3.5);
		trip.setShots(Arrays.asList(shot));

		List<Row> rows = CompassConverter.convertShots(trip, LengthUnit.DECIMAL_FEET);
		Assert.assertEquals(1, rows.size());
		Assert.assertEquals("3.5", rows.get(0).getLeft());
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
		shot.setLeft(3.5);
		trip.setShots(Arrays.asList(shot));

		List<Row> rows = CompassConverter.convertShots(trip, LengthUnit.DECIMAL_FEET);
		Assert.assertEquals(2, rows.size());
		Assert.assertEquals(null, rows.get(0).getLeft());
		Assert.assertEquals("A1", rows.get(0).getFromStation());
		Assert.assertEquals("A2", rows.get(0).getToStation());
		Assert.assertEquals("A2", rows.get(1).getFromStation());
		Assert.assertEquals("3.5", rows.get(1).getLeft());
	}
}
