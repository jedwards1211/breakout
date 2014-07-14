package org.andork.func;

@FunctionalInterface
public interface ThrowingToDoubleFunction<I, T extends Throwable>
{
	public double applyAsDouble( I in ) throws T;
}
