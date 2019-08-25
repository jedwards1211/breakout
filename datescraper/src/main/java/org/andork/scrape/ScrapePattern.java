package org.andork.scrape;

public interface ScrapePattern<T> {
	ScrapeMatcher<T> matcher(CharSequence input);
}
