/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.q2;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.andork.util.Java7;

public abstract class QMap<K, V, C extends Map<K, V>> extends QElement implements Map<K, V>
{
	protected final C map = createMap( );
	transient volatile Set<K> keySet = null;
	transient volatile Collection<V> values = null;
	transient volatile Set<Map.Entry<K, V>> entrySet = null;

	protected abstract C createMap( );

	@Override
	public int size( )
	{
		return map.size( );
	}

	@Override
	public boolean isEmpty( )
	{
		return map.isEmpty( );
	}

	@Override
	public boolean containsKey( Object key )
	{
		return map.containsKey( key );
	}

	@Override
	public boolean containsValue( Object value )
	{
		return map.containsValue( value );
	}

	@Override
	public V get( Object key )
	{
		return map.get( key );
	}

	@Override
	public V put( K key , V value )
	{
		V prev = map.put( key , value );
		if( prev != value )
		{
			fireMapChanged( key , prev , false , value );
		}
		return prev;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public V remove( Object key )
	{
		V prev = map.remove( key );
		if( prev != null )
		{
			fireMapChanged( ( K ) key , prev , true , null );
		}
		return prev;
	}

	@Override
	public void putAll( Map<? extends K, ? extends V> m )
	{
		List<K> keys = new ArrayList<>( );
		List<V> oldValues = new ArrayList<>( );
		List<Boolean> removed = new ArrayList<Boolean>( );
		List<V> newValues = new ArrayList<>( );

		for( Map.Entry<? extends K, ? extends V> entry : m.entrySet( ) )
		{
			K key = entry.getKey( );
			V newValue = entry.getValue( );
			V oldValue = map.put( key , newValue );

			if( oldValue != newValue )
			{
				keys.add( key );
				oldValues.add( oldValue );
				removed.add( false );
				newValues.add( newValue );
			}
		}

		fireMapChanged( keys , oldValues , removed , newValues );
	}

	@Override
	public void clear( )
	{
		List<K> oldKeys = new ArrayList<>( keySet( ) );
		List<V> oldValues = new ArrayList<>( values( ) );
		List<Boolean> removed = multiply( size( ) , true );
		List<V> newValues = multiply( size( ) , null );
		map.clear( );
		fireMapChanged( oldKeys , oldValues , removed , newValues );
	}

	@Override
	public Set<K> keySet( )
	{
		Set<K> ks = keySet;
		return ( ks != null ? ks : ( keySet = new KeySet( ) ) );
	}

	@Override
	public Collection<V> values( )
	{
		Collection<V> vs = values;
		return ( vs != null ? vs : ( values = new Values( ) ) );
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet( )
	{
		Set<java.util.Map.Entry<K, V>> es = entrySet;
		return ( es != null ? es : ( entrySet = new EntrySet( ) ) );
	}

	public void addListener( QMapListener<? super K, ? super V> listener )
	{
		super.addListener( listener );
	}

	public void removeListener( QMapListener<? super K, ? super V> listener )
	{
		super.removeListener( listener );
	}

	private static <T> List<T> multiply( int times , T elem )
	{
		List<T> result = new ArrayList<T>( times );
		for( int i = 0 ; i < times ; i++ )
		{
			result.add( elem );
		}
		return result;
	}

	@SuppressWarnings( "unchecked" )
	protected void fireMapChanged( K key , V oldValue , boolean removed , V newValue )
	{
		forEachListener( QMapListener.class , l -> l.mapChanged( this , key , oldValue , removed , newValue ) );
	}

	@SuppressWarnings( "unchecked" )
	protected void fireMapChanged( List<K> keys , List<V> oldValues , List<Boolean> removed ,
		List<V> newValues )
	{
		forEachListener( QMapListener.class , l -> l.mapChanged( this , keys , oldValues , removed , newValues ) );
	}

	private abstract class HashIterator<E> implements Iterator<E>
	{
		Iterator<Map.Entry<K, V>> entryIter = map.entrySet( ).iterator( );
		Map.Entry<K, V> last;

		@Override
		public boolean hasNext( )
		{
			return entryIter.hasNext( );
		}

		public Map.Entry<K, V> nextEntry( )
		{
			return last = entryIter.next( );
		}

		@Override
		public void remove( )
		{
			QMap.this.remove( last.getKey( ) );
		}
	}

	private class KeyIterator extends HashIterator<K>
	{
		@Override
		public K next( )
		{
			return nextEntry( ).getKey( );
		}
	}

	private class ValueIterator extends HashIterator<V>
	{
		@Override
		public V next( )
		{
			return nextEntry( ).getValue( );
		}
	}

	private class EntryIterator extends HashIterator<Map.Entry<K, V>>
	{
		@Override
		public Map.Entry<K, V> next( )
		{
			return nextEntry( );
		}
	}

	private final class KeySet extends AbstractSet<K>
	{
		public Iterator<K> iterator( )
		{
			return new KeyIterator( );
		}

		public int size( )
		{
			return QMap.this.size( );
		}

		public boolean contains( Object o )
		{
			return containsKey( o );
		}

		public boolean remove( Object o )
		{
			boolean removed = containsKey( o );
			QMap.this.remove( o );
			return removed;
		}

		public void clear( )
		{
			QMap.this.clear( );
		}
	}

	private final class Values extends AbstractCollection<V>
	{
		public Iterator<V> iterator( )
		{
			return new ValueIterator( );
		}

		public int size( )
		{
			return QMap.this.size( );
		}

		public boolean contains( Object o )
		{
			return containsValue( o );
		}

		public void clear( )
		{
			QMap.this.clear( );
		}
	}

	private final class EntrySet extends AbstractSet<Map.Entry<K, V>>
	{
		public Iterator<Map.Entry<K, V>> iterator( )
		{
			return new EntryIterator( );
		}

		public boolean contains( Object o )
		{
			if( ! ( o instanceof Map.Entry ) )
				return false;
			Map.Entry<?, ?> e = ( Map.Entry<?, ?> ) o;
			Object value = QMap.this.get( e.getKey( ) );
			return Java7.Objects.equals( value , e.getValue( ) );
		}

		public boolean remove( Object o )
		{
			if( o instanceof Map.Entry )
			{
				if( contains( o ) )
				{
					QMap.this.remove( ( ( Map.Entry<?, ?> ) o ).getKey( ) );
					return true;
				}
			}
			return false;
		}

		public int size( )
		{
			return QMap.this.size( );
		}

		public void clear( )
		{
			QMap.this.clear( );
		}
	}
}
