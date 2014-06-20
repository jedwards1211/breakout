package org.andork.q;

import java.util.ArrayList;

public class QArrayList<E> extends QList<E, ArrayList<E>>
{
	public static <E> QArrayList<E> newInstance()
	{
		return new QArrayList<E>();
	}

	@Override
	protected ArrayList<E> createCollection()
	{
		return new ArrayList<E>();
	}

	public void trimToSize() {
		collection.trimToSize();
	}

	public void ensureCapacity(int minCapacity) {
		collection.ensureCapacity(minCapacity);
	}
}
