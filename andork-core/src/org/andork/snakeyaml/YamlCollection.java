package org.andork.snakeyaml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.andork.func.Bimapper;

public abstract class YamlCollection<E> extends YamlElement implements Iterable<E>
{
	protected final Bimapper<? super E, Object>	format;
	protected final Collection<E>				collection;
	
	protected YamlCollection( Bimapper<? super E, Object> format )
	{
		this.format = format;
		this.collection = createCollection( );
	}
	
	protected abstract Collection<E> createCollection( );
	
	public boolean add( E element )
	{
		if( collection.add( element ) )
		{
			if( element instanceof YamlElement )
			{
				( ( YamlElement ) element ).changeSupport( ).addPropertyChangeListener( propagator );
			}
			changeSupport.fireChildAdded( this , element );
			return true;
		}
		return false;
	}
	
	public int size( )
	{
		return collection.size( );
	}
	
	public boolean remove( E element )
	{
		if( collection.remove( element ) )
		{
			if( element instanceof YamlElement )
			{
				( ( YamlElement ) element ).changeSupport( ).removePropertyChangeListener( propagator );
			}
			changeSupport.fireChildRemoved( this , element );
			return true;
		}
		return false;
	}
	
	public void clear( )
	{
		for( E element : collection )
		{
			if( element instanceof YamlElement )
			{
				( ( YamlElement ) element ).changeSupport( ).removePropertyChangeListener( propagator );
			}
		}
		collection.clear( );
		changeSupport.fireChildrenChanged( this );
	}
	
	public List<Object> toYaml( )
	{
		List<Object> array = new ArrayList<Object>( );
		for( E element : collection )
		{
			array.add( format.map( element ) );
		}
		return array;
	}
	
	public static void fromYaml( List<?> array , YamlCollection collection ) throws Exception
	{
		for( Object elem : array )
		{
			collection.add( collection.format.unmap( elem ) );
		}
	}
	
	public Iterator<E> iterator( )
	{
		return new Iterator<E>( )
		{
			Iterator<E>	wrapped	= collection.iterator( );
			E			last;
			
			@Override
			public boolean hasNext( )
			{
				return wrapped.hasNext( );
			}
			
			@Override
			public E next( )
			{
				return last = wrapped.next( );
			}
			
			@Override
			public void remove( )
			{
				wrapped.remove( );

				if( last instanceof YamlElement )
				{
					( ( YamlElement ) last ).changeSupport( ).removePropertyChangeListener( propagator );
				}
				changeSupport.fireChildRemoved( this , last );
			}
		};
	}

	public boolean isEmpty( )
	{
		return collection.isEmpty( );
	}
}
