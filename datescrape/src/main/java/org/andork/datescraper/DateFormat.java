package org.andork.datescraper;

import java.util.Date;

public interface DateFormat {
	public static interface MatchGroups {
		public String group(int index);
	}

	public String pattern();
	public Date parse(MatchGroups groups);
}
