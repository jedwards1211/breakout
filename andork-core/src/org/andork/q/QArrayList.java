package org.andork.q;

import java.util.ArrayList;
import java.util.Collection;

import org.andork.func.Mapper;

public class QArrayList<E> extends QList<E, ArrayList<E>>
{
	public static <E> QArrayList<E> newInstance( )
	{
		return new QArrayList<E>( );
	}
	
	public static <E> QArrayList<E> newInstance( Collection<? extends E> c )
	{
		QArrayList<E> result = newInstance( );
		result.addAll( c );
		return result;
	}
	
	@Override
	protected ArrayList<E> createCollection( )
	{
		return new ArrayList<E>( );
	}
	
	public void trimToSize( )
	{
		collection.trimToSize( );
	}
	
	public void ensureCapacity( int minCapacity )
	{
		collection.ensureCapacity( minCapacity );
	}
	
	@Override
	public QArrayList<E> deepClone( Mapper<Object, Object> childMapper )
	{
		QArrayList<E> result = newInstance( );
		for( E elem : this )
		{
			result.add( ( E ) childMapper.map( elem ) );
		}
		return result;
	}
}
