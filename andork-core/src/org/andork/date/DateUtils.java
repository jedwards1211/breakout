package org.andork.date;

import java.util.Calendar;

public class DateUtils
{
	public static long daysSinceTheJesus( Calendar cal )
	{
		long year = cal.get( Calendar.YEAR );
		long dayOfYear = cal.get( Calendar.DAY_OF_YEAR );
		
		return year * 365
				+ ( year + 3 ) / 4
				- ( year + 99 ) / 100
				+ ( year + 399 ) / 400
				+ dayOfYear - 1;
	}
}
