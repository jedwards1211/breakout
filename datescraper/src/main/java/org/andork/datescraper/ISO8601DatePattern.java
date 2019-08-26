package org.andork.datescraper;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.scrape.ScrapeMatcher;
import org.andork.scrape.ScrapePattern;

public class ISO8601DatePattern implements DatePattern {
	private static final Pattern pattern =
		Pattern
			.compile(
				"\\b(\\d{4})(-?)(\\d{2})(\\2([012]\\d|3[01]))?T([0-5]\\d)((:?)([0-5]\\d)(\\8([0-5]\\d)(\\.(\\d{3}))?)?)?\\b");

	public static void main(String[] args) {
		ScrapePattern<Date> p = new ISO8601DatePattern();
		ScrapeMatcher<Date> m = p.matcher("2016-08-30T05:23 20180423T092632 190223T0524");
		while (m.find()) {
			System.out.println(m.match());
		}
	}

	@Override
	public DateMatcher matcher(CharSequence input) {
		Matcher matcher = pattern.matcher(input);
		matcher.useTransparentBounds(true);
		return new AbstractDateMatcher(this, matcher) {
			@Override
			protected Date parseDate() {
				int year = Integer.parseInt(matcher.group(1));
				int month = Integer.parseInt(matcher.group(3));
				int day = matcher.group(5) != null ? Integer.parseInt(matcher.group(5)) : 0;
				if (matcher.group(5) == null && "".equals(matcher.group(2))) {
					day = month;
					month = year % 100;
					int year2 = year / 100;
					year = (year2 >= 69 ? 1900 : 2000) + year2;
				}
				if (month < 1 || month > 12) {
					return null;
				}
				int hour = Integer.parseInt(matcher.group(6));
				int minute = matcher.group(9) != null ? Integer.parseInt(matcher.group(9)) : 0;
				int second = matcher.group(11) != null ? Integer.parseInt(matcher.group(11)) : 0;
				int millisecond = matcher.group(13) != null ? Integer.parseInt(matcher.group(13)) : 0;

				Date date = new Date(year - 1900, month - 1, day, hour, minute, second);
				date.setTime(date.getTime() + millisecond);
				return date;
			}
		};
	}
}
