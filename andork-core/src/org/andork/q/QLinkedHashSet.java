package org.andork.q;

import java.util.LinkedHashSet;

import org.andork.func.Mapper;

public class QLinkedHashSet<E> extends QSet<E, LinkedHashSet<E>>
{
	public static <E> QLinkedHashSet<E> newInstance( )
	{
		return new QLinkedHashSet<E>( );
	}
	
	@Override
	protected LinkedHashSet<E> createCollection( )
	{
		return new LinkedHashSet<E>( );
	}
	
	@Override
	public QLinkedHashSet<E> deepClone( Mapper<Object, Object> childMapper )
	{
		QLinkedHashSet<E> result = newInstance( );
		for( E elem : this )
		{
			result.add( ( E ) childMapper.map( elem ) );
		}
		return result;
	}
}
