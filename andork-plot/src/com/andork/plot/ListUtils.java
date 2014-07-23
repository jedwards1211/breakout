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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

public class ListUtils
{
	public static double[ ] toArray( List<Double> a )
	{
		double[ ] result = new double[ a.size( ) ];
		
		for( int i = 0 ; i < a.size( ) ; i++ )
		{
			result[ i ] = a.get( i );
		}
		
		return result;
	}
	
	public static ArrayList<Double> toArrayList( double[ ] a )
	{
		ArrayList<Double> result = new ArrayList<Double>( );
		
		for( double d : a )
		{
			result.add( d );
		}
		return result;
	}
	
	public static int ceilingIndex( List<Double> a , double key )
	{
		int i = binarySearch( a , key );
		
		if( i >= 0 )
		{
			return i;
		}
		
		i = -( i + 1 );
		
		return i == a.size( ) ? -1 : i;
	}
	
	public static <T>int ceilingIndex( List<T> a , double key , DoubleKeyGetter<T> keyGetter )
	{
		int i = binarySearch( a , key , keyGetter );
		
		if( i >= 0 )
		{
			return i;
		}
		
		i = -( i + 1 );
		
		return i == a.size( ) ? -1 : i;
	}
	
	public static int ceilingIndexUnsorted( List<Double> a , double key )
	{
		double ceiling = Double.NaN;
		int ceilingIndex = -1;
		
		for( int i = 0 ; i < a.size( ) ; i++ )
		{
			double iKey = a.get( i );
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
	
	public static <T>int ceilingIndexUnsorted( List<T> a , double key , DoubleKeyGetter<T> keyGetter )
	{
		double ceiling = Double.NaN;
		int ceilingIndex = -1;
		
		for( int i = 0 ; i < a.size( ) ; i++ )
		{
			double iKey = keyGetter.keyOf( a.get( i ) );
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
	
	public static int floorIndex( List<Double> a , double key )
	{
		int i = binarySearch( a , key );
		
		if( i >= 0 )
		{
			return i;
		}
		
		i = -( i + 1 );
		
		return i - 1;
	}
	
	public static <T>int floorIndex( List<T> a , double key , DoubleKeyGetter<T> keyGetter )
	{
		int i = binarySearch( a , key , keyGetter );
		
		if( i >= 0 )
		{
			return i;
		}
		
		i = -( i + 1 );
		
		return i - 1;
	}
	
	public static int floorIndexUnsorted( List<Double> a , double key )
	{
		double floor = Double.NaN;
		int floorIndex = -1;
		
		for( int i = 0 ; i < a.size( ) ; i++ )
		{
			double iKey = a.get( i );
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
	
	public static <T>int floorIndexUnsorted( List<T> a , double key , DoubleKeyGetter<T> keyGetter )
	{
		double floor = Double.NaN;
		int floorIndex = -1;
		
		for( int i = 0 ; i < a.size( ) ; i++ )
		{
			double iKey = keyGetter.keyOf( a.get( i ) );
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
	
	public static int higherIndex( List<Double> a , double key )
	{
		int i = ceilingIndex( a , key );
		
		return i < 0 || a.get( i ) > key ? i : i < a.size( ) - 1 ? i + 1 : -1;
	}
	
	public static <T>int higherIndex( List<T> a , double key , DoubleKeyGetter<T> keyGetter )
	{
		int i = ceilingIndex( a , key , keyGetter );
		
		return i < 0 || keyGetter.keyOf( a.get( i ) ) > key ? i : i < a.size( ) - 1 ? i + 1 : -1;
	}
	
	public static int lowerIndex( List<Double> a , double key )
	{
		int i = floorIndex( a , key );
		
		return i < 0 || a.get( i ) < key ? i : i - 1;
	}
	
	public static <T>int lowerIndex( List<T> a , double key , DoubleKeyGetter<T> keyGetter )
	{
		int i = floorIndex( a , key , keyGetter );
		
		return i < 0 || keyGetter.keyOf( a.get( i ) ) < key ? i : i - 1;
	}
	
	public static int nearestIndex( List<Double> a , double key )
	{
		int floor = floorIndex( a , key );
		int ceiling = ceilingIndex( a , key );
		
		if( floor >= 0 )
		{
			return ceiling < 0 || a.get( ceiling ) - key > key - a.get( floor ) ? floor : ceiling;
		}
		return ceiling;
	}
	
	public static <T>int nearestIndex( List<T> a , double key , DoubleKeyGetter<T> keyGetter )
	{
		int floor = floorIndex( a , key , keyGetter );
		int ceiling = ceilingIndex( a , key , keyGetter );
		
		if( floor >= 0 )
		{
			return ceiling < 0 || keyGetter.keyOf( a.get( ceiling ) ) - key > key - keyGetter.keyOf( a.get( floor ) ) ? floor : ceiling;
		}
		return ceiling;
	}
	
	public static int nearestIndexUnsorted( List<Double> a , double key )
	{
		double nearest = Double.NaN;
		int nearestIndex = -1;
		
		for( int i = 0 ; i < a.size( ) ; i++ )
		{
			double iKey = a.get( i );
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
	
	public static <T>int nearestIndexUnsorted( List<T> a , double key , DoubleKeyGetter<T> keyGetter )
	{
		double nearest = Double.NaN;
		int nearestIndex = -1;
		
		for( int i = 0 ; i < a.size( ) ; i++ )
		{
			double iKey = keyGetter.keyOf( a.get( i ) );
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
	
	private static final int	BINARYSEARCH_THRESHOLD	= 5000;
	
	/**
	 * Searches the specified list for the specified object using the binary
	 * search algorithm. The list must be sorted into ascending order
	 * according to the {@linkplain Comparable natural ordering} of its
	 * elements (as by the {@link #sort(List)} method) prior to making this
	 * call. If it is not sorted, the results are undefined. If the list
	 * contains multiple elements equal to the specified object, there is no
	 * guarantee which one will be found.
	 * 
	 * <p>
	 * This method runs in log(n) time for a "random access" list (which provides near-constant-time positional access). If the specified list does not
	 * implement the {@link RandomAccess} interface and is large, this method will do an iterator-based binary search that performs O(n) link traversals and
	 * O(log n) element comparisons.
	 * 
	 * @param list
	 *            the list to be searched.
	 * @param key
	 *            the key to be searched for.
	 * @return the index of the search key, if it is contained in the list;
	 *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
	 *         <i>insertion point</i> is defined as the point at which the
	 *         key would be inserted into the list: the index of the first
	 *         element greater than the key, or <tt>list.size()</tt> if all
	 *         elements in the list are less than the specified key. Note
	 *         that this guarantees that the return value will be &gt;= 0 if
	 *         and only if the key is found.
	 * @throws ClassCastException
	 *             if the list contains elements that are not
	 *             <i>mutually comparable</i> (for example, strings and
	 *             integers), or the search key is not mutually comparable
	 *             with the elements of the list.
	 */
	public static <T>int binarySearch( List<? extends T> list , double key , DoubleKeyGetter<T> keyGetter )
	{
		if( list instanceof RandomAccess || list.size( ) < BINARYSEARCH_THRESHOLD )
			return indexedBinarySearch( list , key , keyGetter );
		else
			return iteratorBinarySearch( list , key , keyGetter );
	}
	
	private static <T>int indexedBinarySearch( List<? extends T> list , double key , DoubleKeyGetter<T> keyGetter )
	{
		int low = 0;
		int high = list.size( ) - 1;
		
		while( low <= high )
		{
			int mid = ( low + high ) >>> 1;
			double midVal = keyGetter.keyOf( list.get( mid ) );
			
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
		return -( low + 1 ); // key not found
	}
	
	private static <T>int iteratorBinarySearch( List<? extends T> list , double key , DoubleKeyGetter<T> keyGetter )
	{
		int low = 0;
		int high = list.size( ) - 1;
		ListIterator<? extends T> i = list.listIterator( );
		
		while( low <= high )
		{
			int mid = ( low + high ) >>> 1;
			double midVal = keyGetter.keyOf( get( i , mid ) );
			
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
		return -( low + 1 ); // key not found
	}
	
	/**
	 * Gets the ith element from the given list by repositioning the specified
	 * list listIterator.
	 */
	private static <T>T get( ListIterator<? extends T> i , int index )
	{
		T obj = null;
		int pos = i.nextIndex( );
		if( pos <= index )
		{
			do
			{
				obj = i.next( );
			}
			while( pos++ < index );
		}
		else
		{
			do
			{
				obj = i.previous( );
			}
			while( --pos > index );
		}
		return obj;
	}
	
	/**
	 * Searches the specified list for the specified object using the binary
	 * search algorithm. The list must be sorted into ascending order
	 * according to the {@linkplain Comparable natural ordering} of its
	 * elements (as by the {@link #sort(List)} method) prior to making this
	 * call. If it is not sorted, the results are undefined. If the list
	 * contains multiple elements equal to the specified object, there is no
	 * guarantee which one will be found.
	 * 
	 * <p>
	 * This method runs in log(n) time for a "random access" list (which provides near-constant-time positional access). If the specified list does not
	 * implement the {@link RandomAccess} interface and is large, this method will do an iterator-based binary search that performs O(n) link traversals and
	 * O(log n) element comparisons.
	 * 
	 * @param list
	 *            the list to be searched.
	 * @param key
	 *            the key to be searched for.
	 * @return the index of the search key, if it is contained in the list;
	 *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
	 *         <i>insertion point</i> is defined as the point at which the
	 *         key would be inserted into the list: the index of the first
	 *         element greater than the key, or <tt>list.size()</tt> if all
	 *         elements in the list are less than the specified key. Note
	 *         that this guarantees that the return value will be &gt;= 0 if
	 *         and only if the key is found.
	 * @throws ClassCastException
	 *             if the list contains elements that are not
	 *             <i>mutually comparable</i> (for example, strings and
	 *             integers), or the search key is not mutually comparable
	 *             with the elements of the list.
	 */
	public static int binarySearch( List<Double> list , double key )
	{
		if( list instanceof RandomAccess || list.size( ) < BINARYSEARCH_THRESHOLD )
			return indexedBinarySearch( list , key );
		else
			return iteratorBinarySearch( list , key );
	}
	
	private static int indexedBinarySearch( List<Double> list , double key )
	{
		int low = 0;
		int high = list.size( ) - 1;
		
		while( low <= high )
		{
			int mid = ( low + high ) >>> 1;
			double midVal = list.get( mid );
			
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
		return -( low + 1 ); // key not found
	}
	
	private static int iteratorBinarySearch( List<Double> list , double key )
	{
		int low = 0;
		int high = list.size( ) - 1;
		ListIterator<Double> i = list.listIterator( );
		
		while( low <= high )
		{
			int mid = ( low + high ) >>> 1;
			double midVal = get( i , mid );
			
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
		return -( low + 1 ); // key not found
	}
}
