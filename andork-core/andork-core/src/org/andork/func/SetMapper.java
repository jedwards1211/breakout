package org.andork.func;

import java.util.LinkedHashSet;
import java.util.Set;

public class SetMapper<I, O> implements Mapper<Set<I>, Set<O>> {
	Mapper<I, O>	elemMapper;

	public SetMapper(Mapper<I, O> elemMapper) {
		this.elemMapper = elemMapper;
	}

	public static <I, O> SetMapper<I, O> newInstance(Mapper<I, O> elemMapper) {
		return new SetMapper<I, O>(elemMapper);
	}

	@Override
	public Set<O> map(Set<I> in) {
		Set<O> result = new LinkedHashSet<O>();
		for (I i : in) {
			result.add(elemMapper.map(i));
		}
		return result;
	}
}
