package org.andork.q;

import java.util.Arrays;

import org.andork.func.Mapper;
import org.andork.model.Model;
import org.andork.q.QSpec.Attribute;
import org.andork.util.Java7;

public final class QObject<S extends QSpec<S>> extends QElement implements Model
{
	private final S				spec;
	private final Object[ ]		attributes;
	
	private static final Object	NOT_PRESENT	= new Object( );
	
	/**
	 * Creates a {@code QObject} with the given spec.
	 * 
	 * @param spec
	 *            the {@link QSpec} for this QObject.
	 */
	private QObject( S spec )
	{
		this.spec = spec;
		this.attributes = new Object[ spec.getAttributeCount( ) ];
		Arrays.fill( this.attributes , NOT_PRESENT );
	}
	
	public static <S extends QSpec<S>> QObject<S> newInstance( S spec )
	{
		return new QObject<S>( spec );
	}
	
	/**
	 * @return the {@link QSpec} of this QObject.
	 */
	public S getSpec( )
	{
		return spec;
	}
	
	private <T> void checkBelongs( Attribute<T> attribute )
	{
		if( spec.attributes[ attribute.index ] != attribute )
		{
			throw new IllegalArgumentException( "attribute does not belong to this spec" );
		}
	}
	
	public Object valueAt( int index )
	{
		return attributes[ index ] == NOT_PRESENT ? null : attributes[ index ];
	}
	
	public void setValueAt( int index , Object value )
	{
		attributes[ index ] = value;
	}
	
	/**
	 * Gets the value of an attribute.<br>
	 * <br>
	 * To see the list of attributes this QObject has, use {@link QSpec#getAttributes() getSpec().getAttributes()}.
	 * 
	 * @param attribute
	 *            the attribute to get the value of.
	 * @return the value of the attribute (may be {@code null}).
	 */
	public <T> T get( Attribute<T> attribute )
	{
		checkBelongs( attribute );
		return attributes[ attribute.index ] == NOT_PRESENT ? null : ( T ) attributes[ attribute.index ];
	}
	
	public <T> T set( Attribute<T> attribute , T newValue )
	{
		checkBelongs( attribute );
		T oldValue = ( T ) attributes[ attribute.index ];
		if( oldValue == NOT_PRESENT )
		{
			oldValue = null;
		}
		if( oldValue instanceof QElement )
		{
			( ( QElement ) oldValue ).changeSupport( ).removePropertyChangeListener( propagator );
		}
		attributes[ attribute.index ] = newValue;
		if( newValue instanceof QElement )
		{
			( ( QElement ) newValue ).changeSupport( ).addPropertyChangeListener( propagator );
		}
		if( !Java7.Objects.equals( oldValue , newValue ) )
		{
			changeSupport.firePropertyChange( this , attribute , oldValue , newValue );
		}
		return oldValue;
	}
	
	public <T> T remove( Attribute<T> attribute )
	{
		checkBelongs( attribute );
		Object oldValue = attributes[ attribute.index ];
		if( oldValue == NOT_PRESENT )
		{
			return null;
		}
		if( oldValue instanceof QElement )
		{
			( ( QElement ) oldValue ).changeSupport( ).removePropertyChangeListener( propagator );
		}
		attributes[ attribute.index ] = NOT_PRESENT;
		changeSupport.firePropertyChange( this , attribute , oldValue , null );
		return ( T ) oldValue;
	}
	
	public boolean has( Attribute<?> attribute )
	{
		checkBelongs( attribute );
		return attributes[ attribute.index ] != NOT_PRESENT;
	}
	
	public String toString( )
	{
		StringBuilder builder = new StringBuilder( );
		builder.append( '{' );
		for( int i = 0 ; i < attributes.length ; i++ )
		{
			if( attributes[ i ] != NOT_PRESENT )
			{
				if( builder.length( ) > 1 )
				{
					builder.append( ',' );
				}
				builder.append( '"' ).append( spec.attributes[ i ].getName( ) ).append( "\":" );
				builder.append( String.valueOf( attributes[ i ] ) );
			}
		}
		builder.append( '}' );
		return builder.toString( );
	}
	
	@Override
	public Object get( Object key )
	{
		return get( ( Attribute ) key );
	}
	
	@Override
	public void set( Object key , Object newValue )
	{
		set( ( Attribute ) key , newValue );
	}
	
	@Override
	public QObject<S> deepClone( Mapper<Object, Object> childMapper )
	{
		QObject<S> result = spec.newObject( );
		for( int i = 0 ; i < attributes.length ; i++ )
		{
			if( attributes[ i ] != NOT_PRESENT )
			{
				result.set( spec.attributeAt( i ) , childMapper.map( attributes[ i ] ) );
			}
		}
		return result;
	}
}
