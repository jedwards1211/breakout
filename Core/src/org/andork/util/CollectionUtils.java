package org.andork.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionUtils
{

	/**
	 * Adds a key-value pair to a map that can contain multiple values per key.
	 * 
	 * @return <code>true</code> if <code>relation</code> did not already contain the key-value pair
	 */
	public static <K,V>boolean relate( Map<K,Set<V>> relation , K key , V value )
	{
		Set<V> values = relation.get( key );
		if( values == null )
		{
			values = new HashSet<V>( );
			relation.put( key , values );
		}
		return values.add( value );
	}

	/**
	 * Adds key-value pairs to a map that can contain multiple values per key. Adds one pair with the given key for each value in <code>values</code>.
	 * 
	 * @return <code>true</code> if <code>relation</code> was modified as a result of the call to <code>relate</code>.
	 */
	public static <K,V>boolean relate( Map<K,Set<V>> relation , K key , Collection<V> values )
	{
		Set<V> valueSet = relation.get( key );
		if( valueSet == null )
		{
			valueSet = new HashSet<V>( );
			relation.put( key , valueSet );
		}
		return valueSet.addAll( values );
	}

	/**
	 * Removes a key-value pair from a map that can contain multiple values per key.
	 * 
	 * @return <code>true</code> if <code>relation</code> contained the key-value pair
	 */
	public static <K,V>boolean unrelate( Map<K,Set<V>> relation , K key , V value )
	{
		final Set<V> values = relation.get( key );
		if( values != null )
		{
			final boolean result = values.remove( value );
			if( values.isEmpty( ) )
			{
				relation.remove( key );
			}
			return result;
		}
		return false;
	}

	/**
	 * Removes key-value pairs from a map that can contain multiple values per key. Ensures that the given key will not be associated with the given values.
	 * 
	 * @return <code>true</code> if <code>relation</code> was modified as a result of the call to <code>unrelate</code>.
	 */
	public static <K,V>boolean unrelate( Map<K,Set<V>> relation , K key , Collection<V> values )
	{
		final Set<V> valueSet = relation.get( key );
		if( valueSet != null )
		{
			final boolean result = valueSet.removeAll( values );
			if( valueSet.isEmpty( ) )
			{
				relation.remove( key );
			}
			return result;
		}
		return false;
	}

	/**
	 * Determines if a key-value pair exists in a map that can contain multiple values per key.
	 */
	public static <K,V>boolean areRelated( Map<K,Set<V>> relation , K key , V value )
	{
		final Set<V> valueSet = relation.get( key );
		return valueSet != null ? valueSet.contains( value ) : false;
	}

	/**
	 * Determines if all key-value pairs exist in a map that can contain multiple values per key.
	 */
	public static <K,V>boolean areRelated( Map<K,Set<V>> relation , K key , Collection<V> values )
	{
		final Set<V> valueSet = relation.get( key );
		return valueSet != null ? valueSet.containsAll( values ) : false;
	}

	public static int[ ] toIntArray( Collection<Integer> values )
	{
		final int[ ] result = new int[ values.size( ) ];
		int i = 0;
		for( final Integer value : values )
		{
			result[ i++ ] = value;
		}
		return result;
	}

	public static int[ ] toIntArray( Collection<Integer> values , int[ ] buffer )
	{
		if( buffer == null )
		{
			buffer = new int[ values.size( ) ];
		}
		int i = 0;
		for( final Integer value : values )
		{
			buffer[ i++ ] = value;
		}
		return buffer;
	}

	public static float[ ] toFloatArray( List<Float> values , float[ ] buffer )
	{
		if( buffer == null )
		{
			buffer = new float[ values.size( ) ];
		}
		int k = 0;
		for( float f : values )
		{
			buffer[ k++ ] = f;
		}
		return buffer;
	}

	public static ArrayList<Float> toFloatArrayList( float[ ] values )
	{
		ArrayList<Float> result = new ArrayList<Float>( );
		for( float value : values )
		{
			result.add( value );
		}
		return result;
	}
	
}
