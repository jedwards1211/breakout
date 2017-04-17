package org.breakout.compass;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.andork.compass.AzimuthUnit;
import org.andork.compass.InclinationUnit;
import org.andork.compass.LengthUnit;
import org.andork.compass.LrudAssociation;
import org.andork.compass.survey.CompassShot;
import org.andork.compass.survey.CompassTrip;
import org.andork.compass.survey.CompassTripHeader;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.breakout.model.raw.MutableSurveyRow;
import org.breakout.model.raw.MutableSurveyTrip;
import org.breakout.model.raw.SurveyRow;
import org.breakout.model.raw.SurveyTrip;

public class CompassConverter {
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

	public static List<SurveyRow> convertFromCompass(List<CompassTrip> compassTrips) {
		List<SurveyRow> shots = new ArrayList<SurveyRow>();

		for (CompassTrip compassTrip : compassTrips) {
			shots.addAll(convertShots(compassTrip));
		}

		return shots;
	}

	public static List<SurveyRow> convertShots(CompassTrip compassTrip) {
		SurveyTrip trip = convertTripHeader(compassTrip.getHeader());

		List<CompassShot> compassShots = compassTrip.getShots();
		List<SurveyRow> tripShots = new ArrayList<SurveyRow>();
		LengthUnit lengthUnit = compassTrip.getHeader().getLengthUnit();

		if (compassShots == null) {
			return tripShots;
		}
		for (CompassShot compassShot : compassTrip.getShots()) {
			MutableSurveyRow shot = new MutableSurveyRow();
			shot.setTrip(trip);
			shot.setFromStation(compassShot.getFromStationName());
			shot.setToStation(compassShot.getToStationName());
			shot.setDistance(toString(LengthUnit.convert(compassShot.getLength(), lengthUnit)));
			shot.setFrontAzimuth(toString(compassShot.getFrontsightAzimuth()));
			shot.setBackAzimuth(toString(compassShot.getBacksightAzimuth()));
			shot.setFrontInclination(toString(compassShot.getFrontsightInclination()));
			shot.setBackInclination(toString(compassShot.getBacksightInclination()));
			tripShots.add(shot.toImmutable());
		}

		ListIterator<SurveyRow> tripShotIter = tripShots.listIterator();
		if (compassTrip.getHeader().getLrudAssociation() == LrudAssociation.TO && !compassShots.isEmpty()) {
			// add a row for the LRUDs at the to station of the last shot
			SurveyRow shot = new MutableSurveyRow()
					.setTrip(trip)
					.setFromStation(last(compassShots).getToStationName())
					.toImmutable();
			tripShots.add(shot);

			// offset tripShotIter so that compass LRUDs for to station get
			// applied to (from station of) *next* shot
			tripShotIter = tripShots.listIterator();
			tripShotIter.next();
		}
		Iterator<CompassShot> compassShotIter = compassShots.iterator();
		while (tripShotIter.hasNext() && compassShotIter.hasNext()) {
			SurveyRow shot = tripShotIter.next();
			CompassShot compassShot = compassShotIter.next();
			tripShotIter.set(shot.withMutations(s -> {
				s.setLeft(toString(LengthUnit.convert(compassShot.getLeft(), lengthUnit)));
				s.setRight(toString(LengthUnit.convert(compassShot.getRight(), lengthUnit)));
				s.setUp(toString(LengthUnit.convert(compassShot.getUp(), lengthUnit)));
				s.setDown(toString(LengthUnit.convert(compassShot.getDown(), lengthUnit)));
			}));
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

	public static SurveyTrip convertTripHeader(CompassTripHeader compassTripHeader) {
		MutableSurveyTrip trip = new MutableSurveyTrip();
		trip.setCave(compassTripHeader.getCaveName());
		trip.setName(compassTripHeader.getComment());
		trip.setDate(toString(compassTripHeader.getDate()));
		trip.setSurveyors(convertTeam(compassTripHeader.getTeam()));

		trip.setDistanceCorrection(toString(
				LengthUnit.convert(compassTripHeader.getLengthCorrection(), compassTripHeader.getLengthUnit())));

		trip.setDeclination(toString(
				AzimuthUnit.convert(compassTripHeader.getDeclination(), compassTripHeader.getAzimuthUnit())));

		trip.setFrontAzimuthCorrection(toString(
				AzimuthUnit.convert(compassTripHeader.getFrontsightAzimuthCorrection(),
						compassTripHeader.getAzimuthUnit())));
		trip.setBackAzimuthCorrection(toString(
				AzimuthUnit.convert(compassTripHeader.getBacksightAzimuthCorrection(),
						compassTripHeader.getAzimuthUnit())));
		trip.setBackAzimuthsCorrected(false);

		trip.setFrontInclinationCorrection(toString(
				InclinationUnit.convert(compassTripHeader.getFrontsightInclinationCorrection(),
						compassTripHeader.getInclinationUnit())));
		trip.setBackInclinationCorrection(toString(
				InclinationUnit.convert(compassTripHeader.getBacksightInclinationCorrection(),
						compassTripHeader.getInclinationUnit())));
		trip.setBackInclinationsCorrected(false);

		trip.setDistanceUnit(compassTripHeader.getLengthUnit() == LengthUnit.METERS
				? Length.meters : Length.feet);
		Unit<Angle> azimuthUnit = compassTripHeader.getAzimuthUnit() == AzimuthUnit.GRADS
				? Angle.gradians : Angle.degrees;
		Unit<Angle> inclinationUnit = compassTripHeader.getInclinationUnit() == InclinationUnit.PERCENT_GRADE
				? Angle.percentGrade : compassTripHeader.getInclinationUnit() == InclinationUnit.GRADS
						? Angle.gradians : Angle.degrees;
		trip.setAngleUnit(azimuthUnit);
		if (azimuthUnit != inclinationUnit) {
			trip.setOverrideFrontAzimuthUnit(azimuthUnit);
			trip.setOverrideBackAzimuthUnit(azimuthUnit);
			trip.setOverrideFrontInclinationUnit(inclinationUnit);
			trip.setOverrideBackInclinationUnit(inclinationUnit);
		}
		return trip.toImmutable();
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

	private static String toString(BigDecimal value) {
		return value == null ? null : value.toString();
	}

	private static String toString(Date date) {
		return date == null ? null : dateFormat.format(date);
	}
}
