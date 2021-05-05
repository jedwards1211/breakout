package org.breakout.compass;

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
import org.andork.task.Task;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitType;
import org.andork.unit.UnitizedNumber;
import org.breakout.model.raw.MutableSurveyRow;
import org.breakout.model.raw.MutableSurveyTrip;
import org.breakout.model.raw.SurveyRow;
import org.breakout.model.raw.SurveyTrip;

public class CompassConverter {
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

	public static List<SurveyRow> convertFromCompass(List<CompassTrip> compassTrips, Task<?> subtask) {
		List<SurveyRow> shots = new ArrayList<SurveyRow>();

		subtask.setTotal(compassTrips.size());
		for (CompassTrip compassTrip : compassTrips) {
			shots.addAll(convertShots(compassTrip));
			subtask.increment();
		}

		return shots;
	}

	public static List<SurveyRow> convertShots(CompassTrip compassTrip) {
		SurveyTrip trip = convertTripHeader(compassTrip.getHeader());

		List<CompassShot> compassShots = compassTrip.getShots();
		List<SurveyRow> tripShots = new ArrayList<SurveyRow>();
		Unit<Length> distUnit = trip.getDistanceUnit();
		Unit<Angle> frontAzmUnit = trip.getFrontAzimuthUnit();
		Unit<Angle> backAzmUnit = trip.getBackAzimuthUnit();
		Unit<Angle> frontIncUnit = trip.getFrontInclinationUnit();
		Unit<Angle> backIncUnit = trip.getBackInclinationUnit();

		if (compassShots == null) {
			return tripShots;
		}
		for (CompassShot compassShot : compassTrip.getShots()) {
			if (compassShot.isExcludedFromAllProcessing())
				continue;
			MutableSurveyRow shot = new MutableSurveyRow();
			shot.setTrip(trip);
			shot.setFromStation(compassShot.getFromStationName());
			shot.setToStation(compassShot.getToStationName());
			shot.setDistance(toString(compassShot.getLength(), distUnit));
			shot.setFrontAzimuth(toString(compassShot.getFrontsightAzimuth(), frontAzmUnit));
			shot.setBackAzimuth(toString(compassShot.getBacksightAzimuth(), backAzmUnit));
			shot.setFrontInclination(toString(compassShot.getFrontsightInclination(), frontIncUnit));
			shot.setBackInclination(toString(compassShot.getBacksightInclination(), backIncUnit));
			shot.setExcludeDistance(compassShot.isExcludedFromLength());
			shot.setExcludeFromPlotting(compassShot.isExcludedFromPlotting());
			tripShots.add(shot.toImmutable());
		}

		ListIterator<SurveyRow> tripShotIter = tripShots.listIterator();
		if (compassTrip.getHeader().getLrudAssociation() == LrudAssociation.TO && !compassShots.isEmpty()) {
			// add a row for the LRUDs at the to station of the last shot
			SurveyRow shot =
				new MutableSurveyRow()
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
			CompassShot compassShot = compassShotIter.next();
			if (compassShot.isExcludedFromAllProcessing())
				continue;
			SurveyRow shot = tripShotIter.next();
			tripShotIter.set(shot.withMutations(s -> {
				s.setLeft(toString(compassShot.getLeft(), distUnit));
				s.setRight(toString(compassShot.getRight(), distUnit));
				s.setUp(toString(compassShot.getUp(), distUnit));
				s.setDown(toString(compassShot.getDown(), distUnit));
			}));
		}
		return tripShots;

	}

	public static List<String> convertTeam(String team) {
		if (team == null) {
			return null;
		}
		return new ArrayList<>(Arrays.asList(team.trim().split(team.indexOf(';') >= 0 ? "\\s*;\\s*" : "\\s*,\\s*")));
	}

	public static SurveyTrip convertTripHeader(CompassTripHeader compassTripHeader) {
		MutableSurveyTrip trip = new MutableSurveyTrip();
		// trip.setCave(compassTripHeader.getCaveName());

		String tripName = compassTripHeader.getSurveyName();
		if (tripName != null)
			tripName = tripName.trim();
		String comment = compassTripHeader.getComment();
		if (comment != null)
			comment = comment.trim();

		if (comment != null && !comment.isEmpty()) {
			if (tripName == null || tripName.isEmpty())
				tripName = comment;
			else
				tripName += " (" + compassTripHeader.getComment() + ")";
		}
		trip.setName(tripName);
		trip.setDate(toString(compassTripHeader.getDate()));
		trip.setSurveyors(convertTeam(compassTripHeader.getTeam()));

		Unit<Length> distUnit = compassTripHeader.getLengthUnit() == LengthUnit.METERS ? Length.meters : Length.feet;
		trip.setDistanceUnit(distUnit);
		Unit<Angle> azimuthUnit =
			compassTripHeader.getAzimuthUnit() == AzimuthUnit.GRADS ? Angle.gradians : Angle.degrees;
		Unit<Angle> inclinationUnit =
			compassTripHeader.getInclinationUnit() == InclinationUnit.PERCENT_GRADE
				? Angle.percentGrade
				: compassTripHeader.getInclinationUnit() == InclinationUnit.GRADS ? Angle.gradians : Angle.degrees;
		trip.setAngleUnit(azimuthUnit);
		if (azimuthUnit != inclinationUnit) {
			trip.setOverrideFrontAzimuthUnit(azimuthUnit);
			trip.setOverrideBackAzimuthUnit(azimuthUnit);
			trip.setOverrideFrontInclinationUnit(inclinationUnit);
			trip.setOverrideBackInclinationUnit(inclinationUnit);
		}

		trip.setDistanceCorrection(toString(compassTripHeader.getLengthCorrection(), distUnit));
		trip.setDeclination(toString(compassTripHeader.getDeclination(), azimuthUnit));
		trip.setFrontAzimuthCorrection(toString(compassTripHeader.getFrontsightAzimuthCorrection(), azimuthUnit));
		trip.setBackAzimuthCorrection(toString(compassTripHeader.getBacksightAzimuthCorrection(), azimuthUnit));
		trip.setBackAzimuthsCorrected(false);
		trip
			.setFrontInclinationCorrection(
				toString(compassTripHeader.getFrontsightInclinationCorrection(), inclinationUnit));
		trip
			.setBackInclinationCorrection(
				toString(compassTripHeader.getBacksightInclinationCorrection(), inclinationUnit));
		trip.setBackInclinationsCorrected(false);

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

	private static <T extends UnitType<T>> String toString(UnitizedNumber<T> value, Unit<T> unit) {
		return value == null ? null : String.valueOf(value.get(unit));
	}

	private static String toString(Date date) {
		return date == null ? null : dateFormat.format(date);
	}
}
