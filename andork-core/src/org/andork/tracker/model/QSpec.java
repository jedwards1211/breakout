package org.andork.tracker.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Defines a set of properties that any {@link QArrayObject} constructed with this
 * spec will have.<br>
 * <br>
 * To use this, create a class that extends {@code QSpec}, give it {@code public static final Property<?>} fields, and
 * pass those fields into {@link #QSpec(Property...)}. You'll also probably want to create a {@code public static final}
 * instance of your class and make its constructor {@code private}.<br>
 * <br>
 * The Q doesn't stand for anything.
 * 
 * @author andy.edwards
 * @param <S>
 *            the type of {@link QSpec} for this object.
 */
public class QSpec
{
	public static class Property<T>
	{
		int						index;
		QSpec					spec;
		String					name;
		Class<? super T>		type;
		Supplier<? extends T>	initValue;

		public Property( String name , Class<? super T> type )
		{
			this.name = name;
			this.type = type;
		}

		public Property( String name , Class<? super T> type , Supplier<? extends T> initValue )
		{
			this( name , type );
			this.initValue = initValue;
		}

		public final boolean isPropertyOf( QObject<?> object )
		{
			return object.spec.properties[ index ] == this;
		}

		public final boolean isPropertyOf( QSpec spec )
		{
			return spec.properties[ index ] == this;
		}

		public final Property<T> requirePropertyOf( QSpec spec )
		{
			if( !isPropertyOf( spec ) )
			{
				throw new IllegalArgumentException( this + " is not a property of the given spec" );
			}
			return this;
		}

		public final Property<T> requirePropertyOf( QObject<?> object )
		{
			if( !isPropertyOf( object ) )
			{
				throw new IllegalArgumentException( this + " is not a property of the given object" );
			}
			return this;
		}

		protected T get( QObject<?> object )
		{
			requirePropertyOf( object );
			return object.doGet( this );
		}

		protected T set( QObject<?> object , T newValue )
		{
			requirePropertyOf( object );
			return object.doSet( this , newValue );
		}

		public final int index( )
		{
			return index;
		}

		public final String name( )
		{
			return name;
		}

		public final Class<? super T> type( )
		{
			return type;
		}

		public Supplier<? extends T> initValue( )
		{
			return initValue;
		}

		public String toString( )
		{
			return name;
		}

		protected boolean equals( T a , T b )
		{
			return Objects.equals( a , b );
		}

		protected int hashCode( T t )
		{
			return t == null ? 0 : t.hashCode( );
		}
	}

	public static class NonNullProperty<T> extends Property<T>
	{

		public NonNullProperty( String name , Class<? super T> type , Supplier<? extends T> initValue )
		{
			super( name , type , Objects.requireNonNull( initValue ) );
		}

		@Override
		protected T set( QObject<?> object , T newValue )
		{
			return super.set( object , Objects.requireNonNull( newValue ) );
		}
	}

	QSpec				superspec;
	Property<?>[ ]		properties;
	List<Property<?>>	propertyList;

	/**
	 * Creates a "derived" {@code QSpec} with all the properties of a
	 * "superspec" and more. Implementing classes that use this constructor
	 * should extend the superspec class.
	 * 
	 * @param superspec
	 * @param properties
	 */
	public QSpec( QSpec superspec , Property<?> ... properties )
	{
		this.superspec = superspec;
		this.properties = new Property[ superspec.properties.length + properties.length ];
		System.arraycopy( superspec.properties , 0 , this.properties , 0 , superspec.properties.length );
		for( int i = 0 ; i < properties.length ; i++ )
		{
			if( properties[ i ].spec != null )
			{
				throw new IllegalStateException( "properties[" + i + "] (" + properties[ i ].name +
					") already belongs to spec " + properties[ i ].spec );
			}
			int ii = i + superspec.properties.length;
			this.properties[ ii ] = properties[ i ];
			this.properties[ ii ].spec = this;
			this.properties[ ii ].index = ii;
		}
		propertyList = Collections.unmodifiableList( Arrays.asList( properties ) );
	}

	public QSpec( Property<?> ... properties )
	{
		this.superspec = null;
		this.properties = new Property[ properties.length ];
		for( int i = 0 ; i < properties.length ; i++ )
		{
			if( properties[ i ].spec != null )
			{
				throw new IllegalStateException( "properties[" + i + "] (" + properties[ i ].name +
					") already belongs to spec " + properties[ i ].spec );
			}
			this.properties[ i ] = properties[ i ];
			this.properties[ i ].spec = this;
			this.properties[ i ].index = i;
		}
		propertyList = Collections.unmodifiableList( Arrays.asList( properties ) );
	}

	public static <T> Property<T> property( String name , Class<? super T> type )
	{
		return new Property<T>( name , type );
	}

	public static <T> Property<T> property( String name , Class<? super T> type , T initValue )
	{
		return new Property<T>( name , type , ( ) -> initValue );
	}

	public static <T> Property<T> property( String name , Class<? super T> type , Supplier<? extends T> initValue )
	{
		return new Property<T>( name , type , initValue );
	}

	public static <T> NonNullProperty<T> nonNullProperty( String name , Class<? super T> type , T initValue )
	{
		return new NonNullProperty<T>( name , type , ( ) -> initValue );
	}

	public static <T> NonNullProperty<T> nonNullProperty( String name , Class<? super T> type ,
		Supplier<? extends T> initValue )
	{
		return new NonNullProperty<T>( name , type , initValue );
	}
	
	public final QSpec superspec( )
	{
		return superspec;
	}

	public final Property<?> propertyAt( int index )
	{
		return properties[ index ];
	}

	public final List<Property<?>> properties( )
	{
		return propertyList;
	}

	public final Property<?> propertyNamed( String name )
	{
		for( int i = 0 ; i < properties.length ; i++ )
		{
			if( name.equals( properties[ i ].name ) )
			{
				return properties[ i ];
			}
		}
		return null;
	}

	public final int propertyCount( )
	{
		return properties.length;
	}

	@SuppressWarnings( { "unchecked" , "rawtypes" } )
	protected boolean equals( QObject a , Object b )
	{
		if( b instanceof QObject && a.spec == ( ( QObject ) b ).spec )
		{
			QObject bq = ( QObject ) b;
			for( int i = 0 ; i < properties.length ; i++ )
			{
				if( ! ( ( Property ) properties[ i ] ).equals( a.doGet( properties[ i ] ) , bq.doGet( properties[ i ] ) ) )
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private static final int[ ]	primes	=
										{
										13 , 17 , 19 , 23 , 29 , 31 , 37 , 41 , 43 , 47 , 53 , 59 ,
										61 , 67 , 71 ,
										73 , 79 , 83 , 89 , 97 , 101 , 103 , 107 , 109 , 113 , 127 , 131 , 137 , 139 ,
										149 , 151 , 157 , 163 , 167 ,
										173 ,
										179 , 181 , 191 , 193 , 197 , 199 , 211 , 223 , 227 , 229 , 233 , 239 , 241 ,
										251 , 257 , 263 , 269 , 271 ,
										277 , 281 ,
										283 , 293 , 307 , 311 , 313 , 317 , 331 , 337 , 347 , 349 , 353 , 359 , 367 ,
										373 , 379 , 383 , 389 , 397 ,
										401 , 409 ,
										419 , 421 , 431 , 433 , 439 , 443 , 449 , 457 , 461 , 463 , 467 , 479 , 487 ,
										491 , 499 , 503 , 509 , 521 ,
										523 , 541 };

	@SuppressWarnings( { "unchecked" , "rawtypes" } )
	protected int hashCode( QObject o )
	{
		int hashCode = 0;

		for( int i = 0 ; i < properties.length ; i++ )
		{
			hashCode = ( hashCode * primes[ i % primes.length ] )
				^ ( ( Property ) properties[ i ] ).hashCode( o.doGet( properties[ i ] ) );
		}

		return hashCode;
	}
}
