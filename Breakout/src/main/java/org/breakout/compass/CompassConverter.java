package org.breakout.compass;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.andork.compass.CompassShot;
import org.andork.compass.CompassTrip;
import org.andork.compass.CompassTripHeader;
import org.andork.compass.LengthUnit;
import org.andork.compass.LrudAssociation;
import org.breakout.model.NewSurveyTableModel.Row;
import org.breakout.model.NewSurveyTableModel.Trip;

public class CompassConverter {
	public static List<Row> convertFromCompass(List<CompassTrip> compassTrips) {
		List<Row> shots = new ArrayList<Row>();

		LengthUnit lengthUnit = pickLengthUnit(compassTrips);

		for (CompassTrip compassTrip : compassTrips) {
			Trip trip = convertTripHeader(compassTrip.getHeader());

			List<Row> tripShots = convertShots(compassTrip, lengthUnit);

			for (Row shot : tripShots) {
				shot.setTrip(trip);
			}

			shots.addAll(tripShots);
		}

		return shots;
	}

	public static List<Row> convertShots(CompassTrip compassTrip, LengthUnit lengthUnit) {
		List<CompassShot> compassShots = compassTrip.getShots();
		List<Row> tripShots = new ArrayList<Row>();

		if (compassShots == null) {
			return tripShots;
		}
		for (CompassShot compassShot : compassTrip.getShots()) {
			Row shot = new Row();
			shot.setFromStation(compassShot.getFromStationName());
			shot.setToStation(compassShot.getToStationName());
			shot.setDistance(toString(LengthUnit.convert(compassShot.getLength(), lengthUnit)));
			shot.setFrontAzimuth(toString(compassShot.getFrontsightAzimuth()));
			shot.setBackAzimuth(toString(compassShot.getBacksightAzimuth()));
			shot.setFrontInclination(toString(compassShot.getFrontsightInclination()));
			shot.setBackInclination(toString(compassShot.getBacksightInclination()));
			tripShots.add(shot);
		}

		Iterator<Row> tripShotIter = tripShots.iterator();
		if (compassTrip.getHeader().getLrudAssociation() == LrudAssociation.TO && !compassShots.isEmpty()) {
			// add a row for the LRUDs at the to station of the last shot
			Row shot = new Row();
			shot.setFromStation(last(compassShots).getToStationName());
			tripShots.add(shot);

			// offset tripShotIter so that compass LRUDs for to station get
			// applied to (from station of) *next* shot
			tripShotIter = tripShots.iterator();
			tripShotIter.next();
		}
		Iterator<CompassShot> compassShotIter = compassShots.iterator();
		while (tripShotIter.hasNext() && compassShotIter.hasNext()) {
			Row shot = tripShotIter.next();
			CompassShot compassShot = compassShotIter.next();
			shot.setLeft(toString(compassShot.getLeft()));
			shot.setRight(toString(compassShot.getRight()));
			shot.setUp(toString(compassShot.getUp()));
			shot.setDown(toString(compassShot.getDown()));
		}
		return tripShots;
	}

	public static Trip convertTripHeader(CompassTripHeader compassTripHeader) {
		Trip trip = new Trip();
		trip.setCave(compassTripHeader.getCaveName());
		trip.setName(compassTripHeader.getComment());
		trip.setDate(toString(compassTripHeader.getDate()));
		trip.setSurveyors(toString(compassTripHeader.getTeam()));
		return trip;
	}

	private static <T> T last(List<T> list) {
		return list.listIterator(list.size()).previous();
	}

	public static LengthUnit pickLengthUnit(List<CompassTrip> compassTrips) {
		LengthUnit lengthUnit = LengthUnit.METERS;
		for (LengthUnit unit : LengthUnit.values()) {
			if (compassTrips.stream().allMatch(trip -> trip.getHeader().getLengthUnit() == unit)) {
				lengthUnit = unit;
			}
		}
		return lengthUnit;
	}

	private static String toString(double value) {
		return Double.isNaN(value) ? null : String.valueOf(value);
	}

	private static String toString(Object obj) {
		return obj == null ? null : obj.toString();
	}
}
