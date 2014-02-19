package org.andork.spec.json;

import java.util.Collections;
import java.util.List;

import org.andork.spec.json.JsonSpec.Format;
import org.andork.util.Java7;
import org.andork.util.Java7;

public abstract class JsonSpecList<E> extends JsonSpecCollection<E>
{
	
	protected JsonSpecList( Format<? super E> format )
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
			if( oldValue instanceof JsonSpecElement )
			{
				( ( JsonSpecElement ) oldValue ).changeSupport( ).removePropertyChangeListener( propagator );
			}
			( ( List<E> ) collection ).set( index , element );
			if( element instanceof JsonSpecElement )
			{
				( ( JsonSpecElement ) element ).changeSupport( ).addPropertyChangeListener( propagator );
			}
			changeSupport.fireChildRemoved( this , oldValue );
			changeSupport.fireChildAdded( this , element );
		}
		return oldValue;
	}
}
