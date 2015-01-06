package org.andork.q2;

import java.util.List;

public interface QListListener extends QListener
{
	public void
		listChanged( QList<?, ?> list , CollectionChange change , int index , Object oldValue , Object newValue );

	public void
		listChanged( QList<?, ?> list , CollectionChange change , List<Integer> indices , List<Object> oldValues ,
			List<Object> newValues );
}
