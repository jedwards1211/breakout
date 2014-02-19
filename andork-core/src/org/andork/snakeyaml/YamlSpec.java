package org.andork.snakeyaml;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for {@link YamlObject} format specifications. To create a specification for a new type, simply create a subclass that calls one of the
 * constructors with the appropriate {@link Attribute}s and child specs.
 * 
 * @author james.a.edwards
 */
public abstract class YamlSpec<S extends YamlSpec<S>>
{
	private final Map<String, Attribute<?>>	attributes	= new LinkedHashMap<String, Attribute<?>>( );
	
	/**
	 * Creates a Spec with the attributes.
	 * 
	 * @param attributes
	 *            the attributes.
	 */
	protected YamlSpec( Attribute<?> ... attributes )
	{
		if( attributes != null )
		{
			for( Attribute<?> Attribute : attributes )
			{
				this.attributes.put( Attribute.getName( ) , Attribute );
			}
		}
	}
	
	public YamlObject<S> fromYaml( Map<?, ?> json ) throws Exception
	{
		YamlObject<S> result = newObject( );
		
		for( Map.Entry<?, ?> entry : json.entrySet( ) )
		{
			Attribute attrib = getAttribute( entry.getKey( ).toString( ) );
			if( attrib != null )
			{
				result.set( attrib , attrib.getFormat( ).parse( entry.getValue( ) ) );
			}
		}
		
		return result;
	}
	
	public YamlObject<S> newObject( )
	{
		return YamlObject.newInstance( ( S ) this );
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
		public Object format( T t );
		
		public T parse( Object s ) throws Exception;
	}
	
	/**
	 * Defines an attribute that can be parsed by {@link YamlObject#parseXml(String)}.
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
		public Object format( String t )
		{
			return t == null ? null : t;
		}
		
		public String parse( Object s )
		{
			return s == null ? null : s.toString( );
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
		public Object format( E t )
		{
			return t == null ? null : t.name( );
		}
		
		@Override
		public E parse( Object s ) throws Exception
		{
			return s == null || s == null ? null : Enum.valueOf( cls , s.toString( ) );
		}
	}
	
	public static class BooleanFormat implements Format<Boolean>
	{
		public Object format( Boolean t )
		{
			return t;
		}
		
		public Boolean parse( Object s )
		{
			return s == null ? null : Boolean.valueOf( s.toString( ) );
		}
	}
	
	public static class IntegerFormat implements Format<Integer>
	{
		public Object format( Integer t )
		{
			return t;
		}
		
		public Integer parse( Object s )
		{
			return s == null ? null : Integer.valueOf( s.toString( ) );
		}
	}
	
	public static class LongFormat implements Format<Long>
	{
		public Object format( Long t )
		{
			return t;
		}
		
		public Long parse( Object s )
		{
			return s == null ? null : Long.valueOf( s.toString( ) );
		}
	}
	
	public static class FloatFormat implements Format<Float>
	{
		public Object format( Float t )
		{
			return t;
		}
		
		public Float parse( Object s )
		{
			return s == null ? null : Float.valueOf( s.toString( ) );
		}
	}
	
	public static class DoubleFormat implements Format<Double>
	{
		public Object format( Double t )
		{
			return t;
		}
		
		public Double parse( Object s )
		{
			return s == null ? null : Double.valueOf( s.toString( ) );
		}
	}
	
	public static class BigIntegerFormat implements Format<BigInteger>
	{
		public Object format( BigInteger t )
		{
			return t;
		}
		
		public BigInteger parse( Object s )
		{
			return s == null ? null : new BigInteger( s.toString( ) );
		}
	}
	
	public static class BigDecimalFormat implements Format<BigDecimal>
	{
		public Object format( BigDecimal t )
		{
			return t;
		}
		
		public BigDecimal parse( Object s )
		{
			return s == null ? null : new BigDecimal( s.toString( ) );
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
		public synchronized Object format( Date t )
		{
			return t == null ? null : format.format( t );
		}
		
		@Override
		public synchronized Date parse( Object s ) throws Exception
		{
			return s == null ? null : format.parse( s.toString( ) );
		}
		
	}
	
	public static class SpecObjectFormat<S extends YamlSpec<S>> implements Format<YamlObject<S>>
	{
		S	spec;
		
		private SpecObjectFormat( S spec )
		{
			super( );
			this.spec = spec;
		}
		
		public static <S extends YamlSpec<S>> SpecObjectFormat<S> newInstance( S spec )
		{
			return new SpecObjectFormat<S>( spec );
		}
		
		@Override
		public Object format( YamlObject<S> t )
		{
			return t == null ? null : t.toYaml( );
		}
		
		@Override
		public YamlObject<S> parse( Object s ) throws Exception
		{
			return s == null ? null : spec.fromYaml( ( Map<?, ?> ) s );
		}
	}
	
	public static class SpecArrayListFormat<E> implements Format<YamlArrayList<E>>
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
		public Object format( YamlArrayList<E> t )
		{
			return t == null ? null : t.toYaml( );
		}
		
		@Override
		public YamlArrayList<E> parse( Object s ) throws Exception
		{
			return s == null || s == null ? null : YamlArrayList.fromYaml( ( List<?> ) s , format );
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
	
	public static <S extends YamlSpec<S>> Attribute<YamlObject<S>> yamlObjectAttribute( String name , S spec )
	{
		return new Attribute<YamlObject<S>>( name , SpecObjectFormat.newInstance( spec ) );
	}
	
	public static <E> Attribute<YamlArrayList<E>> yamlArrayListAttribute( String name , Format<? super E> format )
	{
		return new Attribute<YamlArrayList<E>>( name , SpecArrayListFormat.newInstance( format ) );
	}
}
