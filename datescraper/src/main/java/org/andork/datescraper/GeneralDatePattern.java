package org.andork.datescraper;

import static org.andork.datescraper.DateField.DAY;
import static org.andork.datescraper.DateField.FULL_YEAR;
import static org.andork.datescraper.DateField.MONTH;
import static org.andork.datescraper.DateField.TWO_DIGIT_YEAR;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.scrape.ScrapeMatcher;

public class GeneralDatePattern implements DatePattern {
	private static final DateField[] DEFAULT_ORDER = { FULL_YEAR, MONTH, DAY };
	private static final String[] EN_US_MONTH_PATTERNS =
		{
			"jan\\.?|january",
			"febr?\\.?|february",
			"mar\\.?|march",
			"apr\\.?|april",
			"may",
			"jun[.e]?",
			"jul[.y]?",
			"aug\\.?|august",
			"sept?\\.?|september",
			"nov\\.?|november",
			"dec\\.?|december" };
	private static final String EN_US_DAY_PATTERN = "([12][0-9]|3[01]|0?[0-9])(st|nd|rd|th)?";

	private DateField[] order = DEFAULT_ORDER;
	private String[] monthPatterns = EN_US_MONTH_PATTERNS;
	private String dayPattern = EN_US_DAY_PATTERN;
	private String separatorPattern = "|[-/\t.]| +";
	private int minYearDigits = 2;
	private int maxYearDigits = 4;
	private int currentYear = Calendar.getInstance().get(Calendar.YEAR);

	private Pattern pattern = null;

	public static final DatePattern[] EN_US_PATTERNS =
		{
			new GeneralDatePattern().order(MONTH, DAY, FULL_YEAR),
			new GeneralDatePattern().order(MONTH, DAY, TWO_DIGIT_YEAR),
			new GeneralDatePattern().order(FULL_YEAR, MONTH, DAY),
			new GeneralDatePattern().order(MONTH, FULL_YEAR),
			new GeneralDatePattern().order(FULL_YEAR, MONTH),
			new GeneralDatePattern().order(DAY, MONTH, FULL_YEAR),
			new GeneralDatePattern().order(TWO_DIGIT_YEAR, MONTH, DAY),
			new ISO8601DatePattern() };

	public DateField[] Order() {
		return order;
	}

	public GeneralDatePattern order(DateField... order) {
		pattern = null;
		this.order = order;
		return this;
	}

	public String[] monthPatterns() {
		return monthPatterns;
	}

	public GeneralDatePattern monthPatterns(String... monthPatterns) {
		pattern = null;
		this.monthPatterns = monthPatterns;
		return this;
	}

	public String separatorPattern() {
		return separatorPattern;
	}

	public GeneralDatePattern separatorPattern(String separatorPattern) {
		pattern = null;
		this.separatorPattern = separatorPattern;
		return this;
	}

	public int minYearDigits() {
		return minYearDigits;
	}

	public GeneralDatePattern minYearDigits(int minYearDigits) {
		pattern = null;
		this.minYearDigits = minYearDigits;
		return this;
	}

	public int maxYearDigits() {
		return maxYearDigits;
	}

	public GeneralDatePattern maxYearDigits(int maxYearDigits) {
		pattern = null;
		this.maxYearDigits = maxYearDigits;
		return this;
	}

	public static boolean isLeapYear(int year) {
		return (year % 4) == 0 && ((year % 100) != 0 || (year % 400) == 0);
	}

	public static void main(String[] args) {
		GeneralDatePattern pattern = new GeneralDatePattern();
		System.out.println(pattern.pattern());
		ScrapeMatcher<Date> m = pattern.matcher("2304-02-29th");
		m.find();
		System.out.println(m.match());

		pattern.order(new DateField[] { MONTH, DAY, FULL_YEAR });
		System.out.println(pattern.pattern());
		ScrapeMatcher<Date> m2 = pattern.matcher("Feb 29th 2304");
		m2.find();
		System.out.println(m2.match());

		pattern.order(new DateField[] { DAY, MONTH, TWO_DIGIT_YEAR });
		System.out.println(pattern.pattern());
		ScrapeMatcher<Date> m3 = pattern.matcher("5 mar. 14 2018");
		m3.find();
		System.out.println(m3.match());
	}

	private Pattern pattern() {
		if (pattern != null)
			return pattern;
		StringBuilder builder = new StringBuilder();
		for (DateField field : order) {
			if (builder.length() > 0)
				builder.append("(").append(separatorPattern).append(")");
			switch (field) {
			case FULL_YEAR:
				builder.append("(?<!\\d)(\\d{4})(?!\\d|st|nd|rd|th)");
				break;
			case TWO_DIGIT_YEAR:
				builder.append("(?<!\\d)(\\d{2})(?!\\d|st|nd|rd|th)");
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
				builder.append("(?!st|nd|rd|th))");
				break;
			case DAY:
				builder.append("(?<!\\d)(").append(dayPattern).append(")(?!\\d)");
				if (order.length > 2 && order[0] == MONTH && order[1] == DAY) {
					builder.append(",?");
				}
				break;
			}
		}
		return pattern = Pattern.compile(builder.toString(), Pattern.CASE_INSENSITIVE);
	}

	@Override
	public DateMatcher matcher(CharSequence input) {
		Matcher matcher = pattern().matcher(input);
		matcher.useTransparentBounds(true);
		return new AbstractDateMatcher(this, matcher) {
			@Override
			protected Date parseDate() {
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
						year = Integer.parseInt(matcher.group(offset++));
						break;
					case TWO_DIGIT_YEAR:
						int part = Integer.parseInt(matcher.group(offset++));
						year = currentYear - (currentYear % 100) + part;
						if (year > currentYear) {
							year -= 100;
						}
						break;
					case MONTH:
						monthString = matcher.group(offset);
						numericMonth = monthString.matches("\\d");
						for (month = 0; month < 12; month++) {
							if (matcher.group(offset + month + 1) != null) {
								break;
							}
						}
						offset += 12;
						break;
					case DAY:
						day = Integer.parseInt(matcher.group(++offset));
						offset += 2;
						break;
					}
					if (firstSep == null) {
						firstSep = matcher.group(offset);
					}
					else if (secondSep == null && order.length > 2) {
						secondSep = matcher.group(offset);
					}
					offset++;
				}
				if (order.length > 2 && !firstSep.equals(secondSep)) {
					if (!monthString.endsWith(".") || !firstSep.matches("\\s*|\\.") || !secondSep.matches("\\s*|\\.")) {
						return null;
					}
				}
				if (numericMonth && firstSep.matches("\\s")) {
					return null;
				}
				switch (month) {
				case 1:
					if (day > (isLeapYear(year) ? 29 : 28)) {
						return null;
					}
					break;
				case 3:
				case 5:
				case 8:
				case 10:
					if (day > 30) {
						return null;
					}
					break;
				}
				return new Date(year - 1900, month, day);
			}
		};
	}
}
