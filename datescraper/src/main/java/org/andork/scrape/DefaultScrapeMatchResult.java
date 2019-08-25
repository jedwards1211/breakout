package org.andork.scrape;

public class DefaultScrapeMatchResult<T> implements ScrapeMatchResult<T> {
	private final int start;
	private final int end;
	private final String matchText;
	private final T match;

	public DefaultScrapeMatchResult(int start, int end, String matchText, T match) {
		super();
		this.start = start;
		this.end = end;
		this.matchText = matchText;
		this.match = match;
	}

	@Override
	public int start() {
		return start;
	}

	@Override
	public int end() {
		return end;
	}

	@Override
	public String matchText() {
		return matchText;
	}

	@Override
	public T match() {
		return match;
	}

}
