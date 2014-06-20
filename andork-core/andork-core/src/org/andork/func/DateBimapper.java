package org.andork.func;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateBimapper implements Bimapper<Date, Object> {
	DateFormat	dateFormat;

	public DateBimapper(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public Object map(Date in) {
		return dateFormat.format(in);
	}

	@Override
	public Date unmap(Object out) {
		if (out instanceof Date) {
			return (Date) out;
		}
		try {
			return out == null ? null : dateFormat.parse(out.toString());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
