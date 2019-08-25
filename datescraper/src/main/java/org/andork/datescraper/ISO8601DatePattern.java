package org.andork.datescraper;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.scrape.ScrapeMatcher;
import org.andork.scrape.ScrapePattern;

public class ISO8601DatePattern implements DatePattern {
	private static final Pattern pattern =
		Pattern.compile("\b(\\d{4})(0\\d|1[012])([012]\\d|3[01])T([0-5]\\d):([0-5]\\d)(:([0-5]\\d))?\b");

	public static void main(String[] args) {
		ScrapePattern<Date> p = new ISO8601DatePattern();
		ScrapeMatcher<Date> m = p.matcher("20160830T05:23");
		m.find();
		System.out.println(m.match());
	}

	@Override
	public DateMatcher matcher(CharSequence input) {
		Matcher matcher = pattern.matcher(input);
		matcher.useTransparentBounds(true);
		return new AbstractDateMatcher(this, matcher) {
			@Override
			protected Date parseDate() {
				int year = Integer.parseInt(matcher.group(1));
				int month = Integer.parseInt(matcher.group(2));
				int day = Integer.parseInt(matcher.group(3));
				int hour = Integer.parseInt(matcher.group(4));
				int minute = Integer.parseInt(matcher.group(5));
				int second = matcher.group(7) != null ? Integer.parseInt(matcher.group(7)) : 0;

				return new Date(year - 1900, month - 1, day, hour, minute, second);
			}
		};
	}
}
