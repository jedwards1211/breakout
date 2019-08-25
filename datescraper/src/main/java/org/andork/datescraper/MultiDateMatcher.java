package org.andork.datescraper;

import java.util.Date;

import org.andork.scrape.MultiScrapeMatcher;
import org.andork.scrape.ScrapeMatcher;

public class MultiDateMatcher extends MultiScrapeMatcher<Date> implements DateMatcher {
	MultiDateMatcher(MultiDatePattern pattern, CharSequence input) {
		super(pattern, input);
	}

	@Override
	public MultiDatePattern pattern() {
		return (MultiDatePattern) super.pattern();
	}

	@Override
	public MultiDateMatcher reset() {
		super.reset();
		return this;
	}

	@Override
	public MultiDateMatcher reset(CharSequence input) {
		super.reset(input);
		return this;
	}

	@Override
	public MultiDateMatcher region(int start, int end) {
		super.region(start, end);
		return this;
	}

	@Override
	public Date lowerBound() {
		return ((DateMatcher) matchers[0]).lowerBound();
	}

	@Override
	public MultiDateMatcher lowerBound(Date lowerBound) {
		for (ScrapeMatcher<? extends Date> matcher : matchers) {
			((DateMatcher) matcher).lowerBound(lowerBound);
		}
		return this;
	}

	@Override
	public Date upperBound() {
		return ((DateMatcher) matchers[0]).upperBound();
	}

	@Override
	public MultiDateMatcher upperBound(Date upperBound) {
		for (ScrapeMatcher<? extends Date> matcher : matchers) {
			((DateMatcher) matcher).upperBound(upperBound);
		}
		return this;
	}

}
