package org.andork.datescraper;

import static org.andork.datescraper.DateField.DAY;
import static org.andork.datescraper.DateField.FULL_YEAR;
import static org.andork.datescraper.DateField.MONTH;
import static org.andork.datescraper.DateField.TWO_DIGIT_YEAR;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralDateFormat implements DateFormat {
	private static final DateField[] DEFAULT_ORDER = { FULL_YEAR, MONTH, DAY };
	private static final String[] EN_US_MONTH_PATTERNS = { "jan\\.?|january", "feb\\.?|february", "mar\\.?|march",
			"apr\\.?|april", "may", "jun[.e]?", "jul[.y]?", "aug\\.?|august", "sept?\\.?|september", "nov\\.?|november",
			"dec\\.?|december" };
	private static final String EN_US_DAY_PATTERN = "([12][0-9]|3[01]|0?[0-9])(st|nd|rd|th)?";

	private DateField[] order = DEFAULT_ORDER;
	private String[] monthPatterns = EN_US_MONTH_PATTERNS;
	private String dayPattern = EN_US_DAY_PATTERN;
	private String separatorPattern = "|[-/\t.]| +";
	private int minYearDigits = 2;
	private int maxYearDigits = 4;
	
	public static final DateFormat[] EN_US_FORMATS = {
		new GeneralDateFormat().order(MONTH, DAY, FULL_YEAR),
		new GeneralDateFormat().order(MONTH, DAY, TWO_DIGIT_YEAR),
		new GeneralDateFormat().order(FULL_YEAR, MONTH, DAY),
		new GeneralDateFormat().order(MONTH, FULL_YEAR),
		new GeneralDateFormat().order(FULL_YEAR, MONTH),
		new GeneralDateFormat().order(DAY, MONTH, FULL_YEAR),
		new ISO8601DateFormat(),
	};

	public DateField[] Order() {
		return order;
	}

	public GeneralDateFormat order(DateField... order) {
		this.order = order;
		return this;
	}

	public String[] monthPatterns() {
		return monthPatterns;
	}

	public GeneralDateFormat monthPatterns(String... monthPatterns) {
		this.monthPatterns = monthPatterns;
		return this;
	}

	public String separatorPattern() {
		return separatorPattern;
	}

	public GeneralDateFormat separatorPattern(String separatorPattern) {
		this.separatorPattern = separatorPattern;
		return this;
	}

	public int minYearDigits() {
		return minYearDigits;
	}

	public GeneralDateFormat minYearDigits(int minYearDigits) {
		this.minYearDigits = minYearDigits;
		return this;
	}

	public int maxYearDigits() {
		return maxYearDigits;
	}

	public GeneralDateFormat maxYearDigits(int maxYearDigits) {
		this.maxYearDigits = maxYearDigits;
		return this;
	}

	@Override
	public String pattern() {
		StringBuilder builder = new StringBuilder();
		for (DateField field : order) {
			if (builder.length() > 0)
				builder.append("(").append(separatorPattern).append(")");
			switch (field) {
			case FULL_YEAR:
				builder.append("(?<!\\d)(\\d{4})(?!\\d)");
				break;
			case TWO_DIGIT_YEAR:
				builder.append("(?<!\\d)(\\d{2})(?!\\d)");
				break;
			case MONTH:
				builder.append("(");
				for (int i = 0; i < monthPatterns.length; i++) {
					if (i > 0)
						builder.append("|");
					builder.append("(");
					if (i < 9)
						builder.append("0?");
					builder.append(i + 1).append("|");
					builder.append(monthPatterns[i]);
					builder.append(")");
				}
				builder.append(")");
				break;
			case DAY:
				builder.append("(?<!\\d)(").append(dayPattern).append(")(?!\\d)");
				if (order.length > 2 && order[0] == MONTH && order[1] == DAY) {
					builder.append(",?");
				}
				break;
			}
		}
		return builder.toString();
	}

	public static boolean isLeapYear(int year) {
		return (year % 4) == 0 && ((year % 100) != 0 || (year % 400) == 0);
	}

	@Override
	public Date parse(MatchGroups groups) {
		String firstSep = null;
		String secondSep = null;
		int year = 0;
		int month = 0;
		int day = 1;
		int offset = 1;
		String monthString = "";
		boolean numericMonth = false;
		for (DateField field : order) {
			switch (field) {
			case FULL_YEAR:
				year = Integer.parseInt(groups.group(offset++));
				break;
			case TWO_DIGIT_YEAR:
				year = Integer.parseInt(groups.group(offset++)) + 1900;
				break;
			case MONTH:
				monthString = groups.group(offset);
				numericMonth = monthString.matches("\\d");
				for (month = 0; month < 12; month++) {
					if (groups.group(offset + month + 1) != null) {
						break;
					}
				}
				offset += 12;
				break;
			case DAY:
				day = Integer.parseInt(groups.group(++offset));
				offset += 2;
				break;
			}
			if (firstSep == null) {
				firstSep = groups.group(offset);
			} else if (secondSep == null) {
				secondSep = groups.group(offset);
			}
			offset++;
		}
		if (!firstSep.equals(secondSep) && (!firstSep.matches("\\s+") || !secondSep.matches("\\s+"))) {
			if (!monthString.endsWith(".") || !firstSep.matches("\\s*|\\.") || !secondSep.matches("\\s*|\\.")) {
				return null;
			}
		}
		if (numericMonth && firstSep.matches("\\s")) {
			return null;
		}
		switch (month) {
		case 1:
			if (day > (isLeapYear(year) ? 29 : 28))
				return null;
			break;
		case 3:
		case 5:
		case 8:
		case 10:
			if (day > 30)
				return null;
			break;
		}
		return new Date(year - 1900, month, day);
	}

	public static void main(String[] args) {
		GeneralDateFormat fmt = new GeneralDateFormat();
		System.out.println(fmt.pattern());
		Matcher m = Pattern.compile(fmt.pattern(), Pattern.CASE_INSENSITIVE).matcher("2304-02-29th");
		m.find();
		System.out.println(m.group());
		for (int i = 1; i <= m.groupCount(); i++) {
			System.out.print(i);
			System.out.print("\t");
			System.out.println(m.group(i));
		}

		fmt.order(new DateField[] { MONTH, DAY, FULL_YEAR });
		System.out.println(fmt.pattern());
		Matcher m2 = Pattern.compile(fmt.pattern(), Pattern.CASE_INSENSITIVE).matcher("Feb 29th 2304");
		m2.find();
		System.out.println(m2.group());
		for (int i = 1; i <= m2.groupCount(); i++) {
			System.out.print(i);
			System.out.print("\t");
			System.out.println(m2.group(i));
		}
		System.out.println(fmt.parse(group -> m2.group(group)));
		
	}
}
