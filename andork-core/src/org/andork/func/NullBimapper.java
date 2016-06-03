package org.andork.func;

public class NullBimapper<I, O> implements Bimapper<I, O> {
	@Override
	public O map(I in) {
		return null;
	}

	@Override
	public I unmap(O out) {
		return null;
	}
}
