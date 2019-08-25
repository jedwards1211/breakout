package org.andork.datescraper;

import java.util.Date;

import org.andork.scrape.ScrapeMatcher;

public interface DateMatcher extends ScrapeMatcher<Date> {
	public Date lowerBound();

	public DateMatcher lowerBound(Date lowerBound);

	public Date upperBound();

	public DateMatcher upperBound(Date upperBound);
}
