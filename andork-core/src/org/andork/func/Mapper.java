package org.andork.func;


public interface Mapper<I, O> {
	public O map(I in);
}
