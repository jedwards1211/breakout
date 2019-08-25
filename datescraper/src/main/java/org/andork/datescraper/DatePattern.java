package org.andork.datescraper;

import java.util.Date;

import org.andork.scrape.ScrapePattern;

public interface DatePattern extends ScrapePattern<Date> {
	@Override
	DateMatcher matcher(CharSequence input);
}
