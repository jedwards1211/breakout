package org.andork.util;

public class ComparableUtils
{
	public static <T extends Comparable<T>> T min( T a , T b )
	{
		return a == null ? b : b == null ? null : a.compareTo( b ) < 0 ? a : b;
	}

	public static <T extends Comparable<T>> T max( T a , T b )
	{
		return a == null ? b : b == null ? null : a.compareTo( b ) > 0 ? a : b;
	}
}
