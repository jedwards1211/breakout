package org.andork.scrape;

import java.util.regex.Matcher;

public abstract class AbstractRegexScrapeMatcher<T> implements ScrapeMatcher<T> {
	protected final ScrapePattern<T> pattern;
	protected final Matcher matcher;

	protected T parsed = null;

	public AbstractRegexScrapeMatcher(ScrapePattern<T> pattern, Matcher matcher) {
		this.pattern = pattern;
		this.matcher = matcher;
	}

	@Override
	public ScrapePattern<T> pattern() {
		return pattern;
	}

	@Override
	public AbstractRegexScrapeMatcher<T> reset() {
		matcher.reset();
		parsed = null;
		return this;
	}

	@Override
	public AbstractRegexScrapeMatcher<T> reset(CharSequence input) {
		matcher.reset(input);
		parsed = null;
		return this;
	}

	@Override
	public int start() {
		return matcher.start();
	}

	@Override
	public int end() {
		return matcher.end();
	}

	@Override
	public boolean matches() {
		boolean result = matcher.matches() && parse();
		if (!result)
			parsed = null;
		return result;
	}

	@Override
	public boolean find() {
		while (matcher.find()) {
			System.out.println(matcher.group());
			if (parse()) {
				return true;
			}
		}
		parsed = null;
		return false;
	}

	@Override
	public boolean find(int start) {
		boolean found = matcher.find(start);
		while (found) {
			if (parse()) {
				return true;
			}
			found = matcher.find();
		}
		parsed = null;
		return false;
	}

	@Override
	public boolean lookingAt() {
		boolean result = matcher.lookingAt() && parse();
		if (!result)
			parsed = null;
		return result;
	}

	@Override
	public AbstractRegexScrapeMatcher<T> region(int start, int end) {
		matcher.region(start, end);
		return this;
	}

	@Override
	public int regionStart() {
		return matcher.regionStart();
	}

	@Override
	public int regionEnd() {
		return matcher.regionEnd();
	}

	@Override
	public boolean hitEnd() {
		return matcher.hitEnd();
	}

	@Override
	public String matchText() {
		return matcher.group();
	}

	@Override
	public T match() {
		return parsed;
	}

	/**
	 * Parses the current match.
	 * 
	 * @return <code>true</code> iff the current match is valid and was actually
	 *         parsed.
	 */
	protected abstract boolean parse();
}
