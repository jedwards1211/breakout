package org.andork.q2;

import java.util.List;

public interface QListListener<E> extends QListener
{
	public void
		listChanged( QList<E, ?> list , QChange change , int index , E oldValue , E newValue );

	public void
		listChanged( QList<E, ?> list , QChange change , List<Integer> indices , List<E> oldValues ,
			List<E> newValues );
}
