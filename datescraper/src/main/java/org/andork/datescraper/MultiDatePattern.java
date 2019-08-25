package org.andork.datescraper;

import java.util.Date;

import org.andork.scrape.MultiScrapePattern;

public class MultiDatePattern extends MultiScrapePattern<Date> implements DatePattern {
	public MultiDatePattern(DatePattern... patterns) {
		super(patterns);
	}

	@Override
	public MultiDateMatcher matcher(CharSequence input) {
		return new MultiDateMatcher(this, input);
	}
}
