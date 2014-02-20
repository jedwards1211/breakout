package org.andork.snakeyaml;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.andork.model.HasChangeSupport;
import org.andork.model.Model;
import org.andork.snakeyaml.YamlSpec.Attribute;
import org.andork.snakeyaml.YamlSpec.Format;
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
@SuppressWarnings( "serial" )
public final class YamlObject<S extends YamlSpec<S>> extends YamlElement implements Model
{
	private final S							spec;
	private final Map<Attribute<?>, Object>	attributes	= new LinkedHashMap<Attribute<?>, Object>( );
	
	/**
	 * Creates a {@code SpecObject} with the given spec.
	 * 
	 * @param spec
	 *            the {@link YamlSpec} for this SpecObject.
	 */
	private YamlObject( S spec )
	{
		this.spec = spec;
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
	
	/**
	 * Gets the value of an attribute.<br>
	 * <br>
	 * To see the list of attributes this SpecObject has, use {@link YamlSpec#getAttributes() getSpec().getAttributes()}.
	 * 
	 * @param Attribute
	 *            the attribute to get the value of.
	 * @return the value of the attribute (may be {@code null}).
	 */
	public <T> T get( Attribute<T> Attribute )
	{
		return ( T ) attributes.get( Attribute );
	}
	
	public <T> T set( Attribute<T> attribute , T newValue )
	{
		T oldValue = ( T ) attributes.get( attribute );
		if( oldValue instanceof YamlElement )
		{
			( ( YamlElement ) oldValue ).changeSupport( ).removePropertyChangeListener( propagator );
		}
		attributes.put( attribute , newValue );
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
		T oldValue = ( T ) attributes.get( attribute );
		if( attributes.containsKey( attribute ) )
		{
			if( oldValue instanceof YamlElement )
			{
				( ( YamlElement ) oldValue ).changeSupport( ).removePropertyChangeListener( propagator );
			}
			attributes.remove( attribute );
			changeSupport.firePropertyChange( this , attribute , oldValue , null );
		}
		return oldValue;
	}
	
	public boolean has( Attribute<?> attribute )
	{
		return attributes.containsKey( attribute );
	}
	
	public Map<String, Object> toYaml( )
	{
		Map<String, Object> result = new LinkedHashMap<String, Object>( );
		for( Map.Entry<Attribute<?>, Object> entry : attributes.entrySet( ) )
		{
			result.put( entry.getKey( ).getName( ) , ( ( Format ) entry.getKey( ).getFormat( ) ).format( entry.getValue( ) ) );
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
}
