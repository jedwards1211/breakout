package org.breakout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StationSet {
	public StationSet(String str) {
		add(str);
	}
	
	public static abstract class Designation<B extends Comparable<B>> {
		public final String designation;
		List<B[]> ranges = new ArrayList<>();
		
		public Designation(String designation) {
			super();
			this.designation = designation;
		}
		
		public void addRange(String... rangeStr) {
			B[] range = allocateRange(rangeStr.length);
			for (int i = 0; i < rangeStr.length; i++) {
				range[i] = parseSuffix(rangeStr[i]);
			}
			ranges.add(range);
		}
	
		public boolean contains(String station) {
			if (!station.startsWith(designation)) return false;
			return containsStringSuffix(station.substring(designation.length()));
		}
		
		abstract B parseSuffix(String suffix);
		abstract B[] allocateRange(int size);
		
		public boolean containsStringSuffix(String suffix) {
			B parsed;
			try {
				parsed = parseSuffix(suffix);
			} catch (Exception ex) {
				return false;
			}
			return containsSuffix(parsed);
		}
		
		public boolean containsSuffix(B suffix) {
			if (suffix == null) {
				for (B[] range : ranges) {
					if (range.length == 0) return true;
				}
				return false;
			}
			for (B[] range : ranges) {
				if (range.length == 0) return true;
				B lowerBound = range[0];
				if (suffix.compareTo(lowerBound) < 0) continue;
				if (range.length == 1) return true;
				B upperBound = range[1];
				if (suffix.compareTo(upperBound) <= 0) return true;
			}
			return false;
		}
		
		public String toString() {
			if (ranges.size() == 0) {
				return designation;
			}
			StringBuilder builder = new StringBuilder();
			for (B[] range : ranges) {
				if (builder.length() > 0) builder.append(' ');
				builder.append(designation);
				if (range.length > 0) {
					builder.append(range[0]);
					if (range.length > 1) {
						if (!Objects.equals(range[0], range[1])) {
							builder.append('-').append(range[1]);
						}
					}
				}
			}
			return builder.toString();
		}
	}
	
	public static class NumericDesignation extends Designation<Integer> {
		public NumericDesignation(String designation) {
			super(designation);
		}

		@Override
		Integer parseSuffix(String suffix) {
			return suffix.isEmpty() ? null : Integer.valueOf(suffix);
		}

		@Override
		Integer[] allocateRange(int size) {
			return new Integer[size];
		}
	}
	
	public static class AlphabeticDesignation extends Designation<String> {
		public AlphabeticDesignation(String designation) {
			super(designation);
		}

		@Override
		String parseSuffix(String suffix) {
			return suffix;
		}

		@Override
		String[] allocateRange(int size) {
			return new String[size];
		}
	}
	
	private final Map<String, Designation<?>> designations = new HashMap<>();
	private String stringRep = null;
	
	public static String getSuffix(String station) {
		if (station.isEmpty()) return "";
		int i = station.length() - 1;
		if (Character.isDigit(station.charAt(i))) {
			while (i > 0 && Character.isDigit(station.charAt(i - 1))) i--;
		} else {
			while (i > 0 && !Character.isDigit(station.charAt(i - 1))) i--;
		}
		return i == 0 ? "" : station.substring(i);
	}
	
	private static final Pattern designationPattern = Pattern.compile(
		"(?<=^|\\s|,)([^-,\\s]+)(-(\\d+|[^-,\\s]+))?(?=$|\\s|,)"
	);
	
	private static final Pattern numberPattern = Pattern.compile("^\\d+$");
	
	public void add(String str) {
		stringRep = null;

		Matcher m = designationPattern.matcher(str);
		while (m.find()) {
			String group1 = m.group(1);
			String lowerBoundStr = getSuffix(group1);
			String upperBoundStr = m.group(3);
			String designationStr = group1.substring(0, group1.length() - lowerBoundStr.length());
			if (lowerBoundStr.isEmpty()) lowerBoundStr = null;
			if (lowerBoundStr != null && upperBoundStr == null) {
				upperBoundStr = lowerBoundStr;
			}
			Designation<?> designation = designations.get(designationStr);
			boolean isNumeric = designation != null
				? designation instanceof NumericDesignation
				: lowerBoundStr == null || numberPattern.matcher(lowerBoundStr).find();
			
			if (lowerBoundStr != null &&
				numberPattern.matcher(lowerBoundStr).find() != isNumeric) {
				continue;
			}
			if (upperBoundStr != null &&
				numberPattern.matcher(upperBoundStr).find() != isNumeric) {
				continue;
			}
			if (designation == null) {
				if (isNumeric) {
					designation = new NumericDesignation(designationStr);
				} else {
					designation = new AlphabeticDesignation(designationStr);
				}
				designations.put(designationStr, designation);
			}
			if (lowerBoundStr == null) {
				designation.addRange();
			} else if (upperBoundStr == null) {
				designation.addRange(lowerBoundStr);
			} else {
				designation.addRange(lowerBoundStr, upperBoundStr);
			}
		}
	}
	
	public boolean contains(String station) {
		String suffix = getSuffix(station);
		String designationStr = station.substring(0, station.length() - suffix.length());
		Designation<?> designation = designations.get(designationStr);
		return designation != null && designation.containsStringSuffix(suffix);
	}
	
	public String toString() {
		if (stringRep != null) return stringRep;
		StringBuilder builder = new StringBuilder();
		List<String> keys = new ArrayList<>(designations.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			if (builder.length() > 0) builder.append(' ');
			builder.append(designations.get(key));
		}
		return stringRep = builder.toString();
	}
}
