package org.andork.util;

public final class Pair<K, V>
{
	private K	key;
	
	private V	value;
	
	public Pair( K key , V value )
	{
		this.key = key;
		this.value = value;
	}
	
	public K getKey( )
	{
		return key;
	}
	
	public V getValue( )
	{
		return value;
	}
	
	public String toString( )
	{
		return key + "=" + value;
	}
	
	public int hashCode( )
	{
		return key.hashCode( );
	}
	
	public boolean equals( Object o )
	{
		if( o instanceof Pair )
		{
			Pair p = ( Pair ) o;
			return key.equals( p.key ) && value.equals( p.value );
		}
		return false;
	}
}
