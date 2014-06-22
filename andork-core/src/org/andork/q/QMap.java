package org.andork.q;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.andork.model.Model;
import org.andork.util.Java7;

public abstract class QMap<K, V, C extends Map<K, V>> extends QElement implements Map<K, V> , Model
{
	protected final C						map			= createMap( );
	transient volatile Set<K>				keySet		= null;
	transient volatile Collection<V>		values		= null;
	transient volatile Set<Map.Entry<K, V>>	entrySet	= null;
	
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
			if( prev != null )
			{
				removeChild( prev );
			}
			if( value != null )
			{
				addChild( value );
			}
			changeSupport.firePropertyChange( this , key , prev , value );
		}
		return prev;
	}
	
	@Override
	public V remove( Object key )
	{
		V prev = map.remove( key );
		if( prev != null )
		{
			removeChild( prev );
			changeSupport.firePropertyChange( this , key , prev , null );
		}
		return prev;
	}
	
	@Override
	public void putAll( Map<? extends K, ? extends V> m )
	{
		List<Object> added = new ArrayList<Object>( );
		List<Object> removed = new ArrayList<Object>( );
		
		for( Map.Entry<? extends K, ? extends V> entry : m.entrySet( ) )
		{
			K key = entry.getKey( );
			V value = entry.getValue( );
			V prev = map.put( key , value );
			if( prev != null )
			{
				removed.add( prev );
			}
			if( value != null )
			{
				added.add( prev );
			}
			if( prev != value )
			{
				changeSupport.firePropertyChange( this , key , prev , value );
			}
		}
		
		removeChildren( removed );
		addChildren( added );
	}
	
	@Override
	public void clear( )
	{
		Map<K, V> clone = new LinkedHashMap<K, V>( map );
		map.clear( );
		for( Map.Entry<K, V> entry : clone.entrySet( ) )
		{
			if( entry.getValue( ) != null )
			{
				changeSupport.firePropertyChange( this , entry.getKey( ) , entry.getValue( ) , null );
			}
		}
		clearChildren( );
	}
	
	@Override
	public Set<K> keySet( )
	{
		Set<K> ks = keySet;
		return( ks != null ? ks : ( keySet = new KeySet( ) ) );
	}
	
	@Override
	public Collection<V> values( )
	{
		Collection<V> vs = values;
		return( vs != null ? vs : ( values = new Values( ) ) );
	}
	
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet( )
	{
		Set<java.util.Map.Entry<K, V>> es = entrySet;
		return( es != null ? es : ( entrySet = new EntrySet( ) ) );
	}
	
	@Override
	public void set( Object key , Object newValue )
	{
		put( ( K ) key , ( V ) newValue );
	}
	
	private abstract class HashIterator<E> implements Iterator<E>
	{
		Iterator<Map.Entry<K, V>>	entryIter	= map.entrySet( ).iterator( );
		Map.Entry<K, V>				last;
		
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
			if( !( o instanceof Map.Entry ) )
				return false;
			Map.Entry<K, V> e = ( Map.Entry<K, V> ) o;
			V value = QMap.this.get( e.getKey( ) );
			return Java7.Objects.equals( value , e.getValue( ) );
		}
		
		public boolean remove( Object o )
		{
			if( o instanceof Map.Entry )
			{
				if( contains( o ) )
				{
					QMap.this.remove( ( ( Map.Entry ) o ).getKey( ) );
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
