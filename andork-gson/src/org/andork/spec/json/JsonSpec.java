package org.andork.spec.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * Base class for {@link JsonSpecObject} format specifications. To create a specification for a new type, simply create a subclass that calls one of the
 * constructors with the appropriate {@link Attribute}s and child specs.
 * 
 * @author james.a.edwards
 */
public abstract class JsonSpec<S extends JsonSpec<S>>
{
	private final Map<String, Attribute<?>>	attributes	= new HashMap<String, Attribute<?>>( );
	
	/**
	 * Creates a Spec with the attributes.
	 * 
	 * @param attributes
	 *            the attributes.
	 */
	protected JsonSpec( Attribute<?> ... attributes )
	{
		if( attributes != null )
		{
			for( Attribute<?> Attribute : attributes )
			{
				this.attributes.put( Attribute.getName( ) , Attribute );
			}
		}
	}
	
	public JsonSpecObject<S> fromJson( JsonObject json ) throws Exception
	{
		JsonSpecObject<S> result = newObject( );
		
		for( Map.Entry<String, JsonElement> entry : json.entrySet( ) )
		{
			Attribute attrib = getAttribute( entry.getKey( ) );
			if( attrib != null )
			{
				result.set( attrib , attrib.getFormat( ).parse( entry.getValue( ) ) );
			}
		}
		
		return result;
	}
	
	public JsonSpecObject<S> newObject( )
	{
		return JsonSpecObject.newInstance( ( S ) this );
	}
	
	/**
	 * Converts an attribute value to and from {@link String} for JSON.
	 * 
	 * @author james.a.edwards
	 * 
	 * @param <T>
	 *            the type of the attribute value.
	 */
	public static interface Format<T>
	{
		public JsonElement format( T t );
		
		public T parse( JsonElement s ) throws Exception;
	}
	
	/**
	 * Defines an attribute that can be parsed by {@link JsonSpecObject#parseXml(String)}.
	 * 
	 * @author james.a.edwards
	 * 
	 * @param <T>
	 *            the type of the attribute value.
	 */
	public static class Attribute<T>
	{
		private final String	name;
		private final Format<T>	format;
		
		/**
		 * Creates an attribute with the given name and format.
		 * 
		 * @param name
		 *            the name used in XML.
		 * @param format
		 *            the format for converting from the attribute value to and from a {@code String} for XML.
		 */
		public Attribute( String name , Format<T> format )
		{
			super( );
			this.name = name;
			this.format = format;
		}
		
		/**
		 * Gets the attribute name used in XML.
		 * 
		 * @return the attribute name.
		 */
		public String getName( )
		{
			return name;
		}
		
		/**
		 * Gets the format for converting from the attribute value to and from a {@code String} for XML.
		 * 
		 * @return the format.
		 */
		public Format<T> getFormat( )
		{
			return format;
		}
	}
	
	public static class StringFormat implements Format<String>
	{
		public JsonElement format( String t )
		{
			return t == null ? JsonNull.INSTANCE : new JsonPrimitive( t );
		}
		
		public String parse( JsonElement s )
		{
			return s == null || s == JsonNull.INSTANCE ? null : s.getAsString( );
		}
	}
	
	public static class EnumFormat<E extends Enum<E>> implements Format<E>
	{
		Class<E>	cls;
		
		private EnumFormat( Class<E> cls )
		{
			this.cls = cls;
		}
		
		public static <E extends Enum<E>> EnumFormat<E> newInstance( Class<E> cls )
		{
			return new EnumFormat<E>( cls );
		}
		
		@Override
		public JsonElement format( E t )
		{
			return t == null ? JsonNull.INSTANCE : new JsonPrimitive( t.name( ) );
		}
		
		@Override
		public E parse( JsonElement s ) throws Exception
		{
			return s == null || s == JsonNull.INSTANCE ? null : Enum.valueOf( cls , s.getAsString( ) );
		}
	}
	
	public static class BooleanFormat implements Format<Boolean>
	{
		public JsonElement format( Boolean t )
		{
			return t == null ? JsonNull.INSTANCE : new JsonPrimitive( t );
		}
		
		public Boolean parse( JsonElement s )
		{
			return s == null || s == JsonNull.INSTANCE ? null : s.getAsBoolean( );
		}
	}
	
	public static class IntegerFormat implements Format<Integer>
	{
		public JsonElement format( Integer t )
		{
			return t == null ? JsonNull.INSTANCE : new JsonPrimitive( t );
		}
		
		public Integer parse( JsonElement s )
		{
			return s == null || s == JsonNull.INSTANCE ? null : s.getAsInt( );
		}
	}
	
	public static class LongFormat implements Format<Long>
	{
		public JsonElement format( Long t )
		{
			return t == null ? JsonNull.INSTANCE : new JsonPrimitive( t );
		}
		
		public Long parse( JsonElement s )
		{
			return s == null || s == JsonNull.INSTANCE ? null : s.getAsLong( );
		}
	}
	
	public static class FloatFormat implements Format<Float>
	{
		public JsonElement format( Float t )
		{
			return t == null ? JsonNull.INSTANCE : new JsonPrimitive( t );
		}
		
		public Float parse( JsonElement s )
		{
			return s == null || s == JsonNull.INSTANCE ? null : s.getAsFloat( );
		}
	}
	
	public static class DoubleFormat implements Format<Double>
	{
		public JsonElement format( Double t )
		{
			return t == null ? JsonNull.INSTANCE : new JsonPrimitive( t );
		}
		
		public Double parse( JsonElement s )
		{
			return s == null || s == JsonNull.INSTANCE ? null : s.getAsDouble( );
		}
	}
	
	public static class BigIntegerFormat implements Format<BigInteger>
	{
		public JsonElement format( BigInteger t )
		{
			return t == null ? JsonNull.INSTANCE : new JsonPrimitive( t );
		}
		
		public BigInteger parse( JsonElement s )
		{
			return s == null || s == JsonNull.INSTANCE ? null : s.getAsBigInteger( );
		}
	}
	
	public static class BigDecimalFormat implements Format<BigDecimal>
	{
		public JsonElement format( BigDecimal t )
		{
			return t == null ? JsonNull.INSTANCE : new JsonPrimitive( t );
		}
		
		public BigDecimal parse( JsonElement s )
		{
			return s == null || s == JsonNull.INSTANCE ? null : s.getAsBigDecimal( );
		}
	}
	
	public static class DateFormat implements Format<Date>
	{
		java.text.DateFormat	format;
		
		public DateFormat( java.text.DateFormat format )
		{
			this.format = format;
		}
		
		public DateFormat( String simpleDateFormatPattern )
		{
			this( new SimpleDateFormat( simpleDateFormatPattern ) );
		}
		
		@Override
		public synchronized JsonElement format( Date t )
		{
			return t == null ? JsonNull.INSTANCE : new JsonPrimitive( format.format( t ) );
		}
		
		@Override
		public synchronized Date parse( JsonElement s ) throws Exception
		{
			return s == null || s == JsonNull.INSTANCE ? null : format.parse( s.getAsString( ) );
		}
		
	}
	
	public static class SpecObjectFormat<S extends JsonSpec<S>> implements Format<JsonSpecObject<S>>
	{
		S	spec;
		
		private SpecObjectFormat( S spec )
		{
			super( );
			this.spec = spec;
		}
		
		public static <S extends JsonSpec<S>> SpecObjectFormat<S> newInstance( S spec )
		{
			return new SpecObjectFormat<S>( spec );
		}
		
		@Override
		public JsonElement format( JsonSpecObject<S> t )
		{
			return t == null ? JsonNull.INSTANCE : t.toJson( );
		}
		
		@Override
		public JsonSpecObject<S> parse( JsonElement s ) throws Exception
		{
			return s == null || s == JsonNull.INSTANCE ? null : spec.fromJson( ( JsonObject ) s );
		}
	}
	
	public static class SpecObjectStringFormat<S extends JsonSpec<S>> implements org.andork.util.Format<JsonSpecObject<S>>
	{
		S			spec;
		JsonParser	parser	= new JsonParser( );
		
		public SpecObjectStringFormat( S spec )
		{
			super( );
			this.spec = spec;
		}
		
		public static <S extends JsonSpec<S>> SpecObjectFormat<S> newInstance( S spec )
		{
			return new SpecObjectFormat<S>( spec );
		}
		
		@Override
		public String format( JsonSpecObject<S> t )
		{
			return t == null ? null : t.toJson( ).toString( );
		}
		
		@Override
		public JsonSpecObject<S> parse( String s ) throws Exception
		{
			if( s == null || "".equals( s ) )
			{
				return spec.newObject( );
			}
			return s == null || "".equals( s ) ? null : spec.fromJson( ( JsonObject ) parser.parse( s ) );
		}
	}
	
	public static class SpecArrayListFormat<E> implements Format<JsonSpecArrayList<E>>
	{
		Format<? super E>	format;
		
		private SpecArrayListFormat( Format<? super E> format )
		{
			super( );
			this.format = format;
		}
		
		public static <E> SpecArrayListFormat<E> newInstance( Format<? super E> format )
		{
			return new SpecArrayListFormat<E>( format );
		}
		
		@Override
		public JsonElement format( JsonSpecArrayList<E> t )
		{
			return t == null ? JsonNull.INSTANCE : t.toJson( );
		}
		
		@Override
		public JsonSpecArrayList<E> parse( JsonElement s ) throws Exception
		{
			return s == null || s == JsonNull.INSTANCE ? null : JsonSpecArrayList.fromJson( ( JsonArray ) s , format );
		}
	}
	
	/**
	 * Gets the {@link Attribute} with the given name.
	 * 
	 * @param name
	 *            the name of the attribute to get.
	 * @return the {@code Attribute} with the given name, or {@code null} if none is part of this spec.
	 */
	public Attribute<?> getAttribute( String name )
	{
		return attributes.get( name );
	}
	
	/**
	 * Gets all {@link Attribute}s in this spec.
	 * 
	 * @return an unmodifiable {@link Collection} of {@code Attribute}s.
	 */
	public Collection<Attribute<?>> getAttributes( )
	{
		return Collections.unmodifiableCollection( attributes.values( ) );
	}
	
	public static Attribute<String> stringAttribute( String name )
	{
		return new Attribute<String>( name , new StringFormat( ) );
	}
	
	public static <E extends Enum<E>> Attribute<E> enumAttribute( String name , Class<E> cls )
	{
		return new Attribute<E>( name , EnumFormat.newInstance( cls ) );
	}
	
	public static Attribute<Boolean> booleanAttribute( String name )
	{
		return new Attribute<Boolean>( name , new BooleanFormat( ) );
	}
	
	public static Attribute<Integer> integerAttribute( String name )
	{
		return new Attribute<Integer>( name , new IntegerFormat( ) );
	}
	
	public static Attribute<Long> longAttribute( String name )
	{
		return new Attribute<Long>( name , new LongFormat( ) );
	}
	
	public static Attribute<Float> floatAttribute( String name )
	{
		return new Attribute<Float>( name , new FloatFormat( ) );
	}
	
	public static Attribute<Double> doubleAttribute( String name )
	{
		return new Attribute<Double>( name , new DoubleFormat( ) );
	}
	
	public static Attribute<BigInteger> bigIntegerAttribute( String name )
	{
		return new Attribute<BigInteger>( name , new BigIntegerFormat( ) );
	}
	
	public static Attribute<BigDecimal> bigDecimalAttribute( String name )
	{
		return new Attribute<BigDecimal>( name , new BigDecimalFormat( ) );
	}
	
	public static <S extends JsonSpec<S>> Attribute<JsonSpecObject<S>> specObjectAttribute( String name , S spec )
	{
		return new Attribute<JsonSpecObject<S>>( name , SpecObjectFormat.newInstance( spec ) );
	}
	
	public static <E> Attribute<JsonSpecArrayList<E>> specArrayListAttribute( String name , Format<? super E> format )
	{
		return new Attribute<JsonSpecArrayList<E>>( name , SpecArrayListFormat.newInstance( format ) );
	}
}
