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
package com.andork.plot;

/**
 * Methods relating to grids, e.g. computing nice step sizes for humans.
 */
public class GridMath {
	/**
	 * Returns the least (closest to negative infinity) multiple of <code>mod</code>
	 * that is greater than or equal to <code>value</code>.
	 */
	public static double modCeiling(double value, double mod) {
		if (value < 0) {
			return -modFloor(-value, mod);
		}
		mod = Math.abs(mod);
		double rem = value % mod;
		if (rem != 0) {
			value += mod - rem;
		}
		return value;
	}

	/**
	 * @return the least (closest to negative infinity) value
	 *         {@code x = anchor + k * mod} for some integer {@code k} such that
	 *         {@code x >= value}.
	 */
	public static double modCeiling(double value, double mod, double anchor) {
		return anchor + modCeiling(value - anchor, mod);
	}

	/**
	 * Returns the greatest (closest to positive infinity) multiple of
	 * <code>mod</code> that is less than or equal to <code>value</code>.
	 */
	public static double modFloor(double value, double mod) {
		if (value < 0) {
			return -modCeiling(-value, mod);
		}
		mod = Math.abs(mod);
		return value - value % mod;
	}

	/**
	 * @return the greatest (closest to positive infinity) value
	 *         {@code x = anchor + k * mod} for some integer {@code k} such that
	 *         {@code x <= value}.
	 */
	public static double modFloor(double value, double mod, double anchor) {
		return anchor + modFloor(value - anchor, mod);
	}

	/**
	 * Computes the least (closest to negative infinity) "nice" fraction of a power
	 * of 10 (1/8, 1/5, 1/4, or 1/2 * 10^n) greater than or equal to the given
	 * number.
	 */
	public static double niceCeiling(double number) {
		if (number < 0) {
			return -niceFloor(-number);
		}

		int power = (int) Math.ceil(Math.log10(number));

		double order = Math.pow(10, power);

		if (order * .125f >= number) {
			return order * .125f;
		}
		if (order * .2f >= number) {
			return order * .2f;
		}
		if (order * .25f >= number) {
			return order * .25f;
		}
		if (order * .5f >= number) {
			return order * .5f;
		}
		return order;
	}

	public static int niceCeilingFractionDigits(double number) {
		if (number < 0) {
			return niceFloorFractionDigits(-number);
		}

		int power = (int) Math.ceil(Math.log10(number));

		double order = Math.pow(10, power);

		int digits = -power;

		if (order * .125f >= number) {
			digits += 3;
		}
		else if (order * .2f >= number) {
			digits += 1;
		}
		else if (order * .25f >= number) {
			digits += 2;
		}
		else if (order * .5f >= number) {
			digits += 1;
		}

		return Math.max(0, digits);
	}

	public static final int HALF = 0x1;
	public static final int QUARTER = HALF << 1;
	public static final int FIFTH = QUARTER << 1;
	public static final int EIGHTH = FIFTH << 1;
	public static final int ALL_FRACTIONS = HALF | QUARTER | FIFTH | EIGHTH;

	/**
	 * Computes the greatest (closest to positive infinity) "nice" fraction of a
	 * power of 10 (1/8, 1/5, 1/4, or 1/2 * 10^n) less than or equal to the given
	 * number.
	 */
	public static double niceFloor(double number) {
		return niceFloor(ALL_FRACTIONS);
	}

	/**
	 * Computes the greatest (closest to positive infinity) "nice" fraction of a
	 * power of 10 (1/8, 1/5, 1/4, or 1/2 * 10^n) less than or equal to the given
	 * number.
	 */
	public static double niceFloor(double number, int fractions) {
		if (number < 0) {
			return -niceCeiling(-number);
		}

		int power = (int) Math.ceil(Math.log10(number));

		double order = Math.pow(10, power);

		if ((fractions & HALF) != 0 && order * .5f <= number) {
			return order * .5f;
		}
		if ((fractions & QUARTER) != 0 && order * .25f <= number) {
			return order * .25f;
		}
		if ((fractions & FIFTH) != 0 && order * .2f <= number) {
			return order * .2f;
		}
		if ((fractions & EIGHTH) != 0 && order * .125f <= number) {
			return order * .125f;
		}
		return order * .1f;
	}

	public static int niceFloorFractionDigits(double number) {
		return niceFloorFractionDigits(ALL_FRACTIONS);
	}

	public static int niceFloorFractionDigits(double number, int fractions) {
		if (number < 0) {
			return niceCeilingFractionDigits(-number);
		}

		int power = (int) Math.ceil(Math.log10(number));

		double order = Math.pow(10, power);

		int digits = -power;

		if ((fractions & HALF) != 0 && order * .5f <= number) {
			digits += 1;
		}
		else if ((fractions & QUARTER) != 0 && order * .25f <= number) {
			digits += 2;
		}
		else if ((fractions & FIFTH) != 0 && order * .2f <= number) {
			digits += 1;
		}
		else if ((fractions & EIGHTH) != 0 && order * .125f <= number) {
			digits += 3;
		}
		else {
			digits += 1;
		}
		return Math.max(0, digits);
	}

	/**
	 * Computes the least (closest to negative infinity) "nice" fraction of a power
	 * of 10 (1/8, 1/5, 1/4, or 1/2 * 10^n) greater than or equal to the given
	 * number.
	 */
	public static double niceMajorGridLineSpacing(double minor) {
		if (minor < 0) {
			return -niceFloor(-minor);
		}

		int power = (int) Math.ceil(Math.log10(minor));

		double order = Math.pow(10, power);

		if (order * .125f >= minor) {
			return order * .25f;
		}
		if (order * .2f >= minor) {
			return order * .2f;
		}
		if (order * .25f >= minor) {
			return order * .5f;
		}
		if (order * .5f >= minor) {
			return order;
		}
		return order;
	}

	/**
	 * @return the least (closest to negative infinity) power of 10 that is
	 *         {@code >= number}.
	 */
	public static double order(double number) {
		int power = (int) Math.ceil(Math.log10(number));
		return Math.pow(10, power);
	}
}
