package org.breakout.model.parsed;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public class ParsedShotMeasurement {
	public boolean isBacksight;
	public ParsedField<UnitizedDouble<Length>> distance;
	public ParsedField<UnitizedDouble<Angle>> azimuth;
	public ParsedField<UnitizedDouble<Angle>> inclination;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParsedShotMeasurement [isBacksight=").append(isBacksight).append(", distance=").append(distance)
				.append(", azimuth=").append(azimuth).append(", inclination=").append(inclination).append("]");
		return builder.toString();
	}

	public static UnitizedDouble<Length> getFirstDistance(Iterable<ParsedShotMeasurement> measurements) {
		for (ParsedShotMeasurement measurement : measurements) {
			UnitizedDouble<Length> distance = ParsedField.getValue(measurement.distance);
			if (distance != null) {
				return distance;
			}
		}
		return null;
	}

	public static UnitizedDouble<Angle> getFirstFrontAzimuth(Iterable<ParsedShotMeasurement> measurements) {
		for (ParsedShotMeasurement measurement : measurements) {
			if (measurement.isBacksight) {
				continue;
			}
			UnitizedDouble<Angle> azimuth = ParsedField.getValue(measurement.azimuth);
			if (azimuth != null) {
				return azimuth;
			}
		}
		return null;
	}

	public static UnitizedDouble<Angle> getFirstBackAzimuth(Iterable<ParsedShotMeasurement> measurements) {
		for (ParsedShotMeasurement measurement : measurements) {
			if (!measurement.isBacksight) {
				continue;
			}
			UnitizedDouble<Angle> azimuth = ParsedField.getValue(measurement.azimuth);
			if (azimuth != null) {
				return azimuth;
			}
		}
		return null;
	}

	public static UnitizedDouble<Angle> getFirstFrontInclination(Iterable<ParsedShotMeasurement> measurements) {
		for (ParsedShotMeasurement measurement : measurements) {
			if (measurement.isBacksight) {
				continue;
			}
			UnitizedDouble<Angle> inclination = ParsedField.getValue(measurement.inclination);
			if (inclination != null) {
				return inclination;
			}
		}
		return null;
	}

	public static UnitizedDouble<Angle> getFirstBackInclination(Iterable<ParsedShotMeasurement> measurements) {
		for (ParsedShotMeasurement measurement : measurements) {
			if (!measurement.isBacksight) {
				continue;
			}
			UnitizedDouble<Angle> inclination = ParsedField.getValue(measurement.inclination);
			if (inclination != null) {
				return inclination;
			}
		}
		return null;
	}
}
