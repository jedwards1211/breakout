package org.andork.datescraper;

import static org.andork.datescraper.DateField.DAY;
import static org.andork.datescraper.DateField.FULL_YEAR;
import static org.andork.datescraper.DateField.MONTH;
import static org.andork.datescraper.DateField.TWO_DIGIT_YEAR;

public class DatePatterns {
	private DatePatterns() {

	}

	public static final DatePattern en_US =
		new MultiDatePattern(
			new ISO8601DatePattern(),
			new GeneralDatePattern().order(MONTH, DAY, FULL_YEAR),
			new GeneralDatePattern().order(MONTH, DAY, TWO_DIGIT_YEAR),
			new GeneralDatePattern().order(FULL_YEAR, MONTH, DAY),
			new GeneralDatePattern().order(MONTH, FULL_YEAR),
			new GeneralDatePattern().order(FULL_YEAR, MONTH),
			new GeneralDatePattern().order(DAY, MONTH, FULL_YEAR),
			new GeneralDatePattern().order(TWO_DIGIT_YEAR, MONTH, DAY));
}
