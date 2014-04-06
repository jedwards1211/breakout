package org.andork.reflect;

import static org.andork.util.ArrayUtils.copyOf;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.andork.util.ArrayUtils;

public class ReflectionUtils
{
	private static TypeFormatter	staticTypeFormatter	= new DefaultTypeFormatter( );
	
	/**
	 * Searches all public, protected, package access, and private fields of a class and its superclasses for a field with the given name.
	 * {@link Class#getField(String)} only searches public members declared in the class.
	 */
	public static Field getField( Class<?> clazz , String name )
	{
		while( clazz != null )
		{
			for( Field field : clazz.getDeclaredFields( ) )
			{
				if( field.getName( ).equals( name ) )
				{
					return field;
				}
			}
			clazz = clazz.getSuperclass( );
		}
		return null;
	}
	
	public static Map<String, Field> getInstanceFields( Class<?> clazz )
	{
		Map<String, Field> result = new HashMap<String, Field>( );
		while( clazz != null )
		{
			for( Field field : clazz.getDeclaredFields( ) )
			{
				if( !Modifier.isStatic( field.getModifiers( ) ) && !result.containsKey( field.getName( ) ) )
				{
					result.put( field.getName( ) , field );
				}
			}
			clazz = clazz.getSuperclass( );
		}
		return result;
	}
	
	public static List<Field> getInstanceFieldList( Class<?> clazz )
	{
		List<Field> result = new ArrayList<Field>( );
		while( clazz != null )
		{
			for( Field field : clazz.getDeclaredFields( ) )
			{
				if( !Modifier.isStatic( field.getModifiers( ) ) )
				{
					result.add( field );
				}
			}
			clazz = clazz.getSuperclass( );
		}
		return result;
	}
	
	/**
	 * Gets all static fields declared in the given class.
	 * 
	 * @param clazz
	 *            the class to get static fields of.
	 * @param includeSuperclasses
	 *            whether to include static fields declared in superclasses. They will come before the {@code clazz}'s fields in the result list.
	 * @return a {@link List} of {@link Field}s.
	 */
	public static List<Field> getStaticFieldList( Class<?> clazz , boolean includeSuperclasses )
	{
		List<Field> result = new ArrayList<Field>( );
		addStaticFields( clazz , includeSuperclasses , result );
		return result;
	}
	
	private static void addStaticFields( Class<?> clazz , boolean includeSuperclasses , Collection<Field> out )
	{
		if( includeSuperclasses && clazz != Object.class )
		{
			addStaticFields( clazz.getSuperclass( ) , true , out );
		}
		for( Field field : clazz.getDeclaredFields( ) )
		{
			if( Modifier.isStatic( field.getModifiers( ) ) )
			{
				out.add( field );
			}
		}
	}
	
	public static List<Method> getInstanceMethodList( Class<?> clazz )
	{
		List<Method> result = new ArrayList<Method>( );
		while( clazz != null )
		{
			for( Method method : clazz.getDeclaredMethods( ) )
			{
				if( !Modifier.isStatic( method.getModifiers( ) ) )
				{
					result.add( method );
				}
			}
			clazz = clazz.getSuperclass( );
		}
		return result;
	}
	
	public static List<Method> getAllMethods( Class<?> clazz )
	{
		List<Method> result = new ArrayList<Method>( );
		while( clazz != null )
		{
			result.addAll( Arrays.asList( clazz.getDeclaredMethods( ) ) );
			clazz = clazz.getSuperclass( );
		}
		return result;
	}
	
	public static Object getFieldValue( Object obj , String fieldName ) throws IllegalArgumentException , IllegalAccessException
	{
		return getField( obj.getClass( ) , fieldName ).get( obj );
	}
	
	/**
	 * Searches all public, protected, package access, and private methods of a class and its superclasses for a method with the given name and parameter types.
	 * {@link Class#getMethod(String, Class...)} only searches public members declared in the class.
	 */
	public static Method getMethod( Class<?> clazz , String name , Class<?> ... parameterTypes )
	{
		while( clazz != null )
		{
			for( Method method : clazz.getDeclaredMethods( ) )
			{
				if( method.getName( ).equals( name ) && Arrays.deepEquals( method.getParameterTypes( ) , parameterTypes ) )
				{
					return method;
				}
			}
			clazz = clazz.getSuperclass( );
		}
		return null;
	}
	
	/**
	 * Creates a string representation of the given type that would be valid in java code.
	 */
	public static String format( Type t )
	{
		return staticTypeFormatter.format( t );
	}
	
	/**
	 * Replaces any occurrences of the given {@link TypeVariable}s inside the given type (or the type itself, if it is one of the given {@code TypeVariable}s)
	 * with the given replacement types.
	 * 
	 * @param t
	 *            the type to replace {@code TypeVariable}s within.
	 * @param variables
	 *            the {@code TypeVariable}s to replace.
	 * @param replacements
	 *            the replacement {@code Type}s corresponding to {@code variables}.
	 */
	public static Type replaceTypeVariables( Type t , TypeVariable<?>[ ] variables , Type[ ] replacements )
	{
		if( t instanceof WildcardType )
		{
			WildcardType wt = ( WildcardType ) t;
			
			Type[ ] lowerBounds = wt.getLowerBounds( );
			final Type[ ] replacedLowerBounds = new Type[ lowerBounds.length ];
			for( int i = 0 ; i < lowerBounds.length ; i++ )
			{
				replacedLowerBounds[ i ] = replaceTypeVariables( lowerBounds[ i ] , variables , replacements );
			}
			
			Type[ ] upperBounds = wt.getUpperBounds( );
			final Type[ ] replacedUpperBounds = new Type[ upperBounds.length ];
			for( int i = 0 ; i < upperBounds.length ; i++ )
			{
				replacedUpperBounds[ i ] = replaceTypeVariables( upperBounds[ i ] , variables , replacements );
			}
			
			return new WildcardTypeImpl( replacedUpperBounds , replacedLowerBounds );
		}
		if( t instanceof TypeVariable<?> )
		{
			int index = ArrayUtils.indexOf( variables , t );
			return index < 0 ? t : replacements[ index ];
		}
		if( t instanceof GenericArrayType )
		{
			GenericArrayType gat = ( GenericArrayType ) t;
			return new GenericArrayTypeImpl( replaceTypeVariables(
					gat.getGenericComponentType( ) , variables , replacements ) );
		}
		if( t instanceof ParameterizedType )
		{
			final ParameterizedType pt = ( ParameterizedType ) t;
			
			Type[ ] arguments = pt.getActualTypeArguments( );
			final Type[ ] replacedArguments = new Type[ arguments.length ];
			for( int i = 0 ; i < arguments.length ; i++ )
			{
				replacedArguments[ i ] = replaceTypeVariables( arguments[ i ] , variables , replacements );
			}
			
			return new ParameterizedTypeImpl( pt.getRawType( ) , pt.getOwnerType( ) , replacedArguments );
		}
		return t;
	}
	
	public static Type getGenericSupertype( Type t )
	{
		if( t instanceof Class<?> )
		{
			Class<?> c = ( Class<?> ) t;
			return c.getGenericSuperclass( );
		}
		if( t instanceof WildcardType )
		{
			WildcardType wt = ( WildcardType ) t;
			Type[ ] lowerBounds = wt.getLowerBounds( );
			Type[ ] upperBounds = wt.getUpperBounds( );
			if( lowerBounds == null && upperBounds != null && upperBounds.length == 1 )
			{
				return new WildcardTypeImpl( new Type[ ] { getGenericSupertype( upperBounds[ 0 ] ) } ,
						new Type[ 0 ] );
			}
			return Object.class;
		}
		if( t instanceof GenericArrayType )
		{
			return new GenericArrayTypeImpl( getGenericSupertype( ( ( GenericArrayType ) t ).getGenericComponentType( ) ) );
		}
		if( t instanceof TypeVariable<?> )
		{
			TypeVariable<?> tv = ( TypeVariable<?> ) t;
			Type[ ] bounds = tv.getBounds( );
			if( bounds != null && bounds.length == 1 )
			{
				return new TypeVariableImpl<GenericDeclaration>( tv.getGenericDeclaration( ) ,
						new Type[ ] { getGenericSupertype( bounds[ 0 ] ) } , tv.getName( ) );
			}
		}
		if( t instanceof ParameterizedType )
		{
			ParameterizedType pt = ( ParameterizedType ) t;
			Class<?> c = ( Class<?> ) pt.getRawType( );
			return replaceTypeVariables( c.getGenericSuperclass( ) , c.getTypeParameters( ) , pt.getActualTypeArguments( ) );
		}
		return t;
	}
	
	public static Class<?> getRawType( Type t )
	{
		if( t instanceof Class<?> )
		{
			return ( Class<?> ) t;
		}
		if( t instanceof WildcardType )
		{
			WildcardType wt = ( WildcardType ) t;
			Type[ ] upperBounds = wt.getUpperBounds( );
			if( upperBounds != null && upperBounds.length == 1 )
			{
				return getRawType( upperBounds[ 0 ] );
			}
		}
		if( t instanceof GenericArrayType )
		{
			try
			{
				return Class.forName( "[L" + getRawType( ( ( GenericArrayType ) t ).getGenericComponentType( ) ).getName( ) + ";" );
			}
			catch( ClassNotFoundException e )
			{
				return new Object[ 0 ].getClass( );
			}
		}
		if( t instanceof ParameterizedType )
		{
			return ( Class<?> ) ( ( ParameterizedType ) t ).getRawType( );
		}
		if( t instanceof TypeVariable<?> )
		{
			TypeVariable<?> tv = ( TypeVariable<?> ) t;
			Type[ ] bounds = tv.getBounds( );
			if( bounds != null && bounds.length == 1 )
			{
				return getRawType( bounds[ 0 ] );
			}
		}
		return Object.class;
	}
	
	public static Class<?> getOwnerType( Type t )
	{
		if( t instanceof Class<?> )
		{
			return ( ( Class<?> ) t ).getEnclosingClass( );
		}
		if( t instanceof ParameterizedType )
		{
			return ( Class<?> ) ( ( ParameterizedType ) t ).getOwnerType( );
		}
		return null;
	}
	
	public static Type getComponentType( Type type )
	{
		if( type instanceof GenericArrayType )
		{
			return ( ( GenericArrayType ) type ).getGenericComponentType( );
		}
		if( type instanceof Class<?> )
		{
			return ( ( Class<?> ) type ).getComponentType( );
		}
		return null;
	}
	
	public static Type getTypeParameter( Type type , int index )
	{
		if( type instanceof ParameterizedType )
		{
			try
			{
				return ( ( ParameterizedType ) type ).getActualTypeArguments( )[ index ];
			}
			catch( Exception ex )
			{
				System.out.println( "TEST" );
			}
		}
		if( type instanceof WildcardType )
		{
			WildcardType wt = ( WildcardType ) type;
			Type[ ] upperBounds = wt.getUpperBounds( );
			if( upperBounds.length == 1 )
			{
				return getTypeParameter( upperBounds[ 0 ] , index );
			}
		}
		if( type instanceof Class<?> )
		{
			try
			{
				return ( ( Class<?> ) type ).getTypeParameters( )[ index ];
			}
			catch( Exception e )
			{
				
			}
		}
		return null;
	}
	
	public static Type getTypeParameterOrObject( Type type , int index )
	{
		Type result = getTypeParameter( type , index );
		return result == null ? Object.class : result;
	}
	
	public static Type[ ] getTypeParameters( Type type )
	{
		if( type instanceof ParameterizedType )
		{
			return ( ( ParameterizedType ) type ).getActualTypeArguments( );
		}
		if( type instanceof Class<?> )
		{
			return ( ( Class<?> ) type ).getTypeParameters( );
		}
		return new Type[ 0 ];
	}
	
	public static Type[ ] getSupertypeParameters( Class<?> supertype , Type resolvedSubtype )
	{
		Type lastType = resolvedSubtype;
		while( resolvedSubtype != null && isAssignableFrom( supertype , resolvedSubtype ) )
		{
			lastType = resolvedSubtype;
			resolvedSubtype = getGenericSupertype( resolvedSubtype );
		}
		Class<?> rawType = getRawType( lastType );
		TypeVariable<?>[ ] variables = rawType.getTypeParameters( );
		Type[ ] replacements = getTypeParameters( lastType );
		
		if( supertype.isInterface( ) )
		{
			Type[ ] interfaces = rawType.getGenericInterfaces( );
			int ifaceIndex;
			for( ifaceIndex = interfaces.length - 1 ; ifaceIndex >= 0 ; ifaceIndex-- )
			{
				if( getRawType( interfaces[ ifaceIndex ] ) == supertype )
				{
					break;
				}
			}
			if( ifaceIndex >= 0 )
			{
				Type[ ] ifaceParams = getTypeParameters( interfaces[ ifaceIndex ] );
				for( int i = 0 ; i < ifaceParams.length ; i++ )
				{
					ifaceParams[ i ] = replaceTypeVariables( ifaceParams[ i ] , variables , replacements );
				}
				return ifaceParams;
			}
		}
		else if( rawType == supertype )
		{
			return replacements;
		}
		
		return lastType == null ? new Type[ 0 ] : getTypeParameters( lastType );
	}
	
	public static Type getAddAllableType( Type collectionType )
	{
		if( !Collection.class.isAssignableFrom( getRawType( collectionType ) ) )
		{
			throw new IllegalArgumentException( format( collectionType ) + " is not a Collection" );
		}
		Type parameter = getTypeParameter( collectionType , 0 );
		if( parameter == null )
		{
			return parameterize( Collection.class , new WildcardTypeImpl( new Type[ 0 ] , new Type[ 0 ] ) );
		}
		Type elemType = parameter;
		if( !Modifier.isFinal( getRawType( elemType ).getModifiers( ) ) )
		{
			elemType = extendsWildcard( elemType );
		}
		return parameterize( Collection.class , elemType );
	}
	
	public static Type extendsWildcard( Type basetype )
	{
		if( basetype instanceof WildcardType )
		{
			throw new IllegalArgumentException( "can't extend a wildcard type" );
		}
		return new WildcardTypeImpl( new Type[ ] { basetype } , new Type[ 0 ] );
	}
	
	public static Type resolveType( final Type toResolve , Type resolvedEnclosingType )
	{
		if( toResolve instanceof WildcardType )
		{
			WildcardType wt = ( WildcardType ) toResolve;
			
			Type[ ] lowerBounds = wt.getLowerBounds( );
			final Type[ ] resolvedLowerBounds = new Type[ lowerBounds.length ];
			for( int i = 0 ; i < lowerBounds.length ; i++ )
			{
				resolvedLowerBounds[ i ] = resolveType( lowerBounds[ i ] , resolvedEnclosingType );
			}
			
			Type[ ] upperBounds = wt.getUpperBounds( );
			final Type[ ] resolvedUpperBounds = new Type[ upperBounds.length ];
			for( int i = 0 ; i < upperBounds.length ; i++ )
			{
				resolvedUpperBounds[ i ] = resolveType( upperBounds[ i ] , resolvedEnclosingType );
			}
			
			return new WildcardTypeImpl( resolvedUpperBounds , resolvedLowerBounds );
		}
		if( toResolve instanceof TypeVariable<?> )
		{
			TypeVariable<?> tv = ( TypeVariable<?> ) toResolve;
			if( tv.getGenericDeclaration( ) instanceof Class )
			{
				Class<?> declClass = ( Class<?> ) tv.getGenericDeclaration( );
				int varIndex = ArrayUtils.indexOf( declClass.getTypeParameters( ) , tv );
				Type[ ] superParams = getSupertypeParameters( declClass , resolvedEnclosingType );
				if( varIndex < superParams.length )
				{
					return superParams[ varIndex ];
				}
			}
			return toResolve;
		}
		if( toResolve instanceof GenericArrayType )
		{
			GenericArrayType gat = ( GenericArrayType ) toResolve;
			return new GenericArrayTypeImpl( resolveType(
					gat.getGenericComponentType( ) , resolvedEnclosingType ) );
		}
		if( toResolve instanceof ParameterizedType || toResolve instanceof Class<?> )
		{
			Type[ ] args = getTypeParameters( toResolve );
			if( args.length == 0 )
			{
				return toResolve;
			}
			final Type[ ] resolvedTypeArguments = new Type[ args.length ];
			
			for( int i = 0 ; i < args.length ; i++ )
			{
				resolvedTypeArguments[ i ] = resolveType( args[ i ] , resolvedEnclosingType );
			}
			
			return new ParameterizedTypeImpl( getRawType( toResolve ) , getOwnerType( toResolve ) , resolvedTypeArguments );
		}
		
		return toResolve;
	}
	
	public static boolean isAssignableFrom( Class<?> c , Type t )
	{
		if( t instanceof WildcardType )
		{
			for( Type bound : ( ( WildcardType ) t ).getUpperBounds( ) )
			{
				if( !isAssignableFrom( c , bound ) )
				{
					return false;
				}
			}
			return true;
		}
		else if( t instanceof GenericArrayType )
		{
			return isAssignableFrom( c , getRawType( t ) );
		}
		else if( t instanceof ParameterizedType )
		{
			return isAssignableFrom( c , getRawType( t ) );
		}
		else if( t instanceof TypeVariable<?> )
		{
			for( Type bound : ( ( TypeVariable<?> ) t ).getBounds( ) )
			{
				if( isAssignableFrom( c , bound ) )
				{
					return true;
				}
			}
		}
		else if( t instanceof Class<?> )
		{
			return c.isAssignableFrom( ( Class<?> ) t );
		}
		return false;
	}
	
	public static Class<?> getBox( Class<?> primitiveType )
	{
		if( primitiveType.equals( boolean.class ) )
		{
			return Boolean.class;
		}
		if( primitiveType.equals( byte.class ) )
		{
			return Byte.class;
		}
		if( primitiveType.equals( short.class ) )
		{
			return Short.class;
		}
		if( primitiveType.equals( char.class ) )
		{
			return Character.class;
		}
		if( primitiveType.equals( int.class ) )
		{
			return Integer.class;
		}
		if( primitiveType.equals( float.class ) )
		{
			return Float.class;
		}
		if( primitiveType.equals( long.class ) )
		{
			return Long.class;
		}
		if( primitiveType.equals( double.class ) )
		{
			return Double.class;
		}
		throw new IllegalArgumentException( "Invalid type: " + primitiveType );
	}
	
	public static boolean isFieldSettableFrom( Class<?> fieldType , Class<?> cls )
	{
		if( fieldType.isPrimitive( ) )
		{
			return getBox( fieldType ).isAssignableFrom( cls );
		}
		return fieldType.isAssignableFrom( cls );
	}
	
	public static boolean isAssignableFrom( Type a , Type b )
	{
		if( a instanceof WildcardType )
		{
			WildcardType wt = ( WildcardType ) a;
			Type[ ] lower = wt.getLowerBounds( );
			if( lower != null && lower.length > 0 )
			{
				return lower.length == 1;
			}
			
			Type[ ] upper = wt.getUpperBounds( );
			if( upper != null && upper.length == 1 )
			{
				return isAssignableFrom( upper[ 0 ] , b );
			}
		}
		else if( a instanceof GenericArrayType )
		{
			return getComponentType( b ) != null && isAssignableFrom( getComponentType( a ) , getComponentType( b ) );
		}
		else if( a instanceof ParameterizedType )
		{
			if( b instanceof WildcardType )
			{
				WildcardType wt = ( WildcardType ) b;
				Type[ ] lower = wt.getLowerBounds( );
				if( lower != null && lower.length > 0 )
				{
					return false;
				}
				
				Type[ ] upper = wt.getUpperBounds( );
				if( upper != null && upper.length == 0 )
				{
					return isAssignableFrom( a , upper[ 0 ] );
				}
			}
			else if( b instanceof GenericArrayType )
			{
				return false;
			}
			else if( b instanceof ParameterizedType )
			{
				if( !isAssignableFrom( getRawType( a ) , b ) )
				{
					return false;
				}
				
				Type[ ] aParams = ( ( ParameterizedType ) a ).getActualTypeArguments( );
				b = parameterize( getRawType( b ) , getSupertypeParameters( getRawType( a ) , b ) );
				Type[ ] bParams = ( ( ParameterizedType ) b ).getActualTypeArguments( );
				
				if( aParams.length != bParams.length )
				{
					return false;
				}
				
				for( int i = 0 ; i < aParams.length ; i++ )
				{
					if( !isAssignableFrom( aParams[ i ] , bParams[ i ] ) )
					{
						return false;
					}
				}
				
				return true;
			}
			else if( b instanceof TypeVariable<?> )
			{
				TypeVariable<?> tv = ( TypeVariable<?> ) b;
				Type[ ] bounds = tv.getBounds( );
				return bounds.length == 1 && isAssignableFrom( a , bounds[ 0 ] );
			}
			else if( b instanceof Class<?> )
			{
				return false;
			}
			
		}
		else if( a instanceof TypeVariable<?> )
		{
			TypeVariable<?> tv = ( TypeVariable<?> ) a;
			Type[ ] bounds = tv.getBounds( );
			return bounds.length == 1 && isAssignableFrom( bounds[ 0 ] , b );
		}
		else if( a instanceof Class<?> )
		{
			return ( ( Class<?> ) a ).isAssignableFrom( getRawType( b ) );
		}
		return false;
	}
	
	public static void main( String[ ] args )
	{
		List<Type> types = new ArrayList<Type>( );
		types.add( Map.class );
		types.add( HashMap.class );
		types.add( new ParameterizedTypeImpl( Map.class , null , Number.class , Number.class ) );
		types.add( new ParameterizedTypeImpl( Map.class , null , Double.class , Number.class ) );
		types.add( new ParameterizedTypeImpl( Map.class , null , Number.class , Number.class ) );
		types.add( new ParameterizedTypeImpl( HashMap.class , null , Number.class , Double.class ) );
		
		System.out.println( staticTypeFormatter.format( findBestUpperBound( types , new ParameterizedTypeImpl( Map.class , null , Integer.class , Double.class ) ) ) );
		System.out.println( staticTypeFormatter.format( findBestUpperBound( types , new ParameterizedTypeImpl( Map.class , null , Double.class , Double.class ) ) ) );
		System.out.println( staticTypeFormatter.format( findBestUpperBound( types , new ParameterizedTypeImpl( LinkedHashMap.class , null , Double.class , Double.class ) ) ) );
		System.out.println( staticTypeFormatter.format( findBestUpperBound( types , LinkedHashMap.class ) ) );
	}
	
	public static Type findBestUpperBound( Collection<Type> types , Type toBound )
	{
		Type best = null;
		for( Type type : types )
		{
			if( isAssignableFrom( type , toBound ) )
			{
				if( best == null || isAssignableFrom( best , type ) )
				{
					best = type;
				}
			}
		}
		return best;
	}
	
	public static ParameterizedType parameterize( Class<?> clazz , final Type ... parameters )
	{
		return new ParameterizedTypeImpl( clazz , clazz.getEnclosingClass( ) , parameters );
	}
	
	@SuppressWarnings( "unchecked" )
	public static <T> List<T> findConstants( Class<T> type , Class<?> ... declaringClasses )
	{
		List<T> result = new ArrayList<T>( );
		
		for( Class<?> declClass : declaringClasses )
		{
			for( Field field : declClass.getDeclaredFields( ) )
			{
				if( type.isAssignableFrom( field.getType( ) ) &&
						Modifier.isStatic( field.getModifiers( ) ) &&
						Modifier.isFinal( field.getModifiers( ) ) )
				{
					try
					{
						field.setAccessible( true );
						result.add( ( T ) field.get( null ) );
					}
					catch( Exception ex )
					{
						ex.printStackTrace( );
					}
				}
			}
		}
		
		return result;
	}
	
	public static final class GenericArrayTypeImpl implements GenericArrayType
	{
		private final Type	genericComponentType;
		
		protected GenericArrayTypeImpl( Type genericComponentType )
		{
			super( );
			this.genericComponentType = genericComponentType;
		}
		
		@Override
		public Type getGenericComponentType( )
		{
			return genericComponentType;
		}
		
		public String toString( )
		{
			return format( this );
		}
		
		public boolean equals( Object o )
		{
			if( o instanceof GenericArrayType )
			{
				return genericComponentType == ( ( GenericArrayType ) o ).getGenericComponentType( );
			}
			return false;
		}
		
		public int hashCode( )
		{
			return genericComponentType.hashCode( );
		}
	}
	
	public static final class ParameterizedTypeImpl implements ParameterizedType
	{
		private final Type		rawType;
		private final Type		ownerType;
		private final Type[ ]	actualTypeArguments;
		
		public ParameterizedTypeImpl( Type rawType , Type ownerType , Type ... actualTypeArguments )
		{
			super( );
			this.rawType = rawType;
			this.ownerType = ownerType;
			this.actualTypeArguments = copyOf( actualTypeArguments );
		}
		
		@Override
		public Type[ ] getActualTypeArguments( )
		{
			return copyOf( actualTypeArguments );
		}
		
		@Override
		public Type getRawType( )
		{
			return rawType;
		}
		
		@Override
		public Type getOwnerType( )
		{
			return ownerType;
		}
		
		public String toString( )
		{
			return format( this );
		}
	}
	
	public static final class TypeVariableImpl<D extends GenericDeclaration> implements TypeVariable<D>
	{
		private final D			genericDeclaration;
		private final Type[ ]	bounds;
		private final String	name;
		
		protected TypeVariableImpl( D genericDeclaration , Type[ ] bounds , String name )
		{
			this.genericDeclaration = genericDeclaration;
			this.bounds = copyOf( bounds );
			this.name = name;
		}
		
		@Override
		public Type[ ] getBounds( )
		{
			return copyOf( bounds );
		}
		
		@Override
		public D getGenericDeclaration( )
		{
			return genericDeclaration;
		}
		
		@Override
		public String getName( )
		{
			return name;
		}
		
		public String toString( )
		{
			return format( this );
		}
	}
	
	public static final class WildcardTypeImpl implements WildcardType
	{
		private final Type[ ]	upperBounds;
		private final Type[ ]	lowerBounds;
		
		protected WildcardTypeImpl( Type[ ] upperBounds , Type[ ] lowerBounds )
		{
			this.upperBounds = copyOf( upperBounds );
			this.lowerBounds = copyOf( lowerBounds );
		}
		
		@Override
		public Type[ ] getUpperBounds( )
		{
			return copyOf( upperBounds );
		}
		
		@Override
		public Type[ ] getLowerBounds( )
		{
			return copyOf( lowerBounds );
		}
		
		public String toString( )
		{
			return format( this );
		}
	}
}
