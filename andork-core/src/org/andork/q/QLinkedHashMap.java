package org.andork.q;

import java.util.LinkedHashMap;
import java.util.Map;

import org.andork.func.Mapper;

public class QLinkedHashMap<K, V> extends QMap<K, V, LinkedHashMap<K, V>>
{
	public static <K, V> QLinkedHashMap<K, V> newInstance( )
	{
		return new QLinkedHashMap<K, V>( );
	}
	
	@Override
	protected LinkedHashMap<K, V> createMap( )
	{
		return new LinkedHashMap<K, V>( );
	}
	
	@Override
	public QElement deepClone( Mapper<Object, Object> childMapper )
	{
		QLinkedHashMap<K, V> result = newInstance( );
		for( Map.Entry<K, V> entry : entrySet( ) )
		{
			result.put( entry.getKey( ) , ( V ) childMapper.map( entry.getValue( ) ) );
		}
		return result;
	}
}
