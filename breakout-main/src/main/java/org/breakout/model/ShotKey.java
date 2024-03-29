package org.breakout.model;

import static org.andork.util.JavaScript.or;

import java.util.Objects;

import org.breakout.model.raw.SurveyRow;

public class ShotKey {
	public final String fromCave;
	public final String fromStation;
	public final String toCave;
	public final String toStation;
	private final int hashCode;

	public ShotKey(SurveyRow SurveyRow) {
		this(SurveyRow.getFromCave(), SurveyRow.getFromStation(), SurveyRow.getToCave(), SurveyRow.getToStation());
	}

	public ShotKey(String fromCave, String fromStation, String toCave, String toStation) {
		super();
		this.fromCave = fromCave = or(fromCave, "");
		this.fromStation = Objects.requireNonNull(fromStation);
		this.toCave = toCave = or(toCave, "");
		this.toStation = Objects.requireNonNull(toStation);

		// alphabetize so that reverse of this shot has the same hash code
		if (fromCave.compareTo(toCave) > 0 || fromStation.compareTo(toStation) > 0) {
			// swap
			fromCave = this.toCave;
			fromStation = this.toStation;
			toCave = this.fromCave;
			toStation = this.fromStation;
		}
		final int prime = 31;
		int hashCode = 1;
		hashCode = prime * hashCode + fromCave.hashCode();
		hashCode = prime * hashCode + fromStation.hashCode();
		hashCode = prime * hashCode + toCave.hashCode();
		hashCode = prime * hashCode + toStation.hashCode();
		this.hashCode = hashCode;
	}

	public ShotKey(StationKey fromKey, StationKey toKey) {
		this(fromKey.cave, fromKey.station, toKey.cave, toKey.station);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		ShotKey other = (ShotKey) obj;

		if (fromCave.equals(other.fromCave) && fromStation.equals(other.fromStation)) {
			return toCave.equals(other.toCave) && toStation.equals(other.toStation);
		}
		// reverse is equal too
		else if (fromCave.equals(other.toCave) && fromStation.equals(other.toStation)) {
			return toCave.equals(other.fromCave) && toStation.equals(other.fromStation);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ShotKey [fromCave=").append(fromCave).append(", fromStation=").append(fromStation)
				.append(", toCave=").append(toCave).append(", toStation=").append(toStation).append(", hashCode=")
				.append(hashCode).append("]");
		return builder.toString();
	}
	
	public StationKey fromKey() {
		return new StationKey(fromCave, fromStation);
	}
	
	public StationKey toKey() {
		return new StationKey(toCave, toStation);
	}
}
