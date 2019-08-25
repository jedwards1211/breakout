package org.andork.scrape;

public class MultiScrapePattern<T> implements ScrapePattern<T> {
	final ScrapePattern<? extends T>[] patterns;

	public MultiScrapePattern(ScrapePattern<? extends T>[] patterns) {
		super();
		this.patterns = patterns;
	}

	@Override
	public MultiScrapeMatcher<T> matcher(CharSequence input) {
		return new MultiScrapeMatcher<>(this, input);
	}

}
