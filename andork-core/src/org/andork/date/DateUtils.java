/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.date;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

public class DateUtils {
	public static long daysSinceTheJesus(Calendar cal) {
		long year = cal.get(Calendar.YEAR);
		long dayOfYear = cal.get(Calendar.DAY_OF_YEAR);

		return year * 365 + (year + 3) / 4 - (year + 99) / 100 + (year + 399) / 400 + dayOfYear - 1;
	}

	public static Calendar startOf(Calendar cal, int field) {
		cal = (Calendar) cal.clone();
		switch (field) {
		case Calendar.YEAR:
			cal.set(Calendar.MONTH, 0);
		case Calendar.MONTH:
			cal.set(Calendar.DATE, 1);
		case Calendar.DATE:
			cal.set(Calendar.HOUR, 0);
		case Calendar.HOUR:
			cal.set(Calendar.MINUTE, 0);
		case Calendar.MINUTE:
			cal.set(Calendar.SECOND, 0);
		case Calendar.SECOND:
			cal.set(Calendar.MILLISECOND, 0);
		}

		return cal;
	}

	public static Date calendarOp(Date date, Consumer<Calendar> op) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		op.accept(cal);
		return cal.getTime();
	}

	public static Date startOf(Date date, int field) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return startOf(cal, field).getTime();
	}

	public static Calendar endOf(Calendar cal, int field) {
		Calendar result = (Calendar) cal.clone();
		result = startOf(result, field);
		if (!result.equals(cal))
			result.add(field, 1);
		return result;
	}

	public static Date endOf(Date date, int field) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return DateUtils.endOf(cal, field).getTime();
	}

	public static Calendar lowerOf(Calendar cal, int field) {
		Calendar result = startOf(cal, field);
		if (result.equals(cal))
			result.add(field, -1);
		return result;
	}

	public static Date lowerOf(Date date, int field) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return DateUtils.lowerOf(cal, field).getTime();
	}

	public static Calendar higherOf(Calendar cal, int field) {
		Calendar result = endOf(cal, field);
		if (result.equals(cal))
			result.add(field, 1);
		return result;
	}

	public static Date higherOf(Date date, int field) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return DateUtils.higherOf(cal, field).getTime();
	}

	public static Date add(Date date, int field, int amount) {
		return DateUtils.calendarOp(date, cal -> cal.add(field, amount));
	}

	public static Date sub(Date date, int field, int amount) {
		return add(date, field, -amount);
	}
}
