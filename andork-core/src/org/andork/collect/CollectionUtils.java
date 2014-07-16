package org.andork.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.andork.func.Mapper;
import org.andork.func.Predicate;
import org.andork.util.Java7;

public class CollectionUtils
{
	/**
	 * Finds the closest element to a given value in a list.
	 * 
	 * @param list
	 *        the list to search through, sorted in ascending order.
	 * @param value
	 *        the target value.
	 * @return the element of {@code list} closest to {@code value}, or {@code null} if {@code list} is empty.
	 */
	public static int findClosestIndex( List<Double> list , Double value )
	{
		if( list.isEmpty( ) )
		{
			return -1;
		}
		int index = Collections.binarySearch( list , value );
		if( index >= 0 )
		{
			return index;
		}
		else
		{
			index = -( index + 1 );
			if( index == list.size( ) || ( index > 0 && list.get( index ) - value > value - list.get( index - 1 ) ) )
			{
				index-- ;
			}
			return index;
		}
	}
	
	/**
	 * Finds the index of the first element in the given list of the given type.
	 * 
	 * @param list
	 *        the list to search through.
	 * @param type
	 *        the type to find.
	 * @return the index of the first element in {@code list} that is an instance of {@code type}, or -1 if no such element is in {@code list}.
	 */
	public static <T> int indexOfInstance( List<T> list , Class<? extends T> type )
	{
		int k = 0;
		for( T t : list )
		{
			if( type.isInstance( t ) )
			{
				return k;
			}
			k++ ;
		}
		return -1;
	}
	
	/**
	 * Gets the index of the first element in the given list of the given type.
	 * 
	 * @param list
	 *        the list to search through.
	 * @param type
	 *        the type to find.
	 * @return the first element in {@code list} that is an instance of {@code type}, or {@code null} if no such element is in {@code list}.
	 */
	@SuppressWarnings( "unchecked" )
	public static <T, S extends T> S findInstance( List<T> list , Class<S> type )
	{
		int index = indexOfInstance( list , type );
		return index < 0 ? null : ( S ) list.get( index );
	}
	
	public static <T> void testComparator( List<? extends T> list , Comparator<T> comparator )
	{
		for( int i = 0 ; i < list.size( ) ; i++ )
		{
			for( int j = i ; j < list.size( ) ; j++ )
			{
				T oi = list.get( i );
				T oj = list.get( j );
				
				int ci = comparator.compare( oi , oj );
				int cj = comparator.compare( oj , oi );
				
				if( Math.signum( ci ) != -Math.signum( cj ) )
				{
					throw new RuntimeException( "Bad comparator: " + comparator + "\n\tlist.get(" + i + "): " + oi
							+ "\n\tlist.get(" + j + "): " + oj
							+ "\ncompare(list.get(" + i + "), list.get(" + j + ")): " + ci
							+ "\ncompare(list.get(" + j + "), list.get(" + i + ")): " + cj );
				}
			}
		}
	}
	
	public static <K, V> HashMap<K, V> newHashMap( )
	{
		return new HashMap<K, V>( );
	}
	
	public static <T> ArrayList<T> newArrayList( )
	{
		return new ArrayList<T>( );
	}
	
	public static <K, V> HashMap<V, K> invert( Map<K, V> in )
	{
		HashMap<V, K> result = new HashMap<V, K>( );
		for( Map.Entry<K, V> e : in.entrySet( ) )
		{
			result.put( e.getValue( ) , e.getKey( ) );
		}
		return result;
	}
	
	public static <K, V> void invert( Map<K, V> in , Map<V, K> out )
	{
		for( Map.Entry<K, V> e : in.entrySet( ) )
		{
			out.put( e.getValue( ) , e.getKey( ) );
		}
	}
	
	public static <E> void removeAll( Iterable<E> c , Predicate<E> p )
	{
		Iterator<E> i = c.iterator( );
		while( i.hasNext( ) )
		{
			if( p.eval( i.next( ) ) )
			{
				i.remove( );
			}
		}
	}
	
	public static <E> void removeAll( Iterable<E> c , java.util.function.Predicate<E> p )
	{
		Iterator<E> i = c.iterator( );
		while( i.hasNext( ) )
		{
			if( p.test( i.next( ) ) )
			{
				i.remove( );
			}
		}
	}
	
	public static <T> void addAll( Collection<T> c , Iterable<? extends T> i )
	{
		for( T t : i )
		{
			c.add( t );
		}
	}
	
	public static <T> ArrayList<T> toArrayList( Iterable<? extends T> i )
	{
		ArrayList<T> result = new ArrayList<T>( );
		addAll( result , i );
		return result;
	}
	
	public static <T> List<T> toUnmodifiableList( Iterable<? extends T> i )
	{
		ArrayList<T> result = toArrayList( i );
		result.trimToSize( );
		return Collections.unmodifiableList( result );
	}
	
	public static <T extends Comparable<T>> ArrayList<T> toSortedArrayList( Iterable<? extends T> i )
	{
		ArrayList<T> result = toArrayList( i );
		Collections.sort( result );
		return result;
	}
	
	public static <T> ArrayList<T> toSortedArrayList( Iterable<? extends T> i , Comparator<? super T> comparator )
	{
		ArrayList<T> result = toArrayList( i );
		Collections.sort( result , comparator );
		return result;
	}
	
	/**
	 * Adds the first {@code count} elements of an {@link Iterable} to a {@link Collection}, or all of the elements if there are fewer than {@code count}.
	 * 
	 * @param count
	 *        the maximum number of elements to take.
	 * @param i
	 *        the {@link Iterable} to take elements from.
	 * @param collection
	 *        the {@link Collection} to add the taken elements to.
	 */
	public static <T> void take( int count , Iterable<? extends T> i , Collection<T> collection )
	{
		for( T t : i )
		{
			if( count-- > 0 )
			{
				collection.add( t );
			}
			else
			{
				break;
			}
		}
	}
	
	public static <T> ArrayList<T> asArrayList( T ... elements )
	{
		ArrayList<T> result = new ArrayList<T>( );
		for( T elem : elements )
		{
			result.add( elem );
		}
		return result;
	}
	
	public static <T> HashSet<T> asHashSet( T ... elements )
	{
		HashSet<T> result = new HashSet<T>( );
		for( T elem : elements )
		{
			result.add( elem );
		}
		return result;
	}
	
	public static <T> ArrayList<T> toArrayList( Stream<T> stream )
	{
		ArrayList<T> result = new ArrayList<T>( );
		stream.forEach( t -> result.add( t ) );
		return result;
	}
	
	public static <T> HashSet<T> toHashSet( Stream<T> stream )
	{
		HashSet<T> result = new HashSet<T>( );
		stream.forEach( t -> result.add( t ) );
		return result;
	}
	
	public static <T> Set<T> asImmutableHashSet( T ... elements )
	{
		return Collections.unmodifiableSet( asHashSet( elements ) );
	}
	
	public static <T, E extends T> int indexOf( List<T> list , E element , int startIndex )
	{
		while( startIndex < list.size( ) )
		{
			if( Java7.Objects.equals( list.get( startIndex ) , element ) )
			{
				return startIndex;
			}
			startIndex++ ;
		}
		return -1;
	}
	
	public static <T, O extends Comparable<O>> int binarySearch( List<? extends T> l , Mapper<T, ? extends O> mapper , O key )
	{
		if( l instanceof RandomAccess )
		{
			return indexedBinarySearch( l , 0 , l.size( ) , mapper , key );
		}
		else
		{
			throw new UnsupportedOperationException( "This method currently only supports RandomAccess lists" );
		}
	}
	
	public static <T, O extends Comparable<O>> int binarySearch( List<? extends T> l , int fromIndex , int toIndex , Mapper<T, ? extends O> mapper , O key )
	{
		if( l instanceof RandomAccess )
		{
			return indexedBinarySearch( l , fromIndex , toIndex - 1 , mapper , key );
		}
		else
		{
			throw new UnsupportedOperationException( "This method currently only supports RandomAccess lists" );
		}
	}
	
	private static <T, O extends Comparable<O>> int indexedBinarySearch( List<? extends T> l , int low , int high , Mapper<T, ? extends O> mapper , O key )
	{
		while( low <= high )
		{
			int mid = ( low + high ) >>> 1;
			T midVal = l.get( mid );
			int cmp = mapper.map( midVal ).compareTo( key );
			
			if( cmp < 0 )
				low = mid + 1;
			else if( cmp > 0 )
				high = mid - 1;
			else
				return mid; // key found
		}
		return -( low + 1 ); // key not found
	}
	
	public static <T, O> int binarySearch( List<? extends T> l , Mapper<T, ? extends O> mapper , O key , Comparator<? super O> c )
	{
		if( l instanceof RandomAccess )
		{
			return indexedBinarySearch( l , mapper , key , c );
		}
		else
		{
			throw new UnsupportedOperationException( "This method currently only supports RandomAccess lists" );
		}
	}
	
	private static <T, O> int indexedBinarySearch( List<? extends T> l , Mapper<T, ? extends O> mapper , O key , Comparator<? super O> c )
	{
		int low = 0;
		int high = l.size( ) - 1;
		
		while( low <= high )
		{
			int mid = ( low + high ) >>> 1;
			T midVal = l.get( mid );
			int cmp = c.compare( mapper.map( midVal ) , key );
			
			if( cmp < 0 )
				low = mid + 1;
			else if( cmp > 0 )
				high = mid - 1;
			else
				return mid; // key found
		}
		return -( low + 1 ); // key not found
	}
	
	public static <T, E extends T> int lastIndexOf( List<T> list , E element , int startIndex )
	{
		while( startIndex >= 0 )
		{
			if( Java7.Objects.equals( list.get( startIndex ) , element ) )
			{
				return startIndex;
			}
			startIndex-- ;
		}
		return -1;
	}
	
	public static <K, V> LinkedHashMap<K, V> newLinkedHashMap( )
	{
		return new LinkedHashMap<K, V>( );
	}
	
	public static <T> List<T> nullTolerantUnmodifiableList( List<? extends T> l )
	{
		return l == null ? Collections.<T>emptyList( ) : Collections.unmodifiableList( l );
	}
	
	public static <K, V> HashMap<K, V> mutableSingletonHashMap( K key , V value )
	{
		HashMap<K, V> result = new HashMap<K, V>( );
		result.put( key , value );
		return result;
	}
	
	public static List<Double> toDoubles( Collection<? extends Number> numbers )
	{
		List<Double> result = new ArrayList<Double>( );
		for( Number number : numbers )
		{
			result.add( number.doubleValue( ) );
		}
		return result;
	}
	
	public static <I, O> Iterable<O> map( final Mapper<I, O> mapper , final Iterable<? extends I> in )
	{
		return new Iterable<O>( )
		{
			public Iterator<O> iterator( )
			{
				return new Iterator<O>( )
				{
					Iterator<? extends I>	inIter	= in.iterator( );
					
					public boolean hasNext( )
					{
						return inIter.hasNext( );
					}
					
					public O next( )
					{
						return mapper.map( inIter.next( ) );
					}
					
					public void remove( )
					{
						throw new UnsupportedOperationException( );
					}
				};
			}
		};
	}
	
	public static <I, O> O[ ] map( Mapper<I, O> mapper , I[ ] in )
	{
		O[ ] out = ( O[ ] ) new Object[ in.length ];
		for( int i = 0 ; i < in.length ; i++ )
		{
			out[ i ] = mapper.map( in[ i ] );
		}
		return out;
	}
	
	public static <K, V> Map<K, V> keyify( Stream<? extends V> stream , Function<? super V, K> keyAssigner )
	{
		Map<K, V> result = new LinkedHashMap<>( );
		stream.forEach( v -> result.put( keyAssigner.apply( v ) , v ) );
		return result;
	}
}
