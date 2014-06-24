package org.andork.func;

public class OutCastingBimapper<I, O, OC> implements Bimapper<I, OC> {
	Bimapper<I, O>	wrapped;

	private OutCastingBimapper(Bimapper<I, O> bimapper) {
		this.wrapped = bimapper;
	}

	public static <I, O, OC> OutCastingBimapper<I, O, OC> cast(Bimapper<I, O> bimapper, Class<? super OC> outClass) {
		return new OutCastingBimapper<I, O, OC>(bimapper);
	}

	@Override
	public OC map(I in) {
		return (OC) wrapped.map(in);
	}

	@Override
	public I unmap(OC out) {
		return wrapped.unmap((O) out);
	}

}
