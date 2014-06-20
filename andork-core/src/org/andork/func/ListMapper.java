package org.andork.func;

import java.util.ArrayList;
import java.util.List;

public class ListMapper<I, O> implements Mapper<List<I>, List<O>> {
	Mapper<I, O>	elemMapper;

	public ListMapper(Mapper<I, O> elemMapper) {
		this.elemMapper = elemMapper;
	}

	public static <I, O> ListMapper<I, O> newInstance(Mapper<I, O> elemMapper) {
		return new ListMapper<I, O>(elemMapper);
	}

	@Override
	public List<O> map(List<I> in) {
		List<O> result = new ArrayList<O>();
		for (I i : in) {
			result.add(elemMapper.map(i));
		}
		return result;
	}
}
