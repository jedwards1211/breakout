package org.andork.snakeyaml;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.andork.model.Model;
import org.andork.snakeyaml.YamlSpec.Attribute;
import org.andork.util.Java7;

/**
 * A type-safe property set that can be converted to JSON via {@link #toYaml()} and converted from JSON via {@link YamlSpec#fromJson(JsonObject)}.<br>
 * <br>
 * 
 * @param <S>
 *            the type of the {@link YamlSpec} for this {@code SpecObject}.
 * 
 * @author james.a.edwards
 */
public final class YamlObject<S extends YamlSpec<S>> extends YamlElement implements Model
{
	private final S				spec;
	private final Object[ ]		attributes;
	
	private static final Object	NOT_PRESENT	= new Object( );
	
	/**
	 * Creates a {@code SpecObject} with the given spec.
	 * 
	 * @param spec
	 *            the {@link YamlSpec} for this SpecObject.
	 */
	private YamlObject( S spec )
	{
		this.spec = spec;
		this.attributes = new Object[ spec.getAttributeCount( ) ];
		Arrays.fill( this.attributes , NOT_PRESENT );
	}
	
	public static <S extends YamlSpec<S>> YamlObject<S> newInstance( S spec )
	{
		return new YamlObject<S>( spec );
	}
	
	/**
	 * @return the {@link YamlSpec} of this SpecObject.
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
	 * To see the list of attributes this SpecObject has, use {@link YamlSpec#getAttributes() getSpec().getAttributes()}.
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
		if( oldValue instanceof YamlElement )
		{
			( ( YamlElement ) oldValue ).changeSupport( ).removePropertyChangeListener( propagator );
		}
		attributes[ attribute.index ] = newValue;
		if( newValue instanceof YamlElement )
		{
			( ( YamlElement ) newValue ).changeSupport( ).addPropertyChangeListener( propagator );
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
		if( oldValue instanceof YamlElement )
		{
			( ( YamlElement ) oldValue ).changeSupport( ).removePropertyChangeListener( propagator );
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
	
	public Map<String, Object> toYaml( )
	{
		Map<String, Object> result = new LinkedHashMap<String, Object>( );
		for( int i = 0 ; i < spec.getAttributeCount( ) ; i++ )
		{
			if( attributes[ i ] != NOT_PRESENT )
			{
				result.put( spec.attributeAt( i ).getName( ) , ( ( Attribute ) spec.attributeAt( i ) ).format.map( attributes[ i ] ) );
			}
		}
		return result;
	}
	
	public String toString( )
	{
		return toYaml( ).toString( );
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
	
	public YamlObject<S> deepClone( )
	{
		YamlObject<S> result = spec.newObject( );
		for( int i = 0 ; i < attributes.length ; i++ )
		{
			if( attributes[ i ] instanceof YamlElement )
			{
				result.attributes[ i ] = ( ( YamlElement ) attributes[ i ] ).deepClone( );
			}
			else
			{
				result.attributes[ i ] = attributes[ i ];
			}
		}
		return result;
	}
}
