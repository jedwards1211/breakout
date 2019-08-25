package org.breakout.leadimport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;
import org.breakout.model.raw.MutableSurveyLead;
import org.breakout.model.raw.SurveyLead;

public class LeadListParser {
	public static interface Context {
		public Unit<Length> defaultLengthUnit();

		public boolean widthComesFirst();

		public boolean isStationName(String s);
	}

	public static interface Table {
		public int getRowCount();

		public int getColumnCount();

		public String getValueAt(int row, int column);
	}

	public static class ListTable implements Table {
		List<List<String>> rows;
		int columnCount;

		public ListTable(List<List<String>> rows) {
			this.rows = rows;
			columnCount = 0;
			for (List<String> row : rows) {
				if (row.size() > columnCount)
					columnCount = row.size();
			}
		}

		@Override
		public int getRowCount() {
			return rows.size();
		}

		@Override
		public int getColumnCount() {
			return columnCount;
		}

		@Override
		public String getValueAt(int row, int column) {
			List<String> cols = rows.get(row);
			return column < cols.size() ? cols.get(column) : null;
		}
	}

	public static boolean isStationlike(String s) {
		s = s.trim();
		int numDigits = 0;
		int numLetters = 0;
		int numPunct = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isWhitespace(c))
				return false;
			if (Character.isDigit(c))
				numDigits++;
			else if (Character.isAlphabetic(c))
				numLetters++;
			else
				numPunct++;
		}
		return numDigits > 0 && (numLetters > 0 || numPunct > 0);
	}

	private static final Pattern nonwhitespacePattern = Pattern.compile("\\S+");
	private static final Matcher nonwhitespaceMatcher = nonwhitespacePattern.matcher("");
	private static final Pattern wordPattern = Pattern.compile("\\b\\p{IsAlphabetic}+\\b");
	private static final Matcher wordMatcher = wordPattern.matcher("");
	private static final Pattern gradePattern = Pattern.compile("[A-F][-+]?", Pattern.CASE_INSENSITIVE);
	private static final Matcher gradeMatcher = gradePattern.matcher("");
	private static final Pattern dimensionsPattern =
		Pattern
			.compile(
				"(\\d+(\\.\\d*)?|\\.\\d+|\\?)\\s*(['\"]|c?m|yd|ft?|in)?\\s*([hw])?\\s*(?:x|by)\\s*(\\d+(\\.\\d*)?|\\.\\d+|\\?)\\s*(['\"]|c?m|yd|ft|in)?([hw])?|(\\d+(\\.\\d*)?|\\.\\d+|\\?)\\s*(['\"]|c?m|yd|ft?|in)?\\s*([hw])",
				Pattern.CASE_INSENSITIVE);
	private static final Matcher dimensionsMatcher = dimensionsPattern.matcher("");
	private static final Pattern booleanPattern =
		Pattern.compile("y(es)?|no?|t(rue)?|f(alse)?|x", Pattern.CASE_INSENSITIVE);
	private static final Matcher booleanMatcher = booleanPattern.matcher("");
	private static final Pattern donePattern =
		Pattern.compile("done|checked|finished|pushed", Pattern.CASE_INSENSITIVE);
	private static final Matcher doneMatcher = donePattern.matcher("");
	private static final Pattern badSyntaxPattern = Pattern.compile("^([a-z]+)-([^-\\s]+)", Pattern.CASE_INSENSITIVE);
	private static final Matcher badSyntaxMatcher = badSyntaxPattern.matcher("");

	private static int count(Matcher matcher) {
		int count = 0;
		while (matcher.find())
			count++;
		return count;
	}

	private static boolean parseBoolean(String s) {
		if (s == null)
			return false;
		s = s.trim();
		if (s.isEmpty())
			return false;
		switch (s.charAt(0)) {
		case 'y':
		case 'Y':
		case 't':
		case 'T':
		case 'x':
		case 'X':
			return true;
		}
		return false;
	}

	public static class Dimensions {
		public Double width;
		public Unit<Length> widthUnit;
		public Double height;
		public Unit<Length> heightUnit;

		public void applyTo(MutableSurveyLead lead) {
			if (width != null) {
				lead.setWidth(new UnitizedDouble<>(width, widthUnit));
			}
			if (height != null) {
				lead.setHeight(new UnitizedDouble<>(height, heightUnit));
			}
		}
	}

	public static List<Dimensions> parseDimensions(String s, Context context) {
		if (s == null)
			return Collections.emptyList();
		dimensionsMatcher.reset(s);
		if (!dimensionsMatcher.find())
			return Collections.emptyList();
		List<Dimensions> result = new ArrayList<>();
		do {
			Dimensions dimensions = new Dimensions();
			if (dimensionsMatcher.group(9) != null) {
				String dim = dimensionsMatcher.group(9);
				Unit<Length> unit = parseLengthUnit(dimensionsMatcher.group(11));
				if (unit == null)
					unit = context.defaultLengthUnit();
				Double value = dim.startsWith("?") ? null : Double.parseDouble(dim);
				if ("w".equals(dimensionsMatcher.group(12))) {
					dimensions.width = value;
					dimensions.widthUnit = unit;
				}
				else {
					dimensions.height = value;
					dimensions.heightUnit = unit;
				}
			}
			else {
				String dim1 = dimensionsMatcher.group(1);
				String dim2 = dimensionsMatcher.group(5);
				Unit<Length> unit1 = parseLengthUnit(dimensionsMatcher.group(3));
				Unit<Length> unit2 = parseLengthUnit(dimensionsMatcher.group(7));
				if (unit1 == null)
					unit1 = context.defaultLengthUnit();
				if (unit2 == null)
					unit2 = context.defaultLengthUnit();
				Double value1 = dim1.startsWith("?") ? null : Double.parseDouble(dim1);
				Double value2 = dim2.startsWith("?") ? null : Double.parseDouble(dim2);
				if ("w".equals(dimensionsMatcher.group(4)) || "h".equals(dimensionsMatcher.group(8))) {
					dimensions.width = value1;
					dimensions.widthUnit = unit1;
					dimensions.height = value2;
					dimensions.heightUnit = unit2;
				}
				else if ("h".equals(dimensionsMatcher.group(4)) || "w".equals(dimensionsMatcher.group(8))) {
					dimensions.width = value2;
					dimensions.widthUnit = unit2;
					dimensions.height = value1;
					dimensions.heightUnit = unit1;
				}
				else {
					dimensions.width = context.widthComesFirst() ? value1 : value2;
					dimensions.widthUnit = context.widthComesFirst() ? unit1 : unit2;
					dimensions.height = context.widthComesFirst() ? value2 : value1;
					dimensions.heightUnit = context.widthComesFirst() ? unit2 : unit1;
				}
			}
			result.add(dimensions);
		} while (dimensionsMatcher.find());
		return result;
	}

	public static Unit<Length> parseLengthUnit(String unit) {
		if (unit == null)
			return null;
		unit = unit.toLowerCase();
		if (unit.matches("'|ft?|foot|feet")) {
			return Length.feet;
		}
		if (unit.matches("\"|i(n(ch(es)?)?)?")) {
			return Length.inches;
		}
		if (unit.matches("m(eters?)?")) {
			return Length.meters;
		}
		if (unit.matches("cm|centimeters?")) {
			return Length.centimeters;
		}
		return null;
	}

	public static class ColumnStats {
		int numStations = 0;
		int numStationlike = 0;
		int totalChars = 0;
		int numWords = 0;
		int numEmpty = 0;
		int numGrades = 0;
		int numDimensions = 0;
		int numBooleans = 0;

		public void update(String s, Context context) {
			if (s == null) {
				numEmpty++;
				return;
			}
			s = s.trim();
			if (s.isEmpty()) {
				numEmpty++;
				return;
			}
			totalChars += s.length();
			numWords += count(wordMatcher.reset(s));
			if (context.isStationName(s))
				numStations++;
			if (gradeMatcher.reset(s).matches())
				numGrades++;
			else if (dimensionsMatcher.reset(s).matches())
				numDimensions++;
			else if (booleanMatcher.reset(s).matches())
				numBooleans++;
			else if (isStationlike(s))
				numStationlike++;
		}

		public static ColumnStats[] collect(Table table, Context context) {
			ColumnStats[] result = new ColumnStats[table.getColumnCount()];
			for (int i = 0; i < result.length; i++) {
				result[i] = new ColumnStats();
			}
			for (int row = 0; row < table.getRowCount(); row++) {
				for (int column = 0; column < table.getColumnCount(); column++) {
					result[column].update(table.getValueAt(row, column), context);
				}
			}
			return result;
		}
	}

	public static class ColumnIndices {
		int station = -1;
		int description = -1;
		int width = -1;
		int height = -1;
		int dimensions = -1;
		int grade = -1;
		int done = -1;

		public void determine(Table table, ColumnStats[] stats) {
			for (int column = 0; column < stats.length; column++) {
				if (stats[column].numStations > 0
					&& (station < 0 || stats[column].numStations > stats[station].numStations)) {
					station = column;
				}
				if (description < 0 || stats[column].numWords > stats[description].numWords) {
					description = column;
				}
				if (stats[column].numGrades > 0 && (grade < 0 || stats[column].numGrades > stats[grade].numGrades)) {
					grade = column;
				}
				if (stats[column].numDimensions > 0
					&& (dimensions < 0 || stats[column].numDimensions > stats[dimensions].numDimensions)) {
					dimensions = column;
				}
				if (stats[column].numBooleans > 0) {
					for (int row = 0; row < table.getRowCount(); row++) {
						String text = table.getValueAt(row, column);
						if (text == null)
							continue;
						if (doneMatcher.reset(text).find()) {
							done = column;
						}
					}
				}
			}
			if (description == station) {
				description = -1;
			}
			if (grade == station) {
				grade = -1;
			}
			if (dimensions == station) {
				dimensions = -1;
			}
		}

		@Override
		public String toString() {
			return "ColumnIndices [station="
				+ station
				+ ", description="
				+ description
				+ ", width="
				+ width
				+ ", height="
				+ height
				+ ", dimensions="
				+ dimensions
				+ ", grade="
				+ grade
				+ "]";
		}
	}

	public static List<SurveyLead> parse(Table table, Context context) {
		ColumnStats[] stats = ColumnStats.collect(table, context);
		ColumnIndices indices = new ColumnIndices();
		indices.determine(table, stats);

		List<SurveyLead> leads = new ArrayList<>();

		if (indices.station < 0)
			return leads;

		for (int row = 1; row < table.getRowCount(); row++) {
			String station = table.getValueAt(row, indices.station);
			if (station == null) {
				continue;
			}
			station = station.trim();
			if (station.isEmpty()) {
				continue;
			}
			if (!context.isStationName(station)) {
				if (badSyntaxMatcher.reset(station).find()) {
					station = badSyntaxMatcher.group(1) + badSyntaxMatcher.group(2);
					if (!context.isStationName(station)) {
						continue;
					}
				}
				if (!isStationlike(station)) {
					continue;
				}
			}
			MutableSurveyLead lead = new MutableSurveyLead().setStation(station);
			if (indices.description >= 0) {
				lead.setDescription(table.getValueAt(row, indices.description));
			}
			if (indices.dimensions >= 0) {
				String dimensions = table.getValueAt(row, indices.dimensions);
				if (dimensions != null) {
					List<Dimensions> parsed = parseDimensions(dimensions, context);
					if (!parsed.isEmpty()) {
						parsed.get(0).applyTo(lead);
					}
				}
			}
			if (indices.done >= 0) {
				Boolean done = parseBoolean(table.getValueAt(row, indices.done));
				lead.setDone(done);
			}
			leads.add(lead.toImmutable());
		}

		return leads;
	}
}
