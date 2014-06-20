package org.andork.func;

import java.util.IdentityHashMap;

/**
 * A {@link Bimapper} whose {@link #map(Object)} and {@link #unmap(Object)} methods will return
 * the same values for the same parameters, even if the wrapped {@link Bimapper} returns different (but equal)
 * values for the same parameters.
 * 
 * @author andy.edwards
 */
public class IdentityHashMapBimapper<I, O> implements Bimapper<I, O> {
	private final Bimapper<I, O>		wrapped;
	private final IdentityHashMap<I, O>	inToOut	= new IdentityHashMap<I, O>();
	private final IdentityHashMap<O, I>	outToIn	= new IdentityHashMap<O, I>();

	public IdentityHashMapBimapper(Bimapper<I, O> wrapped) {
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

	@Override
	public I unmap(O out) {
		I result = outToIn.get(out);
		if (result == null) {
			outToIn.put(out, result = wrapped.unmap(out));
		}
		return result;
	}
}
