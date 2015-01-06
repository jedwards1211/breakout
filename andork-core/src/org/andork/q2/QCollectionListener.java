package org.andork.q2;

import java.util.Collection;

public interface QCollectionListener extends QListener
{
	public void collectionChanged( QCollection<?, ?> source , QCollectionChange changeType , Object elem );

	public void collectionChanged( QCollection<?, ?> source , QCollectionChange changeType , Collection<?> elems );
}
