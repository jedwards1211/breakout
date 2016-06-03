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
package org.andork.func;

import java.util.regex.Pattern;

import org.andork.util.Java7;

/**
 * A true/false function defined on some type that can be used for filtering or
 * other operations. Typically, a predicate function would always return the
 * same value for a given object.
 *
 * @author andy.edwards
 *
 * @param <E>
 */
public interface Predicate<E> {
	public static class Equals<E> implements Predicate<E> {
		private E value;

		public Equals(E value) {
			super();
			this.value = value;
		}

		@Override
		public boolean eval(E e) {
			return Java7.Objects.equals(e, value);
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "[" + value + "]";
		}
	}

	public static class Regex implements Predicate<String> {
		private Pattern pattern;

		public Regex(Pattern pattern) {
			super();
			this.pattern = pattern;
		}

		public Regex(String regex) {
			this(Pattern.compile(regex));
		}

		@Override
		public boolean eval(String e) {
			return pattern.matcher(e).matches();
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "[" + pattern + "]";
		}
	}

	public static class StartsWith implements Predicate<String> {
		private String prefix;

		public StartsWith(String prefix) {
			super();
			this.prefix = prefix;
		}

		@Override
		public boolean eval(String e) {
			return e != null && e.startsWith(prefix);
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "[" + prefix + "]";
		}
	}

	public boolean eval(E e);
}
