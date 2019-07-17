package org.andork.datescraper;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiDateFormat implements DateFormat {
	private DateFormat[] formats;
	private int[] groupOffsets;
	
	private Pattern pattern;
	
	public MultiDateFormat(DateFormat... formats) {
		super();
		this.formats = formats;
		groupOffsets = new int[formats.length];
		int offset = 1;
		for (int i = 0; i < formats.length; i++) {
			groupOffsets[i] = offset;
			Matcher m = Pattern.compile(formats[i].pattern()).matcher("");
			offset += m.groupCount() + 1;
		}
	}

	@Override
	public String pattern() {
		StringBuilder builder = new StringBuilder();
		for (DateFormat format : formats) {
			if (builder.length() > 0) builder.append("|");
			builder.append("(").append(format.pattern()).append(")");
		}
		return builder.toString();
	}

	@Override
	public Date parse(MatchGroups groups) {
		for (int i = 0; i < groupOffsets.length; i++) {
			int offset = groupOffsets[i];
			if (groups.group(offset) != null) {
				return formats[i].parse(group -> groups.group(group + offset));
			}
		}
		return null;
	}
	
	public Iterator<Date> iterator(String s) {
		if (pattern == null) pattern = Pattern.compile(pattern(), Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(s);
		return new Iterator<Date>() {
			boolean done = false;
			Date next = null;
			
			private boolean findNext() {
				if (done) return false;
				while (m.find()) {
					System.out.println(m.group());
					next = parse(group -> m.group(group));
					if (next != null) return true;
				}
				done = true;
				return false;
			}

			@Override
			public boolean hasNext() {
				if (next == null) return findNext();
				return false;
			}

			@Override
			public Date next() {
				if (next == null) findNext();
				Date result = next;
				next = null;
				return result;
			}
		};
	}
	
	public Iterable<Date> iterable(String s) {
		return new Iterable<Date>() {
			@Override
			public Iterator<Date> iterator() {
				return MultiDateFormat.this.iterator(s);
			}
		};
	}
	
	public static void main(String[] args) {
		System.out.println(new MultiDateFormat(GeneralDateFormat.EN_US_FORMATS).pattern());
		System.out.println(Arrays.toString(new MultiDateFormat(GeneralDateFormat.EN_US_FORMATS).groupOffsets));
		for (Date date : new MultiDateFormat(GeneralDateFormat.EN_US_FORMATS).iterable("04-02-29th,Aug 03, 2012 2 Sep 1964 2  blah 1394/02/3   2019.jul.31 0 2k389 182  23 1 29  38482 29 3 28 3-4-2019 jan12,2013\t2019jul.20 2016aug03 5 mar. 14 2018 20190822T03:45")) {
			System.out.println(date);
		}
	}
}
