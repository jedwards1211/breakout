package org.andork.scrape;

public interface ScrapeMatchResult<T> {
	int start();

	int end();

	String matchText();

	T match();
}
