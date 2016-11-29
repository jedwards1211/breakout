package org.andork.util;

/**
 * Because some things are so much easier in JS.
 */
public class JavaScript {
	/**
	 * @return true if s is null or empty.
	 */
	public static boolean falsy(String s) {
		return s == null || s.isEmpty();
	}

	/**
	 * @return true iff d is not NaN or infinite.
	 */
	public static boolean isFinite(float d) {
		return !Float.isNaN(d) && !Float.isInfinite(d);
	}

	@SafeVarargs
	public static <T> T or(T... objects) {
		int i;
		for (i = 0; i < objects.length - 1; i++) {
			if (truthy(objects[i])) {
				break;
			}
		}
		return i < objects.length ? objects[i] : null;
	}

	/**
	 * @return the first string that is {@link #truthy(String)}, or the last
	 *         string
	 */
	public static String or(String... strings) {
		int i;
		for (i = 0; i < strings.length - 1; i++) {
			if (truthy(strings[i])) {
				break;
			}
		}
		return i < strings.length ? strings[i] : null;
	}

	public static boolean truthy(Object o) {
		return o != null && !o.equals(false) && !o.equals("") && !o.equals(0) &&
				(!(o instanceof Number) || Double.isNaN(((Number) o).doubleValue()));
	}

	/**
	 * @return true if s is not null or empty.
	 */
	public static boolean truthy(String s) {
		return s != null && !s.isEmpty();
	}
}
