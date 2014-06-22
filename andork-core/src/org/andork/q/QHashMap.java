package org.andork.q;

import java.util.HashMap;
import java.util.Map;

import org.andork.func.Mapper;

public class QHashMap<K, V> extends QMap<K, V, HashMap<K, V>>
{
	public static <K, V> QHashMap<K, V> newInstance( )
	{
		return new QHashMap<K, V>( );
	}
	
	@Override
	protected HashMap<K, V> createMap( )
	{
		return new HashMap<K, V>( );
	}
	
	@Override
	public QElement deepClone( Mapper<Object, Object> childMapper )
	{
		QHashMap<K, V> result = newInstance( );
		for( Map.Entry<K, V> entry : entrySet( ) )
		{
			result.put( entry.getKey( ) , ( V ) childMapper.map( entry.getValue( ) ) );
		}
		return result;
	}
}
