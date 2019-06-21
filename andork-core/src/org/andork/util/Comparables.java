package org.andork.util;

import java.util.Comparator;

public class Comparables {
	public static <T extends Comparable<T>> T max(T a, T b) {
		return a == null ? b : b == null ? null : a.compareTo(b) > 0 ? a : b;
	}

	public static <T extends Comparable<T>> T min(T a, T b) {
		return a == null ? b : b == null ? null : a.compareTo(b) < 0 ? a : b;
	}
	
	public static <T> int compareNullsLast(T a, T b, Comparator<? super T> comparator) {
		if (a == null) return b == null ? 0 : 1;
		if (b == null) return -1;
		return comparator.compare(a, b);
	}
}
