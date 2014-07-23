/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.swing.text;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.andork.util.Format;

public class Formats {
	public static Format<Integer> createIntegerFormat(int maxIntegerDigits) {
		return createIntegerFormat(maxIntegerDigits, true);
	}

	public static Format<Integer> createIntegerFormat(int maxIntegerDigits, final boolean allowNull) {
		NumberFormat format = new DecimalFormat();
		format.setMaximumIntegerDigits(maxIntegerDigits);
		format.setMaximumFractionDigits(0);
		format.setGroupingUsed(false);

		return createIntegerFormat(format, allowNull);
	}

	public static Format<Integer> createIntegerFormat(final NumberFormat format, final boolean allowNull) {
		return new Format<Integer>() {
			public Integer parse(String s) throws Exception {
				if (allowNull && (s == null || "".equals(s))) {
					return null;
				}
				return format.parse(s).intValue();
			}

			public String format(Integer t) {
				if (allowNull && t == null) {
					return null;
				}
				return format.format(t);
			}
		};
	}

	public static Format<Double> createDoubleFormat(int maxIntegerDigits, int fractionDigits) {
		return createDoubleFormat(maxIntegerDigits, fractionDigits, true);
	}

	public static Format<Double> createDoubleFormat(int maxIntegerDigits, int fractionDigits, final boolean allowNull) {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumIntegerDigits(maxIntegerDigits);
		format.setMinimumFractionDigits(fractionDigits);
		format.setMaximumFractionDigits(fractionDigits);
		format.setGroupingUsed(false);

		return createDoubleFormat(format, allowNull);
	}

	public static Format<Double> createDoubleFormat(final NumberFormat format, final boolean allowNull) {
		return new Format<Double>() {
			public Double parse(String s) throws Exception {
				if (allowNull && (s == null || "".equals(s))) {
					return null;
				}
				return format.parse(s).doubleValue();
			}

			public String format(Double t) {
				if (allowNull && t == null) {
					return null;
				}
				return format.format(t);
			}
		};
	}

	public static Format<BigDecimal> createBigDecimalFormat(int maxIntegerDigits, int fractionDigits) {
		return createBigDecimalFormat(maxIntegerDigits, fractionDigits, true);
	}

	public static Format<BigDecimal> createBigDecimalFormat(int maxIntegerDigits, int fractionDigits, final boolean allowNull) {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumIntegerDigits(maxIntegerDigits);
		format.setMinimumFractionDigits(fractionDigits);
		format.setMaximumFractionDigits(fractionDigits);
		format.setGroupingUsed(false);
		format.setParseBigDecimal(true);

		return createBigDecimalFormat(format, allowNull);
	}

	public static Format<BigDecimal> createBigDecimalFormat(final NumberFormat format, final boolean allowNull) {
		return new Format<BigDecimal>() {
			public BigDecimal parse(String s) throws Exception {
				if (allowNull && (s == null || "".equals(s))) {
					return null;
				}
				return (BigDecimal) format.parse(s);
			}

			public String format(BigDecimal t) {
				if (allowNull && t == null) {
					return null;
				}
				return format.format(t);
			}
		};
	}
}