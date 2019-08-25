package org.breakout.model.calc;

import java.util.Date;

import org.breakout.model.ShotKey;
import org.breakout.model.StationKey;

public class CalcShot {
	public CalcTrip trip;

	public CalcShot prev;
	public CalcShot next;

	public CalcStation fromStation;
	public CalcStation toStation;

	public double distance;
	public boolean excludeDistance;
	public double azimuth;
	public double inclination;

	public CalcCrossSection fromCrossSection;
	public CalcCrossSection toCrossSection;

	public float[] normals;
	public float[] vertices;
	/**
	 * Polarity is my term for a number indicating which side of a shot a vertex is
	 * on. There's one number in here corresponding to each vertex. It should be 0
	 * if the vertex is at or beyond {@link #fromStation}, 1 if the vertex is at or
	 * beyond {@link #toStation}, or something between 0 and 1 if the vertex is
	 * somewhere between the two (along the shot vector).
	 */
	public float[] polarities;
	/**
	 * Every 3 elements are the indices of triangle endpoints in {@link #vertices}
	 */
	public int[] indices;

	public Date date;

	private int flags = 0;
	private static final int EXCLUDE_DISTANCE = 0;
	private static final int EXCLUDE_FROM_PLOTTING = 1;
	private static final int HAS_SURVEY_NOTES = 2;

	private void setFlag(int flag, boolean value) {
		if (value)
			flags |= flag;
		else
			flags &= ~flag;
	}

	private boolean getFlag(int flag) {
		return (flags & flag) != 0;
	}

	public boolean isExcludeDistance() {
		return getFlag(EXCLUDE_DISTANCE);
	}

	public boolean isExcludeFromPlotting() {
		return getFlag(EXCLUDE_FROM_PLOTTING);
	}

	public boolean hasSurveyNotes() {
		return getFlag(HAS_SURVEY_NOTES);
	}

	public void setExcludeDistance(boolean value) {
		setFlag(EXCLUDE_DISTANCE, value);
	}

	public void setExcludeFromPlotting(boolean value) {
		setFlag(EXCLUDE_FROM_PLOTTING, value);
	}

	public void setHasSurveyNotes(boolean value) {
		setFlag(HAS_SURVEY_NOTES, value);
	}

	public CalcShot() {

	}

	public CalcShot(CalcStation from, CalcStation to) {
		fromStation = from;
		toStation = to;
		if (from.shots != null)
			from.shots.put(toKey(), this);
		if (to.shots != null)
			to.shots.put(fromKey(), this);
	}

	/**
	 * Interpolates between a value at the from station and to station for a
	 * vertex's polarity (position between the stations).
	 *
	 * @param fromValue   the value at {@link #fromStation}
	 * @param toValue     the value at {@link #toStation}
	 * @param vertexIndex the index of the vertex (in {@link #vertices})
	 * @return The interpolated value. For a vertex at the from station (polarity 0)
	 *         it will be {@code fromValue}. For a vertex at the from station
	 *         (polarity 1) it will be {@code toValue}. For a vertex halfway between
	 *         (polarity 0.5) it will be the average.
	 */
	public float interpolateParamAtVertex(float fromValue, float toValue, int vertexIndex) {
		return fromValue * (1 - polarities[vertexIndex]) + toValue * polarities[vertexIndex];
	}

	public CalcCrossSection getCrossSectionAt(CalcStation station) {
		if (station == null) {
			return null;
		}
		if (station == fromStation) {
			return fromCrossSection;
		}
		if (station == toStation) {
			return toCrossSection;
		}
		return null;
	}

	public void setCrossSectionAt(CalcStation station, CalcCrossSection crossSection) {
		if (station == null) {
			throw new IllegalArgumentException("station must be non-null");
		}
		if (station == fromStation) {
			fromCrossSection = crossSection;
		}
		else if (station == toStation) {
			toCrossSection = crossSection;
		}
		else {
			throw new IllegalArgumentException("station must be fromStation or toStation");
		}
	}

	public StationKey fromKey() {
		return fromStation != null ? fromStation.key() : null;
	}

	public StationKey toKey() {
		return toStation != null ? toStation.key() : null;
	}

	public ShotKey key() {
		return fromStation != null && toStation != null ? new ShotKey(fromStation.key(), toStation.key()) : null;
	}

	public CalcStation otherStation(CalcStation station) {
		if (station == null) {
			throw new IllegalArgumentException("station must be non-null");
		}
		if (station == fromStation) {
			return toStation;
		}
		if (station == toStation) {
			return fromStation;
		}
		throw new IllegalArgumentException("station must be fromStation or toStation");
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder
			.append("CalcShot [fromStation=")
			.append(fromStation)
			.append(", toStation=")
			.append(toStation)
			.append("]");
		return builder.toString();
	}
}
