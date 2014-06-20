package org.andork.q;

import java.util.HashSet;

public class QHashSet<E> extends QSet<E, HashSet<E>>
{
	public static <E> QHashSet<E> newInstance()
	{
		return new QHashSet<E>();
	}

	@Override
	protected HashSet<E> createCollection()
	{
		return new HashSet<E>();
	}
}
