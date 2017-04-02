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

	public float[] fromSplayNormals;
	public float[] toSplayNormals;

	public CalcCrossSection fromCrossSection;
	public CalcCrossSection toCrossSection;
	public float[] fromSplayPoints;
	public float[] toSplayPoints;

	public Date date;

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
