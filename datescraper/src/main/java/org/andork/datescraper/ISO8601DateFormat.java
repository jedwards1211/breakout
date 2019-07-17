package org.andork.datescraper;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ISO8601DateFormat implements DateFormat {
	public String pattern() {
		return "(?!<\\d)(\\d{4})(0\\d|1[012])([012]\\d|3[01])T([0-5]\\d):([0-5]\\d)(:([0-5]\\d))?(?!\\d)";
	}

	@SuppressWarnings("deprecation")
	public Date parse(MatchGroups groups) {
		int year = Integer.parseInt(groups.group(1));
		int month = Integer.parseInt(groups.group(2));
		int day = Integer.parseInt(groups.group(3));
		int hour = Integer.parseInt(groups.group(4));
		int minute = Integer.parseInt(groups.group(5));
		int second = groups.group(7) != null ? Integer.parseInt(groups.group(7)) : 0;
		
		return new Date(year - 1900, month - 1, day, hour, minute, second);
	}
	
	public static void main(String[] args) {
		DateFormat format = new ISO8601DateFormat();
		Pattern p = Pattern.compile(format.pattern());
		Matcher m = p.matcher("20160830T05:23");
		m.find();
		Date date = format.parse(group -> m.group(group));
		System.out.println(date);
	}
}
