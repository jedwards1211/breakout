package org.andork.collect;

import java.util.LinkedHashMap;

@SuppressWarnings( "serial" )
public class MapLiteral<K, V> extends LinkedHashMap<K, V>
{
	public MapLiteral<K, V> map( K key , V value )
	{
		put( key , value );
		return this;
	}

	public static <K, V> MapLiteral<K, V> create( )
	{
		return new MapLiteral<>( );
	}
}
