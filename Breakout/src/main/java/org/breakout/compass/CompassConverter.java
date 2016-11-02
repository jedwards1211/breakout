package org.breakout.compass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.andork.compass.CompassShot;
import org.andork.compass.CompassTrip;
import org.andork.compass.CompassTripHeader;
import org.andork.compass.LengthUnit;
import org.andork.compass.LrudAssociation;
import org.breakout.model.SurveyTableModel.Row;
import org.breakout.model.SurveyTableModel.Trip;

public class CompassConverter {
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

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
			shot.setLeft(toString(LengthUnit.convert(compassShot.getLeft(), lengthUnit)));
			shot.setRight(toString(LengthUnit.convert(compassShot.getRight(), lengthUnit)));
			shot.setUp(toString(LengthUnit.convert(compassShot.getUp(), lengthUnit)));
			shot.setDown(toString(LengthUnit.convert(compassShot.getDown(), lengthUnit)));
		}
		return tripShots;
	}

	public static List<String> convertTeam(String team) {
		if (team == null) {
			return null;
		}
		return new ArrayList<>(Arrays.asList(
				team.trim().split(team.indexOf(';') >= 0 ? "\\s*;\\s*" : "\\s*,\\s*")));
	}

	public static Trip convertTripHeader(CompassTripHeader compassTripHeader) {
		Trip trip = new Trip();
		trip.setCave(compassTripHeader.getCaveName());
		trip.setName(compassTripHeader.getComment());
		trip.setDate(toString(compassTripHeader.getDate()));
		trip.setSurveyors(convertTeam(compassTripHeader.getTeam()));
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

	private static String toString(Date date) {
		return date == null ? null : dateFormat.format(date);
	}

	private static String toString(double value) {
		return Double.isNaN(value) ? null : String.valueOf(value);
	}
}
