package org.andork.util;

import java.util.Comparator;

public class Comparators {
	private Comparators() {

	}

	public static <T> Comparator<T> negate(Comparator<? super T> c) {
		return (a, b) -> -c.compare(a, b);
	}

	@SafeVarargs
	public static <T> Comparator<T> combine(Comparator<? super T>... comparators) {
		return (a, b) -> {
			for (Comparator<? super T> comparator : comparators) {
				int result = comparator.compare(a, b);
				if (result != 0) {
					return result;
				}
			}
			return 0;
		};
	}
}
