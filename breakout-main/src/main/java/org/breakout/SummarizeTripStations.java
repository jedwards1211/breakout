package org.breakout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.breakout.model.calc.CalcShot;
import org.breakout.model.calc.CalcStation;
import org.breakout.model.calc.CalcTrip;

public class SummarizeTripStations {
	private SummarizeTripStations() {

	}

	public static char charClass(char c) {
		if (Character.isDigit(c))
			return '0';
		if (Character.isLetter(c))
			return 'a';
		return c;
	}

	public static String[] splitStationName(String stationName) {
		if (stationName == null || stationName.isEmpty()) {
			throw new IllegalArgumentException("stationName must not be null or empty");
		}
		int i = stationName.length();
		while (i > 0 && !Character.isDigit(stationName.charAt(i - 1)) && !Character.isLetter(stationName.charAt(i - 1)))
			i--;
		String suffix = stationName.substring(i--);

		if (i <= 0)
			return new String[] { "", stationName, "" };

		char numberClass = charClass(stationName.charAt(i));
		while (i > 0 && charClass(stationName.charAt(i - 1)) == numberClass)
			i--;

		String stationNumber = stationName.substring(i, stationName.length() - suffix.length());
		String prefix = stationName.substring(0, i);

		return new String[] { prefix, stationNumber, suffix };
	}

	private static boolean areStationNumbersConsecutive(Object a, Object b) {
		if (a instanceof Integer)
			return b instanceof Integer && ((int) a) + 1 == (int) b;
		String sa = String.valueOf(a);
		String sb = String.valueOf(b);
		return sa.length() == 1 && sb.length() == 1 && (sa.charAt(0) + 1) == sb.charAt(0);
	}

	private static class Designation {
		public final String prefix;
		public final String suffix;

		public Designation(String prefix, String suffix) {
			super();
			this.prefix = prefix == null ? "" : prefix;
			this.suffix = suffix == null ? "" : suffix;
		}

		@Override
		public int hashCode() {
			return Objects.hash(prefix, suffix);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Designation other = (Designation) obj;
			return Objects.equals(prefix, other.prefix) && Objects.equals(suffix, other.suffix);
		}

		@Override
		public String toString() {
			return "Designation [prefix=" + prefix + ", suffix=" + suffix + "]";
		}

		public String summarize(Iterable<String> stationNumbers) {
			Iterator<String> i = stationNumbers.iterator();
			if (!i.hasNext())
				return "";
			String first = i.next();
			List<?> numbers;
			if (first.matches("^\\d+$")) {
				List<Integer> ints = new ArrayList<>();
				ints.add(Integer.parseUnsignedInt(first));
				while (i.hasNext())
					ints.add(Integer.parseUnsignedInt(i.next()));
				Collections.sort(ints);
				numbers = ints;
			}
			else {
				List<String> strs = new ArrayList<>();
				strs.add(first);
				while (i.hasNext())
					strs.add(i.next());
				Collections.sort(strs);
				numbers = strs;
			}

			StringBuilder builder = new StringBuilder();
			Object from;
			Object to;

			Iterator<?> n = numbers.iterator();
			Object n0 = n.next();

			from = to = n0;
			while (true) {
				Object next = n.hasNext() ? n.next() : null;
				if (next != null && areStationNumbersConsecutive(to, next)) {
					to = next;
				}
				else {
					if (builder.length() == 0) {
						builder.append(prefix);
					}
					else {
						builder.append(',');
					}
					builder.append(from).append(suffix);
					if (!Objects.equals(from, to))
						builder.append('-').append(to).append(suffix);

					from = to = next;
					if (!n.hasNext())
						break;
				}
			}

			return builder.toString();
		}
	}

	private static void forEachOriginatedStation(CalcTrip trip, Consumer<CalcStation> iteratee) {
		for (CalcShot shot : trip.shots.values()) {
			if (shot.fromStation.originatingShot() == shot) {
				iteratee.accept(shot.fromStation);
			}
			if (shot.toStation.originatingShot() == shot) {
				iteratee.accept(shot.toStation);
			}
		}
	}

	public static Iterable<String> stationsOriginatedIn(CalcTrip trip) {
		List<String> result = new ArrayList<>();
		forEachOriginatedStation(trip, station -> result.add(station.name));
		return result;
	}

	public static String summarizeTripStations(CalcTrip trip) {
		return summarizeTripStations(stationsOriginatedIn(trip));
	}

	public static String summarizeTripStations(Iterable<String> stations) {
		final Map<Designation, Set<String>> designations = new LinkedHashMap<>();

		for (String station : stations) {
			String[] parts = splitStationName(station);
			Designation designation = new Designation(parts[0], parts[2]);
			Set<String> stationsInDesignation = designations.get(designation);
			if (stationsInDesignation == null) {
				stationsInDesignation = new HashSet<>();
				designations.put(designation, stationsInDesignation);
			}
			stationsInDesignation.add(parts[1]);
		}

		StringBuilder builder = new StringBuilder();

		for (Map.Entry<Designation, Set<String>> entry : designations.entrySet()) {
			if (builder.length() > 0)
				builder.append(' ');
			builder.append(entry.getKey().summarize(entry.getValue()));
		}

		return builder.toString();
	}
}
