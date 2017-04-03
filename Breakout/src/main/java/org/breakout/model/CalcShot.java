package org.breakout.model;

import java.util.Date;

public class CalcShot {
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
	 * Polarity is my term for a number indicating which side of a shot a vertex
	 * is on. There's one number in here corresponding to each vertex. It should
	 * be 0 if the vertex is at or beyond {@link #fromStation}, 1 if the vertex
	 * is at or beyond {@link #toStation}, or something between 0 and 1 if the
	 * vertex is somewhere between the two (along the shot vector).
	 */
	public float[] polarities;
	/**
	 * Every 3 elements are the indices of triangle endpoints in
	 * {@link #vertices}
	 */
	public int[] indices;

	public Date date;

	/**
	 * Interpolates between a value at the from station and to station for a
	 * vertex's polarity (position between the stations).
	 * 
	 * @param fromValue
	 *            the value at {@link #fromStation}
	 * @param toValue
	 *            the value at {@link #toStation}
	 * @param vertexIndex
	 *            the index of the vertex (in {@link #vertices})
	 * @return The interpolated value. For a vertex at the from station
	 *         (polarity 0) it will be {@code fromValue}. For a vertex at the
	 *         from station (polarity 1) it will be {@code toValue}. For a
	 *         vertex halfway between (polarity 0.5) it will be the average.
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

	public CalcCrossSection getCrossSectionAt(StationKey key) {
		if (key == null) {
			return null;
		}
		if (fromStation != null && key.equals(fromStation.key())) {
			return fromCrossSection;
		}
		if (toStation != null && key.equals(toStation.key())) {
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
		} else if (station == toStation) {
			toCrossSection = crossSection;
		}
		throw new IllegalArgumentException("station must be fromStation or toStation");
	}

	public void setCrossSectionAt(StationKey key, CalcCrossSection crossSection) {
		if (key == null) {
			throw new IllegalArgumentException("key must be non-null");
		}
		if (fromStation != null && key.equals(fromStation.key())) {
			fromCrossSection = crossSection;
		}
		if (toStation != null && key.equals(toStation.key())) {
			toCrossSection = crossSection;
		}
		throw new IllegalArgumentException("key must match fromStation or toStation");
	}

	public StationKey fromKey() {
		return fromStation != null ? fromStation.key() : null;
	}

	public StationKey toKey() {
		return toStation != null ? toStation.key() : null;
	}

	public ShotKey key() {
		return fromStation != null && toStation != null
				? new ShotKey(fromStation.key(), toStation.key())
				: null;
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
		builder.append("CalcShot [fromStation=").append(fromStation).append(", toStation=").append(toStation)
				.append("]");
		return builder.toString();
	}
}
