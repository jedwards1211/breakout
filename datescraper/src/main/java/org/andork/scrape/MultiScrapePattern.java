package org.andork.scrape;

import java.util.Arrays;

public class MultiScrapePattern<T> implements ScrapePattern<T> {
	final ScrapePattern<? extends T>[] patterns;

	@SuppressWarnings("unchecked")
	public MultiScrapePattern(ScrapePattern<? extends T>... patterns) {
		super();
		this.patterns = Arrays.copyOf(patterns, patterns.length);
	}

	@Override
	public MultiScrapeMatcher<T> matcher(CharSequence input) {
		return new MultiScrapeMatcher<>(this, input);
	}

}
