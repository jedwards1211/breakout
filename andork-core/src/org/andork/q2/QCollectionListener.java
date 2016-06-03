package org.andork.q2;

import java.util.Collection;

public interface QCollectionListener<E> extends QListener {
	public void collectionChanged(QCollection<E, ?> source, QChange changeType, Collection<E> elems);

	public void collectionChanged(QCollection<E, ?> source, QChange changeType, E elem);
}
