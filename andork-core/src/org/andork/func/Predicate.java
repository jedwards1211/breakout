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
	public boolean eval(E e);

	public static class Equals<E> implements Predicate<E> {
		private E	value;

		public Equals(E value) {
			super();
			this.value = value;
		}

		public boolean eval(E e) {
			return Java7.Objects.equals(e, value);
		}

		public String toString() {
			return getClass().getSimpleName() + "[" + value + "]";
		}
	}

	public static class Regex implements Predicate<String> {
		private Pattern	pattern;

		public Regex(Pattern pattern) {
			super();
			this.pattern = pattern;
		}

		public Regex(String regex) {
			this(Pattern.compile(regex));
		}

		public boolean eval(String e) {
			return pattern.matcher(e).matches();
		}

		public String toString() {
			return getClass().getSimpleName() + "[" + pattern + "]";
		}
	}

	public static class StartsWith implements Predicate<String> {
		private String	prefix;

		public StartsWith(String prefix) {
			super();
			this.prefix = prefix;
		}

		@Override
		public boolean eval(String e) {
			return e != null && e.startsWith(prefix);
		}

		public String toString() {
			return getClass().getSimpleName() + "[" + prefix + "]";
		}
	}
}
