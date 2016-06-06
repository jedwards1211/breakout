package org.andork.func;

public class Bimappers {
	public static final <T> Bimapper<T, T> identity() {
		return new Bimapper<T, T>() {
			@Override
			public T map(T in) {
				return in;
			}

			@Override
			public T unmap(T out) {
				return out;
			}

		};
	}
}
