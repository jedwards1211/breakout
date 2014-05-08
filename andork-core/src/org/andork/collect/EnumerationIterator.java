package org.andork.collect;
import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationIterator<T> implements Iterator<T> {
	private final Enumeration<T>	enumeration;

	public EnumerationIterator(Enumeration<T> enumeration) {
		super();
		this.enumeration = enumeration;
	}

	@Override
	public boolean hasNext() {
		return enumeration.hasMoreElements();
	}

	@Override
	public T next() {
		return enumeration.nextElement();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public static <T> Iterable<T> iterable(final Enumeration<T> enumeration) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new EnumerationIterator<T>(enumeration);
			}
		};
	}
}
