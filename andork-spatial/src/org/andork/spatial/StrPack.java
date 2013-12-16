package org.andork.spatial;

import static org.andork.spatial.Rectmath.union;
import static org.andork.spatial.Rectmath.voidRectd;
import static org.andork.spatial.Rectmath.voidRectf;

import java.util.Arrays;
import java.util.Comparator;

import junit.framework.ComparisonCompactor;

public class StrPack
{
	private StrPack( )
	{
		
	}
	
	private static int ceilDiv( int a , int b )
	{
		return ( a + b - 1 ) / b;
	}
	
	public static <T> RdNode<T> pack( int n , RdNode<T> ... nodes )
	{
		int dimension = nodes[ 0 ].mbr( ).length / 2;
		
		while( nodes.length > 1 )
		{
			str( nodes , 0 , nodes.length , dimension , 0 , n );
			int nBranches = ceilDiv( nodes.length , n );
			
			DefaultRdBranch<T>[ ] branches = ( DefaultRdBranch<T>[ ] ) new DefaultRdBranch[ nBranches ];
			
			int b = 0;
			for( int i = 0 ; i < nodes.length ; i += n )
			{
				int nChildren = Math.min( n , nodes.length - i );
				RdNode<T>[ ] children = ( RdNode<T>[ ] ) new RdNode[ nChildren ];
				
				System.arraycopy( nodes , i , children , 0 , nChildren );
				
				double[ ] mbr = voidRectd( dimension );
				
				for( RdNode<T> child : children )
				{
					union( mbr , child.mbr( ) , mbr );
				}
				
				branches[ b++ ] = new DefaultRdBranch<T>( mbr , children );
			}
			nodes = branches;
		}
		return nodes[ 0 ];
	}
	
	private static void str( RdNode<?>[ ] nodes , int from , int to , final int dim , final int axis , int n )
	{
		Arrays.sort( nodes , from , to , new Comparator<RdNode<?>>( )
		{
			@Override
			public int compare( RdNode<?> o1 , RdNode<?> o2 )
			{
				double[ ] r1 = o1.mbr( );
				double[ ] r2 = o2.mbr( );
				return Double.compare( r1[ axis + dim ] + r1[ axis ] , r2[ axis + dim ] + r2[ axis ] );
			}
		} );
		
		if( axis < dim - 1 )
		{
			double k = dim - axis;
			int slabSize = n * ( int ) Math.ceil( Math.pow( ceilDiv( nodes.length , n ) , ( k - 1 ) / k ) );
			
			for( int i = from ; i + slabSize <= to ; i += slabSize )
			{
				str( nodes , i , i + slabSize , dim , axis + 1 , n );
			}
		}
	}
	
	public static <T> RfNode<T> pack( int n , RfNode<T> ... nodes )
	{
		int dimension = nodes[ 0 ].mbr( ).length / 2;
		
		int[ ] axes = new int[ dimension ];
		for( int i = 0 ; i < dimension ; i++ )
		{
			axes[ i ] = i;
		}
		
		while( nodes.length > 1 )
		{
			str2( nodes , 0 , nodes.length , axes , dimension , 0 , n );
			int nBranches = ceilDiv( nodes.length , n );
			
			DefaultRfBranch<T>[ ] branches = ( DefaultRfBranch<T>[ ] ) new DefaultRfBranch[ nBranches ];
			
			int b = 0;
			for( int i = 0 ; i < nodes.length ; i += n )
			{
				int nChildren = Math.min( n , nodes.length - i );
				RfNode<T>[ ] children = ( RfNode<T>[ ] ) new RfNode[ nChildren ];
				
				System.arraycopy( nodes , i , children , 0 , nChildren );
				
				float[ ] mbr = voidRectf( dimension );
				
				for( RfNode<T> child : children )
				{
					union( mbr , child.mbr( ) , mbr );
				}
				
				branches[ b++ ] = new DefaultRfBranch<T>( mbr , children );
			}
			nodes = branches;
		}
		return nodes[ 0 ];
	}
	
	private static void str( RfNode<?>[ ] nodes , int from , int to , final int dim , final int axis , int n )
	{
		Arrays.sort( nodes , from , to , new Comparator<RfNode<?>>( )
		{
			@Override
			public int compare( RfNode<?> o1 , RfNode<?> o2 )
			{
				float[ ] r1 = o1.mbr( );
				float[ ] r2 = o2.mbr( );
				return Double.compare( r1[ axis + dim ] + r1[ axis ] , r2[ axis + dim ] + r2[ axis ] );
			}
		} );
		
		if( axis < dim - 1 )
		{
			float k = dim - axis;
			int slabSize = n * ( int ) Math.ceil( Math.pow( ceilDiv( to - from , n ) , ( k - 1 ) / k ) );
			
			for( int i = from ; i + slabSize <= to ; i += slabSize )
			{
				str( nodes , i , i + slabSize , dim , axis + 1 , n );
			}
		}
	}
	
	private static void str2( RfNode<?>[ ] nodes , int from , int to , final int[ ] axes , final int dim , final int axis , int n )
	{
		float[ ] mbr = voidRectf( dim );
		for( int i = from ; i < to ; i++ )
		{
			union( mbr , nodes[ i ].mbr( ) , mbr );
		}
		
		int maxAxis = -1;
		float maxSpan = Float.NaN;
		
		for( int i = axis ; i < dim ; i++ )
		{
			float span = mbr[ axes[ i ] + dim ] - mbr[ axes[ i ] ];
			if( Float.isNaN( maxSpan ) || span > maxSpan )
			{
				maxAxis = i;
				maxSpan = span;
			}
		}
		
		int temp = axes[ axis ];
		axes[ axis ] = axes[ maxAxis ];
		axes[ maxAxis ] = temp;
		
		final int adjAxis = axes[ axis ];
		
		Arrays.sort( nodes , from , to , new Comparator<RfNode<?>>( )
		{
			@Override
			public int compare( RfNode<?> o1 , RfNode<?> o2 )
			{
				float[ ] r1 = o1.mbr( );
				float[ ] r2 = o2.mbr( );
				return Double.compare( r1[ adjAxis + dim ] + r1[ adjAxis ] , r2[ adjAxis + dim ] + r2[ adjAxis ] );
			}
		} );
		
		if( axis < dim - 1 )
		{
			float k = dim - axis;
			int slabSize = n * ( int ) Math.ceil( Math.pow( ceilDiv( to - from , n ) , ( k - 1 ) / k ) );
			
			for( int i = from ; i + slabSize <= to ; i += slabSize )
			{
				str( nodes , i , i + slabSize , dim , axis + 1 , n );
			}
		}
	}
}
