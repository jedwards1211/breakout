package org.andork.spatial;

import static org.andork.spatial.Rectmath.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KMeansPack
{
	public static <T> RfNode<T> pack( int n , RfNode<T> ... nodes )
	{
		int dim = nodes[ 0 ].mbr( ).length / 2;
		
		while( nodes.length > 1 )
		{
			List<RfNode<T>>[ ] cells = kmeans( nodes , ceilDiv( nodes.length , n ) );
			
			nodes = new RfNode[ cells.length ];
			
			for( int i = 0 ; i < cells.length ; i++ )
			{
				RfNode<T>[ ] children = cells[ i ].toArray( new RfNode[ cells[ i ].size( ) ] );
				float[ ] mbr = voidRectf( dim );
				for( RfNode<T> child : children )
				{
					union( mbr , child.mbr( ) , mbr );
				}
				
				nodes[ i ] = new DefaultRfBranch<T>( mbr , children );
			}
		}
		return nodes[ 0 ];
	}
	
	private static int ceilDiv( int a , int b )
	{
		return ( a + b - 1 ) / b;
	}
	
	private static <T> List<RfNode<T>>[ ] kmeans( RfNode<T>[ ] nodes , int n )
	{
		int dim = nodes[ 0 ].mbr( ).length / 2;
		float[ ][ ] means = new float[ n ][ dim ];
		List<RfNode<T>>[ ] result = new List[ n ];
		for( int i = 0 ; i < n ; i++ )
		{
			result[ i ] = new ArrayList<RfNode<T>>( );
		}
		
		result[ 0 ].addAll( Arrays.asList( nodes ) );
		Collections.shuffle( result[ 0 ] );
		
		for( int i = 0 ; i < n ; i++ )
		{
			center( result[ 0 ].get( i ).mbr( ) , means[ i ] );
		}
		
		result[ 0 ].clear( );
		
		float[ ] center = new float[ dim ];
		
		boolean keepGoing = true;
		
		while( keepGoing )
		{
			for( int i = 0 ; i < n ; i++ )
			{
				result[ i ].clear( );
			}
			
			for( RfNode<T> node : nodes )
			{
				float[ ] mbr = node.mbr( );
				
				int minMean = 0;
				float minDist = 0;
				for( int i = 0 ; i < n ; i++ )
				{
					float dist = dist( mbr , means[ i ] );
					if( i == 0 || dist < minDist )
					{
						minMean = i;
						minDist = dist;
					}
				}
				
				result[ minMean ].add( node );
			}
			
			keepGoing = false;
			
			for( int i = 0 ; i < n ; i++ )
			{
				Arrays.fill( means[ i ] , 0 );
				for( RfNode<T> node : result[ i ] )
				{
					addCenter( node.mbr( ) , center );
				}
				if( !result[ i ].isEmpty( ) )
				{
					for( int d = 0 ; d < dim ; d++ )
					{
						center[ d ] /= result[ i ].size( );
						if( means[ i ][ d ] != center[ d ] )
						{
							means[ i ][ d ] = center[ d ];
							keepGoing = true;
						}
					}
				}
			}
		}
		
		int emptyCount = 0;
		
		int i = 0;
		while( i < n - emptyCount )
		{
			if( result[ i ].isEmpty( ) )
			{
				emptyCount++ ;
				result[ i ] = result[ n - emptyCount ];
			}
			else
			{
				i++ ;
			}
		}
		
		if( emptyCount > 0 )
		{
			result = Arrays.copyOf( result , result.length - emptyCount );
		}
		
		return result;
	}
	
	private static void center( float[ ] mbr , float[ ] out )
	{
		for( int i = 0 ; i < out.length ; i++ )
		{
			out[ i ] = ( mbr[ i + out.length ] + mbr[ i ] ) * 0.5f;
		}
	}
	
	private static void addCenter( float[ ] mbr , float[ ] out )
	{
		for( int i = 0 ; i < out.length ; i++ )
		{
			out[ i ] += ( mbr[ i + out.length ] + mbr[ i ] ) * 0.5f;
		}
	}
	
	private static float dist( float[ ] mbr , float[ ] center )
	{
		float result = 0;
		
		for( int i = 0 ; i < center.length ; i++ )
		{
			float dist = Math.max( Math.abs( center[ i ] - mbr[ i ] ) , Math.abs( center[ i ] - mbr[ i + center.length ] ) );
			if( i == 0 || dist > result )
			{
				result = dist;
			}
		}
		return result;
	}
}
