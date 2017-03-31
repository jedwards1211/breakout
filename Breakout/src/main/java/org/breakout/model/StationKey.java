package org.breakout.model;

import static org.andork.util.JavaScript.or;

import java.util.Objects;

public class StationKey {
	public final String cave;
	public final String station;

	public StationKey(String cave, String station) {
		this.cave = or(cave, "");
		this.station = Objects.requireNonNull(station);
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
		StationKey other = (StationKey) obj;
		return cave.equals(other.cave) && station.equals(other.station);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cave.hashCode();
		result = prime * result + station.hashCode();
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StationKey [cave=").append(cave).append(", station=").append(station).append("]");
		return builder.toString();
	}
}
