package org.andork.func;

@SuppressWarnings({"rawtypes","unchecked"})
public class CompoundBimapper<I, O> extends CompoundMapper<I, O> implements Bimapper<I, O>
{
	protected CompoundBimapper(Bimapper m0, Bimapper m1)
	{
		super(m0, m1);
	}

	public static <I, M, O> CompoundBimapper<I, O> compose(Bimapper<I, M> m0, Bimapper<M, O> m1) {
		return new CompoundBimapper<I, O>(m0, m1);
	}

	@Override
	public I unmap(O out)
	{
		return (I) ((Bimapper) m0).unmap(((Bimapper) m1).unmap(out));
	}
}
