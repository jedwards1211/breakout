package org.breakout.model;

import static org.andork.util.JavaScript.or;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StationKey implements HasStationKey, Comparable<StationKey> {
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

	public StationKey stationKey() {
		return this;
	}

	private static Pattern trailingNumber = Pattern.compile("\\d+$");

	public static int compareStations(String name0, String name1) {
		Matcher matcher0 = trailingNumber.matcher(name0);
		Matcher matcher1 = trailingNumber.matcher(name1);
		if (matcher0.matches() && matcher1.matches()) {
			String header0 = name0.substring(0, matcher0.start());
			String header1 = name1.substring(0, matcher1.start());
			if (!header0.equals(header1))
				return header0.compareTo(header1);
			return Integer.parseInt(matcher0.group()) - Integer.parseInt(matcher1.group());
		}
		return name0.compareTo(name1);
	}

	public static int compareStations(String cave0, String name0, String cave1, String name1) {
		if (!Objects.equals(cave0, cave1)) {
			if (cave0 == null && cave1 != null)
				return -1;
			if (cave0 != null && cave1 == null)
				return 1;
			return cave0.compareTo(cave1);
		}
		Matcher matcher0 = trailingNumber.matcher(name0);
		Matcher matcher1 = trailingNumber.matcher(name1);
		if (matcher0.find() && matcher1.find()) {
			String header0 = name0.substring(0, matcher0.start());
			String header1 = name1.substring(0, matcher1.start());
			if (!header0.equals(header1))
				return header0.compareTo(header1);
			return Integer.parseInt(matcher0.group()) - Integer.parseInt(matcher1.group());
		}
		return name0.compareTo(name1);
	}

	public static int compare(StationKey a, StationKey b) {
		return compareStations(a.cave, a.station, b.cave, b.station);
	}

	@Override
	public int compareTo(StationKey o) {
		return compareStations(cave, station, o.cave, o.station);
	}
}
