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
package org.andork.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.func.CharPredicate;

public class StringUtils {
	public static final Pattern newlinePattern = Pattern.compile("\r\n?|\n");

	public static int lineCount(String s) {
		int count = 1;
		Matcher m = newlinePattern.matcher(s);
		while (m.find()) {
			count++;
		}
		return count;
	}

	public static String escape(String s, char escape) {
		StringBuilder sb = new StringBuilder();

		boolean inEscape = false;
		for (int i = 0; i < s.length(); i++) {
			char ic = s.charAt(i);
			if (inEscape || ic != escape) {
				sb.append(ic);
				inEscape = false;
			}
			else {
				inEscape = true;
			}
		}
		return sb.toString();
	}

	private static String
		formatThrowableForHTML(String prefix, Throwable t, Set<Throwable> visited, int maxStackTraceLines) {
		if (!visited.add(t)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<code>").append(prefix);
		sb.append("<b>").append(t.getClass().getSimpleName()).append("</b>");
		sb.append(": ").append(t.getLocalizedMessage()).append("<br />");

		StackTraceElement[] stackTrace = t.getStackTrace();

		for (int line = 0; line < maxStackTraceLines && line < stackTrace.length; line++) {
			sb.append("&emsp;at ").append(stackTrace[line]).append("<br />");
		}
		if (maxStackTraceLines < stackTrace.length) {
			sb.append("&emsp;...").append(stackTrace.length - maxStackTraceLines).append(" more<br />");
		}

		sb.append("</code>");

		if (t.getCause() != null) {
			sb.append(formatThrowableForHTML("Caused by: ", t.getCause(), visited, maxStackTraceLines));
		}
		return sb.toString();
	}

	public static String formatThrowableForHTML(Throwable t) {
		return formatThrowableForHTML("", t, new HashSet<Throwable>(), 10);
	}

	public static String formatThrowableForHTML(Throwable t, int maxStackTraceLines) {
		return formatThrowableForHTML("", t, new HashSet<Throwable>(), maxStackTraceLines);
	}

	/**
	 * Searches a string for a character matching a predicate and returns its index,
	 * or {@code -1} if no such character was found.
	 *
	 * @param s         a string to search.
	 * @param predicate the search predicate.
	 * @return the first index for which {@code predicate.test(s.charAt(index))} ,
	 *         or {@code -1}.
	 */
	public static int indexOf(String s, CharPredicate predicate) {
		return indexOf(s, 0, predicate);
	}

	/**
	 * Searches a string for a character matching a predicate and returns its index,
	 * or {@code -1} if no such character was found.
	 *
	 * @param s         a string to search.
	 * @param index     the search start index.
	 * @param predicate the search predicate.
	 * @return the first index >= {@code index} for which
	 *         {@code predicate.test(s.charAt(index))}, or {@code -1}.
	 */
	public static int indexOf(String s, int index, CharPredicate predicate) {
		for (int i = index; i < s.length(); i++) {
			if (predicate.test(s.charAt(i))) {
				return i;
			}
		}
		return -1;
	}

	public static boolean isNullOrEmpty(Object aValue) {
		return aValue == null || "".equals(aValue.toString());
	}

	public static String join(String separator, List<?> strings) {
		StringBuilder sb = new StringBuilder();
		if (strings.size() > 0) {
			sb.append(strings.get(0));
		}
		for (int i = 1; i < strings.size(); i++) {
			sb.append(separator).append(strings.get(i));
		}
		return sb.toString();
	}

	public static String join(String separator, String... strings) {
		StringBuilder sb = new StringBuilder();
		if (strings.length > 0) {
			sb.append(strings[0]);
		}
		for (int i = 1; i < strings.length; i++) {
			sb.append(separator).append(strings[i]);
		}
		return sb.toString();
	}

	public static String multiply(String s, int count) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < count; i++) {
			sb.append(s);
		}
		return sb.toString();
	}

	public static String nullifyIfEmpty(String s) {
		return "".equals(s) ? null : s;
	}

	public static String pad(String s, char padChar, int width, boolean leftJustify) {
		if (s.length() > width) {
			return s;
		}

		StringBuilder sb = new StringBuilder(width);

		if (leftJustify) {
			sb.append(s);
		}

		for (int i = 0; i < width - s.length(); i++) {
			sb.append(padChar);
		}

		if (!leftJustify) {
			sb.append(s);
		}

		return sb.toString();
	}

	public static String requireNonNullOrEmpty(String s) {
		if (isNullOrEmpty(s)) {
			throw new IllegalArgumentException("s must not be null or empty");
		}
		return s;
	}

	public static int unescapedIndexOf(String s, char c, char escape) {
		boolean inEscape = false;
		for (int i = 0; i < s.length(); i++) {
			char ic = s.charAt(i);
			if (inEscape) {
				inEscape = false;
			}
			else {
				if (ic == escape) {
					inEscape = true;
				}
				else if (ic == c) {
					return i;
				}
			}
		}
		return -1;
	}

	public static final Pattern wrapLocationPattern = Pattern.compile("(\r\n?|\n)|\\s+|$");

	public static String wrap(String s, int columns) {
		if (columns < 10)
			throw new IllegalArgumentException("columns must be >= 10");

		StringBuilder result = new StringBuilder();
		int i = 0;
		int lineStart = i;
		Matcher m = wrapLocationPattern.matcher(s);
		while (i < s.length()) {
			int next = Math.min(s.length(), i + columns);
			int end = next;
			m.region(i, s.length());
			while (m.find()) {
				if (m.start() > lineStart + columns) {
					break;
				}
				end = m.start();
				next = m.end();
				if (m.group(1) != null) {
					lineStart = m.end();
				}
			}
			if (result.length() > 0) {
				result.append('\n');
			}
			result.append(s.substring(i, end));
			lineStart = i = next;
		}
		return result.toString();
	}

	public static String valueOfOrNull(Object o) {
		return o != null ? String.valueOf(o) : null;
	}

	private StringUtils() {

	}
}
