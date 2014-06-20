package org.andork.func;

import java.util.LinkedHashSet;
import java.util.Set;

public class SetBimapper<I, O> extends SetMapper<I, O> implements Bimapper<Set<I>, Set<O>> {
	Bimapper<I, O>	elemBimapper;

	public SetBimapper(Bimapper<I, O> elemBimapper) {
		super(elemBimapper);
		this.elemBimapper = elemBimapper;
	}

	public static <I, O> SetBimapper<I, O> newInstance(Bimapper<I, O> elemBimapper) {
		return new SetBimapper<I, O>(elemBimapper);
	}

	@Override
	public Set<I> unmap(Set<O> out) {
		Set<I> result = new LinkedHashSet<I>();
		for (O o : out) {
			result.add(elemBimapper.unmap(o));
		}
		return result;
	}
}
