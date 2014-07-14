package org.andork.func;

@FunctionalInterface
public interface ThrowingFunction<I, O, T extends Throwable>
{
	public O apply( I in ) throws T;
}
