package org.andork.scrape;

import java.util.PriorityQueue;

public class MultiScrapeMatcher<T> implements ScrapeMatcher<T> {
	protected final MultiScrapePattern<T> pattern;
	protected final ScrapeMatcher<? extends T>[] matchers;

	protected boolean initialized = false;
	protected final PriorityQueue<PriorityEntry> queue = new PriorityQueue<>();

	protected MultiScrapeMatchResult<T> match = null;

	@SuppressWarnings("unchecked")
	protected MultiScrapeMatcher(MultiScrapePattern<T> pattern, CharSequence input) {
		this.pattern = pattern;
		matchers = new ScrapeMatcher[pattern.patterns.length];
		for (int i = 0; i < pattern.patterns.length; i++) {
			matchers[i] = pattern.patterns[i].matcher(input);
		}
	}

	@Override
	public ScrapePattern<T> pattern() {
		return pattern;
	}

	@Override
	public MultiScrapeMatcher<T> reset() {
		queue.clear();
		match = null;
		initialized = false;
		for (ScrapeMatcher<?> matcher : matchers) {
			matcher.reset();
		}
		return this;
	}

	@Override
	public MultiScrapeMatcher<T> reset(CharSequence input) {
		queue.clear();
		match = null;
		initialized = false;
		for (ScrapeMatcher<?> matcher : matchers) {
			matcher.reset(input);
		}
		return this;
	}

	MultiScrapeMatchResult<? extends T> matchResult() {
		if (match == null) {
			throw new IllegalStateException("there is no current match");
		}
		return match;
	}

	@Override
	public int start() {
		return matchResult().start();
	}

	@Override
	public int end() {
		return matchResult().end();
	}

	@Override
	public T match() {
		return matchResult().match();
	}

	@Override
	public String matchText() {
		return matchResult().matchText();
	}

	void initialize() {
		if (initialized)
			return;
		for (int i = 0; i < matchers.length; i++) {
			if (matchers[i].find()) {
				queue.add(new PriorityEntry(i));
			}
		}
		initialized = true;
	}

	@Override
	public boolean matches() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public boolean find() {
		if (queue.isEmpty()) {
			initialize();
			if (queue.isEmpty()) {
				match = null;
				return false;
			}
		}
		PriorityEntry e = queue.poll();
		ScrapeMatcher<? extends T> m = e.matcher();
		if (match != null) {
			while (m.start() < match.end()) {
				if (m.find()) {
					queue.add(e);
				}
				if (queue.isEmpty()) {
					match = null;
					return false;
				}
				e = queue.poll();
				m = e.matcher();
			}
		}
		match = new MultiScrapeMatchResult<T>(m.pattern(), m.start(), m.end(), m.matchText(), m.match(), m.hitEnd());
		if (m.find()) {
			queue.add(e);
		}
		return true;
	}

	@Override
	public boolean find(int start) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public boolean lookingAt() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public MultiScrapeMatcher<T> region(int start, int end) {
		for (ScrapeMatcher<?> matcher : matchers) {
			matcher.region(start, end);
		}
		return this;
	}

	@Override
	public int regionStart() {
		return matchers[0].regionStart();
	}

	@Override
	public int regionEnd() {
		return matchers[1].regionStart();
	}

	@Override
	public boolean hitEnd() {
		return matchResult().hitEnd;
	}

	private class PriorityEntry implements Comparable<PriorityEntry> {
		final int index;

		public PriorityEntry(int index) {
			super();
			this.index = index;
		}

		ScrapeMatcher<? extends T> matcher() {
			return matchers[index];
		}

		@Override
		public int compareTo(PriorityEntry o) {
			int startDiff = matcher().start() - o.matcher().start();
			if (startDiff != 0)
				return startDiff;
			return index - o.index;
		}

	}
}

class MultiScrapeMatchResult<T> extends DefaultScrapeMatchResult<T> {
	public MultiScrapeMatchResult(
		ScrapePattern<? extends T> pattern,
		int start,
		int end,
		String matchText,
		T match,
		boolean hitEnd) {
		super(start, end, matchText, match);
		this.pattern = pattern;
		this.hitEnd = hitEnd;
	}

	public ScrapePattern<? extends T> pattern() {
		return pattern;
	}

	final ScrapePattern<? extends T> pattern;
	final boolean hitEnd;
}
