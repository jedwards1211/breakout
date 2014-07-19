package org.andork.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import org.andork.func.ThrowingDoubleFunction;
import org.andork.func.ThrowingFunction;
import org.andork.func.ThrowingToDoubleFunction;

public class ArrayUtils
{
	private ArrayUtils( )
	{
		
	}
	
	/**
	 * Changes the block size of an array, i.e. pads or truncates blocks of elements.
	 */
	public static float[ ] changeBlockSize( float[ ] src , int srcBlockSize , int destBlockSize )
	{
		if( ( src.length % srcBlockSize ) != 0 )
		{
			throw new IllegalArgumentException( "src does not have a block size of " + srcBlockSize );
		}
		final float[ ] dest = new float[ src.length * destBlockSize / srcBlockSize ];
		final int minBlockSize = Math.min( srcBlockSize , destBlockSize );
		final int srcPad = srcBlockSize - destBlockSize;
		final int destPad = destBlockSize - srcBlockSize;
		
		int s = 0, d = 0;
		if( srcPad > 0 )
		{
			while( s < src.length )
			{
				System.arraycopy( src , s , dest , d , minBlockSize );
				s += minBlockSize + srcPad;
				d += minBlockSize;
			}
		}
		else if( destPad > 0 )
		{
			while( s < src.length )
			{
				System.arraycopy( src , s , dest , d , minBlockSize );
				s += minBlockSize;
				d += minBlockSize + destPad;
			}
		}
		else
		{
			System.arraycopy( src , 0 , dest , 0 , src.length );
		}
		
		return dest;
	}
	
	/**
	 * Changes the block size of an array, i.e. pads or truncates blocks of elements.
	 */
	public static double[ ] changeBlockSize( double[ ] src , int srcBlockSize , int destBlockSize )
	{
		if( ( src.length % srcBlockSize ) != 0 )
		{
			throw new IllegalArgumentException( "src does not have a block size of " + srcBlockSize );
		}
		final double[ ] dest = new double[ src.length * destBlockSize / srcBlockSize ];
		final int minBlockSize = Math.min( srcBlockSize , destBlockSize );
		final int srcPad = srcBlockSize - destBlockSize;
		final int destPad = destBlockSize - srcBlockSize;
		
		int s = 0, d = 0;
		if( srcPad > 0 )
		{
			while( s < src.length )
			{
				System.arraycopy( src , s , dest , d , minBlockSize );
				s += minBlockSize + srcPad;
				d += minBlockSize;
			}
		}
		else if( destPad > 0 )
		{
			while( s < src.length )
			{
				System.arraycopy( src , s , dest , d , minBlockSize );
				s += minBlockSize;
				d += minBlockSize + destPad;
			}
		}
		else
		{
			System.arraycopy( src , 0 , dest , 0 , src.length );
		}
		
		return dest;
	}
	
	/**
	 * Changes the block size of an array, i.e. pads or truncates blocks of elements.
	 */
	public static byte[ ] changeBlockSize( byte[ ] src , int srcBlockSize , int destBlockSize )
	{
		if( ( src.length % srcBlockSize ) != 0 )
		{
			throw new IllegalArgumentException( "src does not have a block size of " + srcBlockSize );
		}
		final byte[ ] dest = new byte[ src.length * destBlockSize / srcBlockSize ];
		final int minBlockSize = Math.min( srcBlockSize , destBlockSize );
		final int srcPad = srcBlockSize - destBlockSize;
		final int destPad = destBlockSize - srcBlockSize;
		
		int s = 0, d = 0;
		if( srcPad > 0 )
		{
			while( s < src.length )
			{
				System.arraycopy( src , s , dest , d , minBlockSize );
				s += minBlockSize + srcPad;
				d += minBlockSize;
			}
		}
		else if( destPad > 0 )
		{
			while( s < src.length )
			{
				System.arraycopy( src , s , dest , d , minBlockSize );
				s += minBlockSize;
				d += minBlockSize + destPad;
			}
		}
		else
		{
			System.arraycopy( src , 0 , dest , 0 , src.length );
		}
		
		return dest;
	}
	
	public static int max( int[ ] values )
	{
		int max = Integer.MIN_VALUE;
		for( final int value : values )
		{
			max = Math.max( max , value );
		}
		return max;
		
	}
	
	public static String prettyPrintAsNumbers( char[ ] a , int columns , int start , int end , int newlineInterval , String elemFormat )
	{
		StringBuffer sb = new StringBuffer( );
		
		int indexWidth = ( int ) Math.log10( a.length ) + 1;
		String indexFormat = "%" + indexWidth + "d";
		
		int rows = 0;
		
		int i = start;
		while( i < end )
		{
			sb.append( '[' ).append( String.format( indexFormat , i ) ).append( "] " );
			for( int col = 0 ; col < columns - 1 && i < end ; col++ , i++ )
			{
				sb.append( String.format( elemFormat , ( int ) a[ i ] ) ).append( "  " );
			}
			if( i < end )
			{
				sb.append( String.format( elemFormat , ( int ) a[ i++ ] ) ).append( '\n' );
			}
			
			if( ++rows == newlineInterval )
			{
				rows = 0;
				sb.append( '\n' );
			}
		}
		
		return sb.toString( );
	}
	
	public static String prettyPrint( int[ ] a , int columns , int start , int end , int newlineInterval , String elemFormat )
	{
		StringBuffer sb = new StringBuffer( );
		
		int indexWidth = ( int ) Math.log10( a.length ) + 1;
		String indexFormat = "%" + indexWidth + "d";
		
		int rows = 0;
		
		int i = start;
		while( i < end )
		{
			sb.append( '[' ).append( String.format( indexFormat , i ) ).append( "] " );
			for( int col = 0 ; col < columns - 1 && i < end ; col++ , i++ )
			{
				sb.append( String.format( elemFormat , a[ i ] ) ).append( "  " );
			}
			if( i < end )
			{
				sb.append( String.format( elemFormat , a[ i++ ] ) ).append( '\n' );
			}
			
			if( ++rows == newlineInterval )
			{
				rows = 0;
				sb.append( '\n' );
			}
		}
		
		return sb.toString( );
	}
	
	public static String prettyPrint( float[ ] a , int columns , int start , int end , int newlineInterval , String elemFormat )
	{
		StringBuffer sb = new StringBuffer( );
		
		int indexWidth = ( int ) Math.log10( a.length ) + 1;
		String indexFormat = "%" + indexWidth + "d";
		
		int rows = 0;
		
		int i = start;
		while( i < end )
		{
			sb.append( '[' ).append( String.format( indexFormat , i ) ).append( "] " );
			for( int col = 0 ; col < columns - 1 && i < end ; col++ , i++ )
			{
				sb.append( String.format( elemFormat , a[ i ] ) ).append( "  " );
			}
			if( i < end )
			{
				sb.append( String.format( elemFormat , a[ i++ ] ) ).append( '\n' );
			}
			
			if( ++rows == newlineInterval )
			{
				rows = 0;
				sb.append( '\n' );
			}
		}
		
		return sb.toString( );
	}
	
	public static String prettyPrint( float[ ] a , int columns )
	{
		int intDigits = 0;
		int fracDigits = 0;
		
		for( float f : a )
		{
			if( f == 0f )
			{
				continue;
			}
			int log = ( int ) Math.floor( Math.log10( Math.abs( f ) ) );
			intDigits = Math.max( intDigits , log + 1 );
			fracDigits = Math.max( fracDigits , -log );
		}
		
		String elemFormat = String.format( "%%%d.%df" , intDigits + fracDigits + 2 , fracDigits );
		return prettyPrint( a , columns , 0 , a.length , 0 , elemFormat );
	}
	
	public static String prettyPrint( double[ ] a , int columns , int start , int end , int newlineInterval , String elemFormat )
	{
		StringBuffer sb = new StringBuffer( );
		
		int indexWidth = ( int ) Math.log10( a.length ) + 1;
		String indexFormat = "%" + indexWidth + "d";
		
		int rows = 0;
		
		int i = start;
		while( i < end )
		{
			if( i > start )
			{
				sb.append( '\n' );
			}
			
			sb.append( '[' ).append( String.format( indexFormat , i ) ).append( "] " );
			for( int col = 0 ; col < columns && i < end ; col++ , i++ )
			{
				if( col > 0 )
				{
					sb.append( "  " );
				}
				sb.append( String.format( elemFormat , a[ i ] ) );
			}
			
			if( ++rows == newlineInterval )
			{
				rows = 0;
				sb.append( '\n' );
			}
		}
		
		return sb.toString( );
	}
	
	public static String cat( Object[ ] o , String delimiter )
	{
		StringBuffer sb = new StringBuffer( );
		if( o.length > 0 )
		{
			sb.append( o[ 0 ] );
		}
		for( int i = 1 ; i < o.length ; i++ )
		{
			sb.append( delimiter ).append( o[ i ] );
		}
		return sb.toString( );
	}
	
	public static String prettyPrint( double[ ][ ] a , String elemFormat )
	{
		StringBuffer sb = new StringBuffer( );
		
		int indexWidth = ( int ) Math.log10( a.length ) + 1;
		String indexFormat = "%" + indexWidth + "d";
		
		for( int row = 0 ; row < a.length ; row++ )
		{
			sb.append( '[' ).append( String.format( indexFormat , row ) ).append( "] " );
			
			for( int col = 0 ; col < a[ row ].length ; col++ )
			{
				sb.append( String.format( elemFormat , a[ row ][ col ] ) ).append( "  " );
			}
			
			sb.append( '\n' );
		}
		
		return sb.toString( );
	}
	
	public static String prettyPrint( float[ ][ ] a , String elemFormat )
	{
		StringBuffer sb = new StringBuffer( );
		
		int indexWidth = ( int ) Math.log10( a.length ) + 1;
		String indexFormat = "%" + indexWidth + "d";
		
		for( int row = 0 ; row < a.length ; row++ )
		{
			sb.append( '[' ).append( String.format( indexFormat , row ) ).append( "] " );
			
			for( int col = 0 ; col < a[ row ].length ; col++ )
			{
				sb.append( String.format( elemFormat , a[ row ][ col ] ) ).append( "  " );
			}
			
			sb.append( '\n' );
		}
		
		return sb.toString( );
	}
	
	/**
	 * Finds the index of an equivalent {@link Object} in an arbitrary-order {@code Object} array.
	 * 
	 * @param values
	 *        an array of {@code Object}s with arbitrary order.
	 * @param a
	 *        the equivalent {@code Object} to search for. May be {@code null}.
	 * @return the lowest index of any element in {@code values} such that {@code Java7.equals(a, values[i])}, or {@code -1} otherwise.
	 */
	public static int indexOf( Object[ ] values , Object a )
	{
		for( int i = 0 ; i < values.length ; i++ )
		{
			if( Java7.Objects.equals( a , values[ i ] ) )
			{
				return i;
			}
		}
		return -1;
	}
	
	public static boolean contains( Object[ ] values , Object target )
	{
		return indexOf( values , target ) >= 0;
	}
	
	/**
	 * Finds the first index of {@link Object} in an arbitrary-order {@code Object} array.
	 * 
	 * @param values
	 *        an array of {@code Object}s with arbitrary order.
	 * @param a
	 *        the {@code Object} to search for. May be {@code null}.
	 * @return the lowest index of {@code a} in {@code values}, or {@code -1} if it is not present.
	 */
	public static int strictIndexOf( Object[ ] values , Object a )
	{
		for( int i = 0 ; i < values.length ; i++ )
		{
			if( a == values[ i ] )
			{
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Finds the index of a double in an arbitrary-order double array.
	 * 
	 * @param values
	 *        an array of doubles with arbitrary order.
	 * @param a
	 *        the value to search for. This method always returns {@code -1} if {@code NaN} is given.
	 * @return the lowest index of any non-{@code NaN} element in {@code values} that {@code == a}, or {@code -1} otherwise.
	 */
	public static int indexOf( double[ ] values , double a )
	{
		for( int i = 0 ; i < values.length ; i++ )
		{
			if( values[ i ] == a )
			{
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Swaps two elements in an array.
	 * 
	 * @param a
	 *        the array to swap elements of.
	 * @param i0
	 *        the index of the first element.
	 * @param i1
	 *        the index of the second element.
	 */
	public static void swap( double[ ] a , int i0 , int i1 )
	{
		double temp = a[ i0 ];
		a[ i0 ] = a[ i1 ];
		a[ i1 ] = a[ i0 ];
	}
	
	/**
	 * Finds the index of a int in an arbitrary-order int array.
	 * 
	 * @param values
	 *        an array of ints with arbitrary order.
	 * @param a
	 *        the value to search for.
	 * @return the lowest index of any element in {@code values} that {@code == a}, or {@code -1} otherwise.
	 */
	public static int indexOf( int[ ] values , int a )
	{
		for( int i = 0 ; i < values.length ; i++ )
		{
			if( values[ i ] == a )
			{
				return i;
			}
		}
		return -1;
	}
	
	public static ArrayList<Float> toArrayList( float[ ] values )
	{
		ArrayList<Float> result = new ArrayList<Float>( );
		for( float value : values )
		{
			result.add( value );
		}
		return result;
	}
	
	public static ArrayList<Double> toArrayList( double[ ] values )
	{
		ArrayList<Double> result = new ArrayList<Double>( );
		for( double value : values )
		{
			result.add( value );
		}
		return result;
	}
	
	public static <T> T[ ] copyOf( T[ ] original )
	{
		return Arrays.copyOf( original , original.length );
	}
	
	public static double[ ] toDoubleArray( Collection<Double> doubles )
	{
		double[ ] result = new double[ doubles.size( ) ];
		int k = 0;
		for( Double d : doubles )
		{
			result[ k++ ] = d;
		}
		return result;
	}
	
	public static float[ ] toFloatArray( Collection<Float> floats )
	{
		float[ ] result = new float[ floats.size( ) ];
		int k = 0;
		for( Float d : floats )
		{
			result[ k++ ] = d;
		}
		return result;
	}
	
	public static float[ ] toFloatArray2( Collection<Number> numbers )
	{
		float[ ] result = new float[ numbers.size( ) ];
		int k = 0;
		for( Number d : numbers )
		{
			result[ k++ ] = d.floatValue( );
		}
		return result;
	}
	
	public static double[ ] toSortedDoubleArray( Collection<Double> doubles )
	{
		double[ ] result = toDoubleArray( doubles );
		Arrays.sort( result );
		return result;
	}
	
	public static int[ ] toIntArray( Collection<Integer> ints )
	{
		int[ ] result = new int[ ints.size( ) ];
		int k = 0;
		for( int d : ints )
		{
			result[ k++ ] = d;
		}
		return result;
	}
	
	public static int[ ] toSortedIntArray( Collection<Integer> ints )
	{
		int[ ] result = toIntArray( ints );
		Arrays.sort( result );
		return result;
	}
	
	public static <T> T[ ] toArray( Iterable<? extends T> iterable , Class<T> componentType )
	{
		int count = 0;
		for( T t : iterable )
		{
			count++ ;
		}
		T[ ] result = ( T[ ] ) Array.newInstance( componentType , count );
		int k = 0;
		for( T t : iterable )
		{
			result[ k++ ] = t;
		}
		return result;
	}
	
	public static <I, O> O[ ] map( I[ ] in , O[ ] out , Function<I, O> function )
	{
		for( int i = 0 ; i < out.length ; i++ )
		{
			out[ i ] = function.apply( in[ i ] );
		}
		return out;
	}
	
	public static <I, O, T extends Throwable> O[ ] throwableMap( I[ ] in , O[ ] out , ThrowingFunction<I, O, T> function ) throws T
	{
		for( int i = 0 ; i < out.length ; i++ )
		{
			out[ i ] = function.apply( in[ i ] );
		}
		return out;
	}
	
	public static <O, T extends Throwable> O[ ] throwableMap( double[ ] in , O[ ] out , ThrowingDoubleFunction<O, T> function ) throws T
	{
		for( int i = 0 ; i < out.length ; i++ )
		{
			out[ i ] = function.apply( in[ i ] );
		}
		return out;
	}
	
	public static <I, T extends Throwable> double[ ] throwableMap( I[ ] in , double[ ] out , ThrowingToDoubleFunction<I, T> function ) throws T
	{
		for( int i = 0 ; i < out.length ; i++ )
		{
			out[ i ] = function.applyAsDouble( in[ i ] );
		}
		return out;
	}
	
	public static <T> int indexOf( T[ ] array , Predicate<T> predicate )
	{
		for( int i = 0 ; i < array.length ; i++ )
		{
			if( predicate.test( array[ i ] ) )
			{
				return i;
			}
		}
		return -1;
	}
}
