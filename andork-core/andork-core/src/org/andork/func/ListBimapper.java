package org.andork.func;

import java.util.ArrayList;
import java.util.List;

public class ListBimapper<I, O> extends ListMapper<I, O> implements Bimapper<List<I>, List<O>> {
	Bimapper<I, O>	elemBimapper;

	public ListBimapper(Bimapper<I, O> elemBimapper) {
		super(elemBimapper);
		this.elemBimapper = elemBimapper;
	}

	public static <I, O> ListBimapper<I, O> newInstance(Bimapper<I, O> elemBimapper) {
		return new ListBimapper<I, O>(elemBimapper);
	}

	@Override
	public List<I> unmap(List<O> out) {
		List<I> result = new ArrayList<I>();
		for (O o : out) {
			result.add(elemBimapper.unmap(o));
		}
		return result;
	}
}
