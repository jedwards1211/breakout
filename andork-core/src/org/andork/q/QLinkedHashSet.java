package org.andork.q;

import java.util.LinkedHashSet;

public class QLinkedHashSet<E> extends QSet<E, LinkedHashSet<E>>
{
	public static <E> QLinkedHashSet<E> newInstance()
	{
		return new QLinkedHashSet<E>();
	}

	@Override
	protected LinkedHashSet<E> createCollection()
	{
		return new LinkedHashSet<E>();
	}
}
