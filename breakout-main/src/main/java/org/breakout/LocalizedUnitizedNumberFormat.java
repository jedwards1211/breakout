package org.breakout;

import java.text.NumberFormat;
import java.util.Locale;

import org.andork.unit.Length;
import org.andork.unit.UnitType;
import org.andork.unit.UnitizedNumber;

public class LocalizedUnitizedNumberFormat {

	NumberFormat format;
	Locale locale;

	public LocalizedUnitizedNumberFormat(Locale locale, NumberFormat format) {
		super();
		this.locale = locale;
		this.format = format;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public <T extends UnitType<T>> String format(UnitizedNumber<T> value) {
		if ("en".equals(locale.getLanguage())) {
			Number rawValue = value.get(value.unit);
			String formatted = format.format(rawValue);
			boolean isOne = formatted.matches("^1(\\.0*)?$");
			if (value.unit == Length.feet) {
				return format.format(rawValue) + (isOne ? " foot" : " feet");
			}
			if (value.unit == Length.inches) {
				return format.format(rawValue) + (isOne ? " inch" : " inches");
			}
			if (value.unit == Length.yards) {
				return format.format(rawValue) + (isOne ? " yard" : " yards");
			}
			if (value.unit == Length.centimeters) {
				return format.format(rawValue) + (isOne ? " centimeter" : " centimeters");
			}
			if (value.unit == Length.meters) {
				return format.format(rawValue) + (isOne ? " meter" : " meters");
			}
			if (value.unit == Length.kilometers) {
				return format.format(rawValue) + (isOne ? " kilometer" : " kilometers");
			}
			if (value.unit == Length.miles) {
				return format.format(rawValue) + (isOne ? " mile" : " miles");
			}
		}
		return value.toString(format);
	}
}
