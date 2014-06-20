package org.andork.q;

import java.util.HashSet;

import org.andork.func.Mapper;

public class QHashSet<E> extends QSet<E, HashSet<E>>
{
	public static <E> QHashSet<E> newInstance( )
	{
		return new QHashSet<E>( );
	}
	
	@Override
	protected HashSet<E> createCollection( )
	{
		return new HashSet<E>( );
	}
	
	@Override
	public QHashSet<E> deepClone( Mapper<Object, Object> childMapper )
	{
		QHashSet<E> result = newInstance( );
		for( E elem : this )
		{
			result.add( ( E ) childMapper.map( elem ) );
		}
		return result;
	}
}
