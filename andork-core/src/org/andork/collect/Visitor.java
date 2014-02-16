package org.andork.collect;

public interface Visitor<T> {
	public boolean visit(T t);
}
