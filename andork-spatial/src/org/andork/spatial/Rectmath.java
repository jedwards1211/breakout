package org.andork.spatial;

import java.util.Arrays;

public class Rectmath
{
	private Rectmath( )
	{
		
	}
	
	public static String prettyPrint( double[ ] r , String elemFormat )
	{
		int d = r.length / 2;
		StringBuffer sb = new StringBuffer( "[ " );
		for( int i = 0 ; i < d - 1 ; i++ )
		{
			sb.append( String.format( elemFormat , r[ i ] ) ).append( " - " )
					.append( String.format( elemFormat , r[ i + d ] ) ).append( " , " );
		}
		sb.append( String.format( elemFormat , r[ d - 1 ] ) ).append( " - " )
				.append( String.format( elemFormat , r[ d * 2 - 1 ] ) ).append( " ]" );
		return sb.toString( );
	}
	
	public static double[ ] voidRectd( int dimension )
	{
		double[ ] r = new double[ dimension * 2 ];
		Arrays.fill( r , Double.NaN );
		return r;
	}
	
	public static double nmin( double a , double b )
	{
		return a < b || Double.isNaN( b ) ? a : b;
	}
	
	public static double nmax( double a , double b )
	{
		return a > b || Double.isNaN( b ) ? a : b;
	}
	
	public static void union3( double[ ] r1 , double[ ] r2 , double[ ] rout )
	{
		rout[ 0 ] = nmin( r1[ 0 ] , r2[ 0 ] );
		rout[ 1 ] = nmin( r1[ 1 ] , r2[ 1 ] );
		rout[ 2 ] = nmin( r1[ 2 ] , r2[ 2 ] );
		rout[ 3 ] = nmax( r1[ 3 ] , r2[ 3 ] );
		rout[ 4 ] = nmax( r1[ 4 ] , r2[ 4 ] );
		rout[ 5 ] = nmax( r1[ 5 ] , r2[ 5 ] );
	}
	
	public static void union( double[ ] r1 , double[ ] r2 , double[ ] rout )
	{
		int d = r1.length / 2;
		for( int i = 0 ; i < d ; i++ )
		{
			int j = i + d;
			rout[ i ] = nmin( r1[ i ] , r2[ i ] );
			rout[ j ] = nmax( r1[ j ] , r2[ j ] );
		}
	}
	
	public static void punion3( double[ ] r , double[ ] p , double[ ] rout )
	{
		rout[ 0 ] = nmin( r[ 0 ] , p[ 0 ] );
		rout[ 1 ] = nmin( r[ 1 ] , p[ 1 ] );
		rout[ 2 ] = nmin( r[ 2 ] , p[ 2 ] );
		rout[ 3 ] = nmax( r[ 3 ] , p[ 0 ] );
		rout[ 4 ] = nmax( r[ 4 ] , p[ 1 ] );
		rout[ 5 ] = nmax( r[ 5 ] , p[ 2 ] );
	}
	
	public static void ppunion3( double[ ] p1 , double[ ] p2 , double[ ] rout )
	{
		rout[ 0 ] = nmin( p1[ 0 ] , p2[ 0 ] );
		rout[ 1 ] = nmin( p1[ 1 ] , p2[ 1 ] );
		rout[ 2 ] = nmin( p1[ 2 ] , p2[ 2 ] );
		rout[ 3 ] = nmax( p1[ 0 ] , p2[ 0 ] );
		rout[ 4 ] = nmax( p1[ 1 ] , p2[ 1 ] );
		rout[ 5 ] = nmax( p1[ 2 ] , p2[ 2 ] );
	}
	
	public static boolean intersects3( double[ ] r1 , double[ ] r2 )
	{
		return r1[ 0 ] <= r2[ 3 ] && r1[ 1 ] <= r2[ 4 ] && r1[ 2 ] <= r2[ 5 ] &&
				r1[ 3 ] >= r2[ 0 ] && r1[ 4 ] >= r2[ 1 ] && r1[ 5 ] >= r2[ 2 ];
	}
	
	public static boolean intersects( double[ ] r1 , double[ ] r2 )
	{
		int d = r1.length / 2;
		for( int i = 0 ; i < d ; i++ )
		{
			int j = i + d;
			if( r1[ i ] > r2[ i ] || r1[ j ] < r2[ j ] )
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean overlaps3( double[ ] r1 , double[ ] r2 )
	{
		return r1[ 0 ] < r2[ 3 ] && r1[ 1 ] < r2[ 4 ] && r1[ 2 ] < r2[ 5 ] &&
				r1[ 3 ] > r2[ 0 ] && r1[ 4 ] > r2[ 1 ] && r1[ 5 ] > r2[ 2 ];
	}
	
	public static double overlap3( double[ ] r1 , double[ ] r2 )
	{
		double xmin = nmax( r1[ 0 ] , r2[ 0 ] );
		double ymin = nmax( r1[ 1 ] , r2[ 1 ] );
		double zmin = nmax( r1[ 2 ] , r2[ 2 ] );
		double xmax = nmin( r1[ 3 ] , r2[ 3 ] );
		double ymax = nmin( r1[ 4 ] , r2[ 4 ] );
		double zmax = nmin( r1[ 5 ] , r2[ 5 ] );
		
		if( xmax < xmin || ymax < ymin || zmax < zmin )
		{
			return 0;
		}
		
		double x = xmax - xmin;
		double y = ymax - ymin;
		double z = zmax - zmin;
		
		return x * y * z;
	}
	
	public static double volume( double[ ] r )
	{
		double x = r[ 3 ] - r[ 0 ];
		double y = r[ 4 ] - r[ 1 ];
		double z = r[ 5 ] - r[ 2 ];
		return x * y * z;
	}
	
	public static double enlargement3( double[ ] r , double[ ] radded )
	{
		double volume = volume( r );
		
		double xmin = nmin( r[ 0 ] , radded[ 0 ] );
		double ymin = nmin( r[ 1 ] , radded[ 1 ] );
		double zmin = nmin( r[ 2 ] , radded[ 2 ] );
		double xmax = nmax( r[ 3 ] , radded[ 3 ] );
		double ymax = nmax( r[ 4 ] , radded[ 4 ] );
		double zmax = nmax( r[ 5 ] , radded[ 5 ] );
		
		double x = xmax - xmin;
		double y = ymax - ymin;
		double z = zmax - zmin;
		
		return x * y * z - volume;
	}
	
	public static boolean contains3( double[ ] r , double[ ] p )
	{
		return p[ 0 ] >= r[ 0 ] && p[ 0 ] <= r[ 3 ] &&
				p[ 1 ] >= r[ 1 ] && p[ 1 ] <= r[ 4 ] &&
				p[ 2 ] >= r[ 2 ] && p[ 2 ] <= r[ 5 ];
	}
	
	public static boolean rayIntersects( double[ ] rayOrigin , double[ ] rayDirection , double[ ] rect )
	{
		for( int d = 0 ; d < 3 ; d++ )
		{
			if( ( rayOrigin[ d ] <= rect[ d ] && rayDirection[ d ] < 0 ) || ( rayOrigin[ d ] >= rect[ d + 3 ] && rayDirection[ d ] > 0 ) )
			{
				return false;
			}
		}
		
		for( int d0 = 0 ; d0 < 3 ; d0++ )
		{
			if( rayDirection[ d0 ] == 0 )
			{
				if( rayOrigin[ d0 ] < rect[ d0 ] || rayOrigin[ d0 ] > rect[ d0 + 3 ] )
				{
					return false;
				}
				continue;
			}
			
			double l0;
			
			if( rayOrigin[ d0 ] <= rect[ d0 ] )
			{
				l0 = rect[ d0 ] - rayOrigin[ d0 ];
			}
			else if( rayOrigin[ d0 ] >= rect[ d0 + 3 ] )
			{
				l0 = rect[ d0 + 3 ] - rayOrigin[ d0 ];
			}
			else
			{
				continue;
			}
			
			for( int i = 1 ; i < 3 ; i++ )
			{
				int d1 = ( d0 + i ) % 3;
				double l1 = rayDirection[ d1 ] * l0 / rayDirection[ d0 ];
				if( ( rayOrigin[ d1 ] <= rect[ d1 + 3 ] && rayOrigin[ d1 ] + l1 > rect[ d1 + 3 ] ) ||
						( rayOrigin[ d1 ] >= rect[ d1 ] && rayOrigin[ d1 ] + l1 < rect[ d1 ] ) )
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	// ///////////////////////////////////////////////////////////////////////////
	// FLOAT METHODS
	// ///////////////////////////////////////////////////////////////////////////
	public static String prettyPrint( float[ ] r , String elemFormat )
	{
		int d = r.length / 2;
		StringBuffer sb = new StringBuffer( "[ " );
		for( int i = 0 ; i < d - 1 ; i++ )
		{
			sb.append( String.format( elemFormat , r[ i ] ) ).append( " - " )
					.append( String.format( elemFormat , r[ i + d ] ) ).append( " , " );
		}
		sb.append( String.format( elemFormat , r[ d - 1 ] ) ).append( " - " )
				.append( String.format( elemFormat , r[ d * 2 - 1 ] ) ).append( " ]" );
		return sb.toString( );
	}
	
	public static float[ ] voidRectf( int dimension )
	{
		float[ ] r = new float[ dimension * 2 ];
		Arrays.fill( r , Float.NaN );
		return r;
	}
	
	public static float nmin( float a , float b )
	{
		return a < b || Float.isNaN( b ) ? a : b;
	}
	
	public static float nmax( float a , float b )
	{
		return a > b || Float.isNaN( b ) ? a : b;
	}
	
	public static void union3( float[ ] r1 , float[ ] r2 , float[ ] rout )
	{
		rout[ 0 ] = nmin( r1[ 0 ] , r2[ 0 ] );
		rout[ 1 ] = nmin( r1[ 1 ] , r2[ 1 ] );
		rout[ 2 ] = nmin( r1[ 2 ] , r2[ 2 ] );
		rout[ 3 ] = nmax( r1[ 3 ] , r2[ 3 ] );
		rout[ 4 ] = nmax( r1[ 4 ] , r2[ 4 ] );
		rout[ 5 ] = nmax( r1[ 5 ] , r2[ 5 ] );
	}
	
	public static void union( float[ ] r1 , float[ ] r2 , float[ ] rout )
	{
		int d = r1.length / 2;
		for( int i = 0 ; i < d ; i++ )
		{
			int j = i + d;
			rout[ i ] = nmin( r1[ i ] , r2[ i ] );
			rout[ j ] = nmax( r1[ j ] , r2[ j ] );
		}
	}
	
	public static void punion3( float[ ] r , float[ ] p , float[ ] rout )
	{
		rout[ 0 ] = nmin( r[ 0 ] , p[ 0 ] );
		rout[ 1 ] = nmin( r[ 1 ] , p[ 1 ] );
		rout[ 2 ] = nmin( r[ 2 ] , p[ 2 ] );
		rout[ 3 ] = nmax( r[ 3 ] , p[ 0 ] );
		rout[ 4 ] = nmax( r[ 4 ] , p[ 1 ] );
		rout[ 5 ] = nmax( r[ 5 ] , p[ 2 ] );
	}
	
	public static void ppunion3( float[ ] p1 , float[ ] p2 , float[ ] rout )
	{
		rout[ 0 ] = nmin( p1[ 0 ] , p2[ 0 ] );
		rout[ 1 ] = nmin( p1[ 1 ] , p2[ 1 ] );
		rout[ 2 ] = nmin( p1[ 2 ] , p2[ 2 ] );
		rout[ 3 ] = nmax( p1[ 0 ] , p2[ 0 ] );
		rout[ 4 ] = nmax( p1[ 1 ] , p2[ 1 ] );
		rout[ 5 ] = nmax( p1[ 2 ] , p2[ 2 ] );
	}
	
	public static boolean intersects3( float[ ] r1 , float[ ] r2 )
	{
		return r1[ 0 ] <= r2[ 3 ] && r1[ 1 ] <= r2[ 4 ] && r1[ 2 ] <= r2[ 5 ] &&
				r1[ 3 ] >= r2[ 0 ] && r1[ 4 ] >= r2[ 1 ] && r1[ 5 ] >= r2[ 2 ];
	}
	
	public static boolean intersects( float[ ] r1 , float[ ] r2 )
	{
		int d = r1.length / 2;
		for( int i = 0 ; i < d ; i++ )
		{
			int j = i + d;
			if( r1[ i ] > r2[ i ] || r1[ j ] < r2[ j ] )
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean overlaps3( float[ ] r1 , float[ ] r2 )
	{
		return r1[ 0 ] < r2[ 3 ] && r1[ 1 ] < r2[ 4 ] && r1[ 2 ] < r2[ 5 ] &&
				r1[ 3 ] > r2[ 0 ] && r1[ 4 ] > r2[ 1 ] && r1[ 5 ] > r2[ 2 ];
	}
	
	public static float overlap3( float[ ] r1 , float[ ] r2 )
	{
		float xmin = nmax( r1[ 0 ] , r2[ 0 ] );
		float ymin = nmax( r1[ 1 ] , r2[ 1 ] );
		float zmin = nmax( r1[ 2 ] , r2[ 2 ] );
		float xmax = nmin( r1[ 3 ] , r2[ 3 ] );
		float ymax = nmin( r1[ 4 ] , r2[ 4 ] );
		float zmax = nmin( r1[ 5 ] , r2[ 5 ] );
		
		if( xmax < xmin || ymax < ymin || zmax < zmin )
		{
			return 0;
		}
		
		float x = xmax - xmin;
		float y = ymax - ymin;
		float z = zmax - zmin;
		
		return x * y * z;
	}
	
	public static float volume( float[ ] r )
	{
		float x = r[ 3 ] - r[ 0 ];
		float y = r[ 4 ] - r[ 1 ];
		float z = r[ 5 ] - r[ 2 ];
		return x * y * z;
	}
	
	public static float enlargement3( float[ ] r , float[ ] radded )
	{
		float volume = volume( r );
		
		float xmin = nmin( r[ 0 ] , radded[ 0 ] );
		float ymin = nmin( r[ 1 ] , radded[ 1 ] );
		float zmin = nmin( r[ 2 ] , radded[ 2 ] );
		float xmax = nmax( r[ 3 ] , radded[ 3 ] );
		float ymax = nmax( r[ 4 ] , radded[ 4 ] );
		float zmax = nmax( r[ 5 ] , radded[ 5 ] );
		
		float x = xmax - xmin;
		float y = ymax - ymin;
		float z = zmax - zmin;
		
		return x * y * z - volume;
	}
	
	public static boolean contains3( float[ ] r , float[ ] p )
	{
		return p[ 0 ] >= r[ 0 ] && p[ 0 ] <= r[ 3 ] &&
				p[ 1 ] >= r[ 1 ] && p[ 1 ] <= r[ 4 ] &&
				p[ 2 ] >= r[ 2 ] && p[ 2 ] <= r[ 5 ];
	}
	
	public static boolean rayIntersects( float[ ] rayOrigin , float[ ] rayDirection , float[ ] rect )
	{
		for( int d = 0 ; d < 3 ; d++ )
		{
			if( ( rayOrigin[ d ] <= rect[ d ] && rayDirection[ d ] < 0 ) || ( rayOrigin[ d ] >= rect[ d + 3 ] && rayDirection[ d ] > 0 ) )
			{
				return false;
			}
		}
		
		for( int d0 = 0 ; d0 < 3 ; d0++ )
		{
			if( rayDirection[ d0 ] == 0 )
			{
				if( rayOrigin[ d0 ] < rect[ d0 ] || rayOrigin[ d0 ] > rect[ d0 + 3 ] )
				{
					return false;
				}
				continue;
			}
			
			float l0;
			
			if( rayOrigin[ d0 ] <= rect[ d0 ] )
			{
				l0 = rect[ d0 ] - rayOrigin[ d0 ];
			}
			else if( rayOrigin[ d0 ] >= rect[ d0 + 3 ] )
			{
				l0 = rect[ d0 + 3 ] - rayOrigin[ d0 ];
			}
			else
			{
				continue;
			}
			
			for( int i = 1 ; i < 3 ; i++ )
			{
				int d1 = ( d0 + i ) % 3;
				float l1 = rayDirection[ d1 ] * l0 / rayDirection[ d0 ];
				if( ( rayOrigin[ d1 ] <= rect[ d1 + 3 ] && rayOrigin[ d1 ] + l1 > rect[ d1 + 3 ] ) ||
						( rayOrigin[ d1 ] >= rect[ d1 ] && rayOrigin[ d1 ] + l1 < rect[ d1 ] ) )
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
}