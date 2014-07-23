/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package com.andork.plot;
import java.util.Arrays;


public class ArrayUtils
{
	private static void rangeCheck( int arrayLen , int fromIndex , int toIndex )
	{
		if( fromIndex > toIndex )
			throw new IllegalArgumentException( "fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")" );
		if( fromIndex < 0 )
			throw new ArrayIndexOutOfBoundsException( fromIndex );
		if( toIndex > arrayLen )
			throw new ArrayIndexOutOfBoundsException( toIndex );
	}
	
	/**
	 * Adapted from {@link Arrays#binarySearch(double[], int, int, double)}.
	 */
	public static <T>int binarySearch( T[ ] a , int fromIndex , int toIndex , double key , DoubleKeyGetter<T> keyGetter )
	{
		rangeCheck( a.length , fromIndex , toIndex );
		return binarySearch0( a , fromIndex , toIndex , key , keyGetter );
	}
	
	private static <T>int binarySearch0( T[ ] a , int fromIndex , int toIndex , double key , DoubleKeyGetter<T> keyGetter )
	{
		int low = fromIndex;
		int high = toIndex - 1;
		
		while( low <= high )
		{
			int mid = ( low + high ) >>> 1;
			double midVal = keyGetter.keyOf( a[ mid ] );
			
			int cmp;
			if( midVal < key )
			{
				cmp = -1; // Neither val is NaN, thisVal is smaller
			}
			else if( midVal > key )
			{
				cmp = 1; // Neither val is NaN, thisVal is larger
			}
			else
			{
				long midBits = Double.doubleToLongBits( midVal );
				long keyBits = Double.doubleToLongBits( key );
				cmp = ( midBits == keyBits ? 0 : // Values are equal
						( midBits < keyBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
								1 ) ); // (0.0, -0.0) or (NaN, !NaN)
			}
			
			if( cmp < 0 )
				low = mid + 1;
			else if( cmp > 0 )
				high = mid - 1;
			else
				return mid; // key found
		}
		return -( low + 1 ); // key not found.
	}
	
	public static int ceilingIndex( double[ ] a , double key )
	{
		int i = Arrays.binarySearch( a , key );
		
		if( i >= 0 )
		{
			return i;
		}
		
		i = -( i + 1 );
		
		return i == a.length ? -1 : i;
	}
	
	public static <T>int ceilingIndex( T[ ] a , double key , DoubleKeyGetter<T> keyGetter )
	{
		int i = binarySearch( a , 0 , a.length , key , keyGetter );
		
		if( i >= 0 )
		{
			return i;
		}
		
		i = -( i + 1 );
		
		return i == a.length ? -1 : i;
	}
	
	public static int ceilingIndexUnsorted( double[ ] a , double key )
	{
		double ceiling = Double.NaN;
		int ceilingIndex = -1;
		
		for( int i = 0 ; i < a.length ; i++ )
		{
			if( a[ i ] == key )
			{
				return i;
			}
			else if( a[ i ] > key && ( Double.isNaN( ceiling ) || a[ i ] < ceiling ) )
			{
				ceiling = a[ i ];
				ceilingIndex = i;
			}
		}
		
		return ceilingIndex;
	}
	
	public static <T>int ceilingIndexUnsorted( T[ ] a , double key , DoubleKeyGetter<T> keyGetter )
	{
		double ceiling = Double.NaN;
		int ceilingIndex = -1;
		
		for( int i = 0 ; i < a.length ; i++ )
		{
			double iKey = keyGetter.keyOf( a[ i ] );
			if( iKey == key )
			{
				return i;
			}
			else if( iKey > key && ( Double.isNaN( ceiling ) || iKey < ceiling ) )
			{
				ceiling = iKey;
				ceilingIndex = i;
			}
		}
		
		return ceilingIndex;
	}
	
	public static int floorIndex( double[ ] a , double key )
	{
		int i = Arrays.binarySearch( a , key );
		
		if( i >= 0 )
		{
			return i;
		}
		
		i = -( i + 1 );
		
		return i - 1;
	}
	
	public static <T>int floorIndex( T[ ] a , double key , DoubleKeyGetter<T> keyGetter )
	{
		int i = binarySearch( a , 0 , a.length , key , keyGetter );
		
		if( i >= 0 )
		{
			return i;
		}
		
		i = -( i + 1 );
		
		return i - 1;
	}
	
	public static int floorIndexUnsorted( double[ ] a , double key )
	{
		double floor = Double.NaN;
		int floorIndex = -1;
		
		for( int i = 0 ; i < a.length ; i++ )
		{
			if( a[ i ] == key )
			{
				return i;
			}
			else if( a[ i ] < key && ( Double.isNaN( floor ) || a[ i ] > floor ) )
			{
				floor = a[ i ];
				floorIndex = i;
			}
		}
		
		return floorIndex;
	}
	
	public static <T>int floorIndexUnsorted( T[ ] a , double key , DoubleKeyGetter<T> keyGetter )
	{
		double floor = Double.NaN;
		int floorIndex = -1;
		
		for( int i = 0 ; i < a.length ; i++ )
		{
			double iKey = keyGetter.keyOf( a[ i ] );
			if( iKey == key )
			{
				return i;
			}
			else if( iKey < key && ( Double.isNaN( floor ) || iKey > floor ) )
			{
				floor = iKey;
				floorIndex = i;
			}
		}
		
		return floorIndex;
	}
	
	public static int higherIndex( double[ ] a , double key )
	{
		int i = ceilingIndex( a , key );
		
		return i < 0 || a[ i ] > key ? i : i < a.length - 1 ? i + 1 : -1;
	}
	
	public static int higherIndex( double[ ] a , double key , boolean backward )
	{
		return backward ? lowerIndex( a , key ) : higherIndex( a , key );
	}
	
	public static <T>int higherIndex( T[ ] a , double key , DoubleKeyGetter<T> keyGetter )
	{
		int i = ceilingIndex( a , key , keyGetter );
		
		return i < 0 || keyGetter.keyOf( a[ i ] ) > key ? i : i < a.length - 1 ? i + 1 : -1;
	}
	
	public static int lowerIndex( double[ ] a , double key )
	{
		int i = floorIndex( a , key );
		
		return i < 0 || a[ i ] < key ? i : i - 1;
	}
	
	public static int lowerIndex( double[ ] a , double key , boolean backward )
	{
		return backward ? higherIndex( a , key ) : lowerIndex( a , key );
	}
	
	public static <T>int lowerIndex( T[ ] a , double key , DoubleKeyGetter<T> keyGetter )
	{
		int i = floorIndex( a , key , keyGetter );
		
		return i < 0 || keyGetter.keyOf( a[ i ] ) < key ? i : i - 1;
	}
	
	public static int nearestIndex( double[ ] a , double key )
	{
		int floor = floorIndex( a , key );
		int ceiling = ceilingIndex( a , key );
		
		if( floor >= 0 )
		{
			return ceiling < 0 || a[ ceiling ] - key > key - a[ floor ] ? floor : ceiling;
		}
		return ceiling;
	}
	
	public static <T>int nearestIndex( T[ ] a , double key , DoubleKeyGetter<T> keyGetter )
	{
		int floor = floorIndex( a , key , keyGetter );
		int ceiling = ceilingIndex( a , key , keyGetter );
		
		if( floor >= 0 )
		{
			return ceiling < 0 || keyGetter.keyOf( a[ ceiling ] ) - key > key - keyGetter.keyOf( a[ floor ] ) ? floor : ceiling;
		}
		return ceiling;
	}
	
	public static int nearestIndexUnsorted( double[ ] a , double key )
	{
		double nearest = Double.NaN;
		int nearestIndex = -1;
		
		for( int i = 0 ; i < a.length ; i++ )
		{
			if( a[ i ] == key )
			{
				return i;
			}
			else if( Double.isNaN( nearest ) || Math.abs( a[ i ] - key ) < Math.abs( nearest - key ) )
			{
				nearest = a[ i ];
				nearestIndex = i;
			}
		}
		
		return nearestIndex;
	}
	
	public static <T>int nearestIndexUnsorted( T[ ] a , double key , DoubleKeyGetter<T> keyGetter )
	{
		double nearest = Double.NaN;
		int nearestIndex = -1;
		
		for( int i = 0 ; i < a.length ; i++ )
		{
			double iKey = keyGetter.keyOf( a[ i ] );
			if( iKey == key )
			{
				return i;
			}
			else if( Double.isNaN( nearest ) || Math.abs( iKey - key ) < Math.abs( nearest - key ) )
			{
				nearest = iKey;
				nearestIndex = i;
			}
		}
		
		return nearestIndex;
	}
	
	public static double[ ] remove( double[ ] array , int index )
	{
		double[ ] newArray = new double[ array.length - 1 ];
		
		System.arraycopy( array , 0 , newArray , 0 , index );
		if( index < array.length - 1 )
		{
			System.arraycopy( array , index + 1 , newArray , index , array.length - index - 1 );
		}
		return newArray;
	}
	
	public static double[ ] insert( double[ ] array , double key , int index )
	{
		double[ ] newArray = Arrays.copyOf( array , array.length + 1 );
		System.arraycopy( array , 0 , newArray , 0 , index );
		newArray[ index ] = key;
		System.arraycopy( array , index , newArray , index + 1 , array.length - index );
		return newArray;
	}
	
	public static double[ ] insert( double[ ] sortedSet , double key )
	{
		int insertIndex = ArrayUtils.ceilingIndex( sortedSet , key );
		
		if( insertIndex < 0 )
		{
			insertIndex = sortedSet.length;
		}
		else if( sortedSet[ insertIndex ] == key )
		{
			return sortedSet;
		}
		
		double[ ] newSet = Arrays.copyOf( sortedSet , sortedSet.length + 1 );
		System.arraycopy( sortedSet , 0 , newSet , 0 , insertIndex );
		sortedSet[ insertIndex ] = key;
		System.arraycopy( sortedSet , insertIndex , newSet , insertIndex + 1 , sortedSet.length - insertIndex );
		
		return newSet;
	}
	
	public static boolean equals( Object o1 , Object o2 )
	{
		return ( o1 == null && o2 == null ) || ( o1 != null && o1.equals( o2 ) ) || ( o2 != null && o2.equals( o1 ) );
	}
	
	public static double minValue( double[ ] values )
	{
		double min = Double.NaN;
		for( double d : values )
		{
			if( Double.isNaN( min ) || d < min )
			{
				min = d;
			}
		}
		return min;
	}
	
	public static double maxValue( double[ ] values )
	{
		double max = Double.NaN;
		for( double d : values )
		{
			if( Double.isNaN( max ) || d > max )
			{
				max = d;
			}
		}
		return max;
	}
	
	@SuppressWarnings( "unchecked" )
	public static <T>T[ ] remove( T[ ] array , int index )
	{
		T[ ] newArray = ( T[ ] ) new Object[ array.length - 1 ];
		
		System.arraycopy( array , 0 , newArray , 0 , index );
		if( index < array.length - 1 )
		{
			System.arraycopy( array , index + 1 , newArray , index , array.length - index - 1 );
		}
		return newArray;
	}
	
	public static <T>T[ ] insert( T[ ] array , T key , int index )
	{
		T[ ] newArray = Arrays.copyOf( array , array.length + 1 );
		System.arraycopy( array , 0 , newArray , 0 , index );
		newArray[ index ] = key;
		System.arraycopy( array , index , newArray , index + 1 , array.length - index );
		return newArray;
	}
}
