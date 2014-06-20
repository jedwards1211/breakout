package org.andork.func;

import java.util.IdentityHashMap;

/**
 * A {@link Mapper} whose {@link #map(Object)} and {@link #unmap(Object)} methods will return
 * the same values for the same parameters, even if the wrapped {@link Mapper} returns different (but equal)
 * values for the same parameters.
 * 
 * @author andy.edwards
 */
public class IdentityHashMapMapper<I, O> implements Mapper<I, O> {
	private final Mapper<I, O>		wrapped;
	private final IdentityHashMap<I, O>	inToOut	= new IdentityHashMap<I, O>();

	public IdentityHashMapMapper(Mapper<I, O> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public O map(I in) {
		O result = inToOut.get(in);
		if (result == null) {
			inToOut.put(in, result = wrapped.map(in));
		}
		return result;
	}
}
