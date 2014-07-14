package org.andork.func;

@FunctionalInterface
public interface ThrowingDoubleFunction<O, T extends Throwable>
{
	public O apply( double in ) throws T;
}
