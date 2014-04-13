package org.andork.swing.text;

import java.util.regex.Pattern;

public class Patterns {
	public static Pattern createNumberPattern(int maxIntegerDigits, int fractionDigits, boolean allowEmpty) {
		String p = "0|[1-9]\\d{0," + (maxIntegerDigits - 1) + "}";
		if (allowEmpty) {
			p += "|";
		}
		if (fractionDigits > 0) {
			p = "(" + p + ")(\\.\\d{0," + fractionDigits + "})?";
		}
		return Pattern.compile(p);
	}
}
