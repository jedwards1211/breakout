package org.andork.util;

/**
 * Because some things are so much easier in JS.
 */
public class JavaScript {
	/**
	 * @return true iff d is not NaN or infinite.
	 */
	public static boolean isFinite(float d) {
		return !Float.isNaN(d) && !Float.isInfinite(d);
	}

	/**
	 * @return the first string that is {@link #truthy(String)}, or the last
	 *         string
	 */
	public static String or(String... strings) {
		int i;
		for (i = 0; i < strings.length - 2; i++) {
			if (truthy(strings[i])) {
				break;
			}
		}
		return i < strings.length ? strings[i] : null;
	}

	/**
	 * @return true if s is not null or empty.
	 */
	public static boolean truthy(String s) {
		return s != null && !s.isEmpty();
	}
}
