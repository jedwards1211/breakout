package org.andork.datescraper;

import java.util.Date;
import java.util.regex.Matcher;

import org.andork.scrape.AbstractRegexScrapeMatcher;
import org.andork.scrape.ScrapePattern;

public abstract class AbstractDateMatcher extends AbstractRegexScrapeMatcher<Date> implements DateMatcher {
	public AbstractDateMatcher(ScrapePattern<Date> pattern, Matcher matcher) {
		super(pattern, matcher);
	}

	protected Date lowerBound;
	protected Date upperBound;

	public Date lowerBound() {
		return lowerBound;
	}

	public DateMatcher lowerBound(Date lowerBound) {
		this.lowerBound = lowerBound;
		return this;
	}

	public Date upperBound() {
		return upperBound;
	}

	public DateMatcher upperBound(Date upperBound) {
		this.upperBound = upperBound;
		return this;
	}

	@Override
	protected boolean parse() {
		parsed = parseDate();
		if (parsed == null) {
			return false;
		}
		if ((lowerBound != null && lowerBound.compareTo(parsed) > 0)
			|| (upperBound != null && upperBound.compareTo(parsed) > 0)) {
			parsed = null;
			return false;
		}
		return true;
	}

	protected abstract Date parseDate();
}
