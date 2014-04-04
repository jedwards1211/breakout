package org.andork.snakeyaml;

import java.util.Collections;
import java.util.List;

import org.andork.func.Bimapper;
import org.andork.util.Java7;

public abstract class YamlList<E> extends YamlCollection<E>
{
	
	protected YamlList( Bimapper<? super E, Object> format )
	{
		super( format );
	}
	
	public List<E> getList( )
	{
		return Collections.unmodifiableList( ( List<E> ) collection );
	}
	
	public E get( int index )
	{
		return ( ( List<E> ) collection ).get( index );
	}
	
	public E set( int index , E element )
	{
		E oldValue = ( ( List<E> ) collection ).get( index );
		if( !Java7.Objects.equals( oldValue , element ) )
		{
			if( oldValue instanceof YamlElement )
			{
				( ( YamlElement ) oldValue ).changeSupport( ).removePropertyChangeListener( propagator );
			}
			( ( List<E> ) collection ).set( index , element );
			if( element instanceof YamlElement )
			{
				( ( YamlElement ) element ).changeSupport( ).addPropertyChangeListener( propagator );
			}
			changeSupport.fireChildRemoved( this , oldValue );
			changeSupport.fireChildAdded( this , element );
		}
		return oldValue;
	}
	
	public void add( int index , E element )
	{
		( ( List<E> ) collection ).add( index , element );
		if( element instanceof YamlElement )
		{
			( ( YamlElement ) element ).changeSupport( ).addPropertyChangeListener( propagator );
		}
		changeSupport.fireChildAdded( this , element );
	}
	
	public E remove( int index )
	{
		E removed = ( ( List<E> ) collection ).remove( index );
		if( removed instanceof YamlElement )
		{
			( ( YamlElement ) removed ).changeSupport( ).removePropertyChangeListener( propagator );
		}
		changeSupport.fireChildRemoved( this , removed );
		return removed;
	}
	
}
