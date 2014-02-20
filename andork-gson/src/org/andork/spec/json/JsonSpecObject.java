package org.andork.spec.json;

import java.util.HashMap;
import java.util.Map;

import org.andork.model.HasChangeSupport;
import org.andork.spec.json.JsonSpec.Attribute;
import org.andork.spec.json.JsonSpec.Format;
import org.andork.util.Java7;

import com.google.gson.JsonObject;

/**
 * A type-safe property set that can be converted to JSON via {@link #toJson()} and converted from JSON via {@link JsonSpec#fromJson(JsonObject)}.<br>
 * <br>
 * 
 * @param <S>
 *            the type of the {@link JsonSpec} for this {@code SpecObject}.
 * 
 * @author james.a.edwards
 */
@SuppressWarnings( "serial" )
public final class JsonSpecObject<S extends JsonSpec<S>> extends JsonSpecElement implements HasChangeSupport
{
	private final S							spec;
	private final Map<Attribute<?>, Object>	attributes	= new HashMap<Attribute<?>, Object>( );
	
	/**
	 * Creates a {@code SpecObject} with the given spec.
	 * 
	 * @param spec
	 *            the {@link JsonSpec} for this SpecObject.
	 */
	private JsonSpecObject( S spec )
	{
		this.spec = spec;
	}
	
	public static <S extends JsonSpec<S>> JsonSpecObject<S> newInstance( S spec )
	{
		return new JsonSpecObject<S>( spec );
	}
	
	/**
	 * @return the {@link JsonSpec} of this SpecObject.
	 */
	public S getSpec( )
	{
		return spec;
	}
	
	/**
	 * Gets the value of an attribute.<br>
	 * <br>
	 * To see the list of attributes this SpecObject has, use {@link JsonSpec#getAttributes() getSpec().getAttributes()}.
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
		if( oldValue instanceof JsonSpecElement )
		{
			( ( JsonSpecElement ) oldValue ).changeSupport( ).removePropertyChangeListener( propagator );
		}
		attributes.put( attribute , newValue );
		if( newValue instanceof JsonSpecElement )
		{
			( ( JsonSpecElement ) newValue ).changeSupport( ).addPropertyChangeListener( propagator );
		}
		if( !Java7.Objects.equals( oldValue , newValue ) )
		{
			changeSupport.firePropertyChange( this , attribute , oldValue , newValue );
		}
		return oldValue;
	}
	
	public boolean has( Attribute<?> attribute )
	{
		return attributes.containsKey( attribute );
	}
	
	public JsonObject toJson( )
	{
		JsonObject result = new JsonObject( );
		for( Map.Entry<Attribute<?>, Object> entry : attributes.entrySet( ) )
		{
			result.add( entry.getKey( ).getName( ) , ( ( Format ) entry.getKey( ).getFormat( ) ).format( entry.getValue( ) ) );
		}
		return result;
	}
	
	public String toString( )
	{
		return toJson( ).toString( );
	}
}
