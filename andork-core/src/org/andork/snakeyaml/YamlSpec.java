package org.andork.snakeyaml;

import java.awt.Color;
import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.andork.func.Bimapper;
import org.andork.func.FileStringBimapper;
import org.andork.reflect.ReflectionUtils;

/**
 * Base class for {@link YamlObject} format specifications. To create a specification for a new type, simply create a subclass that calls one of the
 * constructors with the appropriate {@link Attribute}s and child specs.
 * 
 * @author james.a.edwards
 */
public abstract class YamlSpec<S extends YamlSpec<S>>
{
	Attribute<?>[ ]					attributes;
	final Map<String, Attribute<?>>	attributesByName	= new LinkedHashMap<String, Attribute<?>>( );
	
	protected YamlSpec( )
	{
		for( Field field : ReflectionUtils.getStaticFieldList( getClass( ) , true ) )
		{
			if( field.getType( ) == Attribute.class )
			{
				try
				{
					Attribute<?> attr = ( Attribute<?> ) field.get( null );
					attributesByName.put( attr.getName( ) , attr );
				}
				catch( Exception ex )
				{
					// Shouldn't happen...
				}
			}
		}
		this.attributes = attributesByName.values( ).toArray( new Attribute[ attributesByName.size( ) ] );
		
		for( int i = 0 ; i < attributes.length ; i++ )
		{
			if( attributes[ i ].index < 0 )
			{
				attributes[ i ].index = i;
			}
			else if( attributes[ i ].index != i )
			{
				throw new IllegalStateException( "attribute order conflicts with another spec" );
			}
		}
		
	}
	
	/**
	 * Creates a Spec with all attributes defined in static fields in the implementing class and its superclasses.
	 */
	protected YamlSpec( Attribute<?> ... attributes )
	{
		for( Attribute<?> attr : attributes )
		{
			attributesByName.put( attr.getName( ) , attr );
		}
		this.attributes = Arrays.copyOf( attributes , attributes.length );
		
		for( int i = 0 ; i < attributes.length ; i++ )
		{
			if( attributes[ i ].index < 0 )
			{
				attributes[ i ].index = i;
			}
			else if( attributes[ i ].index != i )
			{
				throw new IllegalStateException( "attribute order conflicts with another spec" );
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
				try
				{
					result.set( attrib , attrib.getBimapper( ).unmap( entry.getValue( ) ) );
				}
				catch( Exception ex )
				{
					ex.printStackTrace( );
				}
			}
		}
		
		return result;
	}
	
	public YamlObject<S> newObject( )
	{
		return YamlObject.newInstance( ( S ) this );
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
		return attributesByName.get( name );
	}
	
	/**
	 * Gets all {@link Attribute}s in this spec.
	 * 
	 * @return an unmodifiable {@link Collection} of {@code Attribute}s.
	 */
	public List<Attribute<?>> getAttributes( )
	{
		return Collections.unmodifiableList( Arrays.asList( attributes ) );
	}
	
	public Attribute<?>[ ] getAttributeArray( )
	{
		return Arrays.copyOf( attributes , attributes.length );
	}
	
	public int getAttributeCount( )
	{
		return attributes.length;
	}
	
	public Attribute<?> attributeAt( int index )
	{
		return attributes[ index ];
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
		final Class<? super T>						valueClass;
		int											index	= -1;
		final String								name;
		final Bimapper<? super T, ? extends Object>	format;
		
		public static <T> Attribute<T> newInstance( Class<? super T> valueClass , String name , Bimapper<? super T, ? extends Object> format )
		{
			return new Attribute<T>( valueClass , name , format );
		}
		
		/**
		 * Creates an attribute with the given name and format.
		 * 
		 * @param name
		 *            the name used in XML.
		 * @param format
		 *            the format for converting from the attribute value to and from a {@code String} for XML.
		 */
		public Attribute( Class<? super T> valueClass , String name , Bimapper<? super T, ? extends Object> format )
		{
			super( );
			this.valueClass = valueClass;
			this.name = name;
			this.format = format;
		}
		
		public Class<? super T> getValueClass( )
		{
			return valueClass;
		}
		
		public int getIndex( )
		{
			return index;
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
		public Bimapper<? super T, ? extends Object> getBimapper( )
		{
			return format;
		}
	}
	
	public static class IdentityBimapper implements Bimapper<Object, Object>
	{
		private IdentityBimapper( )
		{
		}
		
		public static final IdentityBimapper	instance	= new IdentityBimapper( );
		
		@Override
		public Object map( Object in )
		{
			return in;
		}
		
		@Override
		public Object unmap( Object out )
		{
			return out;
		}
	}
	
	public static class StringBimapper implements Bimapper<String, Object>
	{
		public Object map( String t )
		{
			return t;
		}
		
		public String unmap( Object s )
		{
			return s == null ? null : s.toString( );
		}
	}
	
	public static class EnumBimapper<E extends Enum<E>> implements Bimapper<E, Object>
	{
		Class<E>	cls;
		
		private EnumBimapper( Class<E> cls )
		{
			this.cls = cls;
		}
		
		public static <E extends Enum<E>> EnumBimapper<E> newInstance( Class<E> cls )
		{
			return new EnumBimapper<E>( cls );
		}
		
		@Override
		public Object map( E t )
		{
			return t == null ? null : t.name( );
		}
		
		@Override
		public E unmap( Object s )
		{
			return s == null || s == null ? null : Enum.valueOf( cls , s.toString( ) );
		}
	}
	
	public static class BooleanBimapper implements Bimapper<Boolean, Object>
	{
		public Object map( Boolean t )
		{
			return t;
		}
		
		public Boolean unmap( Object s )
		{
			return s == null ? null : Boolean.valueOf( s.toString( ) );
		}
	}
	
	public static class IntegerBimapper implements Bimapper<Integer, Object>
	{
		public Object map( Integer t )
		{
			return t;
		}
		
		public Integer unmap( Object s )
		{
			return s == null ? null : Integer.valueOf( s.toString( ) );
		}
	}
	
	public static class LongBimapper implements Bimapper<Long, Object>
	{
		public Object map( Long t )
		{
			return t;
		}
		
		public Long unmap( Object s )
		{
			return s == null ? null : Long.valueOf( s.toString( ) );
		}
	}
	
	public static class FloatBimapper implements Bimapper<Float, Object>
	{
		public Object map( Float t )
		{
			return t;
		}
		
		public Float unmap( Object s )
		{
			return s == null ? null : Float.valueOf( s.toString( ) );
		}
	}
	
	public static class DoubleBimapper implements Bimapper<Double, Object>
	{
		public Object map( Double t )
		{
			return t;
		}
		
		public Double unmap( Object s )
		{
			return s == null ? null : Double.valueOf( s.toString( ) );
		}
	}
	
	public static class BigIntegerBimapper implements Bimapper<BigInteger, Object>
	{
		public Object map( BigInteger t )
		{
			return t;
		}
		
		public BigInteger unmap( Object s )
		{
			return s == null ? null : new BigInteger( s.toString( ) );
		}
	}
	
	public static class BigDecimalBimapper implements Bimapper<BigDecimal, Object>
	{
		public Object map( BigDecimal t )
		{
			return t;
		}
		
		public BigDecimal unmap( Object s )
		{
			return s == null ? null : new BigDecimal( s.toString( ) );
		}
	}
	
	public static class DateBimapper implements Bimapper<Date, Object>
	{
		java.text.DateFormat	format;
		
		public DateBimapper( java.text.DateFormat format )
		{
			this.format = format;
		}
		
		public DateBimapper( String simpleDateFormatPattern )
		{
			this( new SimpleDateFormat( simpleDateFormatPattern ) );
		}
		
		@Override
		public synchronized Object map( Date t )
		{
			return t == null ? null : format.format( t );
		}
		
		@Override
		public synchronized Date unmap( Object s )
		{
			try
			{
				return s == null ? null : format.parse( s.toString( ) );
			}
			catch( ParseException e )
			{
				throw new RuntimeException( e );
			}
		}
	}
	
	public static class NullBimapper implements Bimapper<Object, Object>
	{
		public static final NullBimapper	instance	= new NullBimapper( );
		
		@Override
		public Object map( Object o )
		{
			return null;
		}
		
		@Override
		public Object unmap( Object o )
		{
			return null;
		}
	}
	
	public static class ColorBimapper implements Bimapper<Color, Object>
	{
		public static final ColorBimapper	instance	= new ColorBimapper( );
		
		private ColorBimapper( )
		{
			
		}
		
		@Override
		public Object map( Color in )
		{
			return in == null ? null : String.format( "#%06x" , in.getRGB( ) & 0xffffff );
		}
		
		@Override
		public Color unmap( Object out )
		{
			return out == null ? null : new Color( Integer.parseInt( out.toString( ).substring( 1 ) , 16 ) );
		}
	}
	
	public static class SpecObjectBimapper<S extends YamlSpec<S>> implements Bimapper<YamlObject<S>, Object>
	{
		S	spec;
		
		private SpecObjectBimapper( S spec )
		{
			super( );
			this.spec = spec;
		}
		
		public static <S extends YamlSpec<S>> SpecObjectBimapper<S> newInstance( S spec )
		{
			return new SpecObjectBimapper<S>( spec );
		}
		
		@Override
		public Object map( YamlObject<S> t )
		{
			return t == null ? null : t.toYaml( );
		}
		
		@Override
		public YamlObject<S> unmap( Object s )
		{
			try
			{
				return s == null ? null : spec.fromYaml( ( Map<?, ?> ) s );
			}
			catch( Exception e )
			{
				throw new RuntimeException( e );
			}
		}
	}
	
	public static class SpecArrayListBimapper<E> implements Bimapper<YamlArrayList<E>, Object>
	{
		Bimapper<? super E, Object>	format;
		
		private SpecArrayListBimapper( Bimapper<? super E, Object> format )
		{
			super( );
			this.format = format;
		}
		
		public static <E> SpecArrayListBimapper<E> newInstance( Bimapper<? super E, Object> format )
		{
			return new SpecArrayListBimapper<E>( format );
		}
		
		@Override
		public Object map( YamlArrayList<E> t )
		{
			return t == null ? null : t.toYaml( );
		}
		
		@Override
		public YamlArrayList<E> unmap( Object s )
		{
			try
			{
				return s == null || s == null ? null : YamlArrayList.<E>fromYaml( ( List<?> ) s , format );
			}
			catch( Exception e )
			{
				throw new RuntimeException( e );
			}
		}
	}
	
	public static Attribute<String> stringAttribute( String name )
	{
		return new Attribute<String>( String.class , name , new StringBimapper( ) );
	}
	
	public static Attribute<File> fileAttribute( String name )
	{
		return new Attribute<File>( File.class , name , new FileStringBimapper( ) );
	}
	
	public static <E extends Enum<E>> Attribute<E> enumAttribute( String name , Class<E> cls )
	{
		return new Attribute<E>( cls , name , EnumBimapper.newInstance( cls ) );
	}
	
	public static Attribute<Boolean> booleanAttribute( String name )
	{
		return new Attribute<Boolean>( Boolean.class , name , new BooleanBimapper( ) );
	}
	
	public static Attribute<Integer> integerAttribute( String name )
	{
		return new Attribute<Integer>( Integer.class , name , new IntegerBimapper( ) );
	}
	
	public static Attribute<Long> longAttribute( String name )
	{
		return new Attribute<Long>( Long.class , name , new LongBimapper( ) );
	}
	
	public static Attribute<Float> floatAttribute( String name )
	{
		return new Attribute<Float>( Float.class , name , new FloatBimapper( ) );
	}
	
	public static Attribute<Double> doubleAttribute( String name )
	{
		return new Attribute<Double>( Double.class , name , new DoubleBimapper( ) );
	}
	
	public static Attribute<BigInteger> bigIntegerAttribute( String name )
	{
		return new Attribute<BigInteger>( BigInteger.class , name , new BigIntegerBimapper( ) );
	}
	
	public static Attribute<BigDecimal> bigDecimalAttribute( String name )
	{
		return new Attribute<BigDecimal>( BigDecimal.class , name , new BigDecimalBimapper( ) );
	}
	
	public static <S extends YamlSpec<S>> Attribute<YamlObject<S>> yamlObjectAttribute( String name , S spec )
	{
		return new Attribute<YamlObject<S>>( YamlObject.class , name , SpecObjectBimapper.newInstance( spec ) );
	}
	
	public static <E> Attribute<YamlArrayList<E>> yamlArrayListAttribute( String name , Bimapper<? super E, Object> format )
	{
		return new Attribute<YamlArrayList<E>>( YamlArrayList.class , name , SpecArrayListBimapper.<E>newInstance( format ) );
	}
	
	public static Attribute<float[ ]> floatArrayAttribute( String name )
	{
		return new Attribute<float[ ]>( float[ ].class , name , IdentityBimapper.instance );
	}
	
	public static Attribute<Color> colorAttribute( String name )
	{
		return new Attribute<Color>( Color.class , name , ColorBimapper.instance );
	}
}
