
package org.andork.torquescape.model;

import java.util.Arrays;

public class UVIntersector
{
	public static void main( String[ ] args )
	{
		UVIntersector intersector = new UVIntersector( );
		intersector.intersect( 0.25 , 0.25 , -0.1 , 0.3 );
		
		System.out.println( Arrays.toString( intersector.t ) );
		System.out.println( intersector.u );
		System.out.println( intersector.v );
	}
	
	final double[ ]	t			= new double[ 3 ];
	final int[ ]	edgeIndices	= new int[ 3 ];
	double			u;
	double			v;
	
	int compare( double t0 , double t1 )
	{
		if( Double.isNaN( t0 ) || Double.isInfinite( t0 ) )
		{
			return Double.isNaN( t1 ) || Double.isInfinite( t1 ) ? 0 : 1;
		}
		else
		{
			if( t0 < 0 )
			{
				return t1 < 0 ? Double.compare( t1 , t0 ) : 1;
			}
			else
			{
				return t1 < 0 ? -1 : Double.compare( t0 , t1 );
			}
		}
	}
	
	void swapIfNecessary( double[ ] t , int[ ] sides , int i , int j )
	{
		if( i > j != compare( t[ i ] , t[ j ] ) > 0 )
		{
			double swap = t[ i ];
			t[ i ] = t[ j ];
			t[ j ] = swap;
			
			int swapi = sides[ i ];
			sides[ i ] = sides[ j ];
			sides[ j ] = swapi;
		}
	}
	
	public void intersect( double u0 , double v0 , double u , double v )
	{
		t[ 0 ] = u >= 0 ? Double.NaN : -u0 / u;
		t[ 1 ] = v >= 0 ? Double.NaN : -v0 / v;
		t[ 2 ] = u + v <= 0 ? Double.NaN : ( 1 - u0 - v0 ) / ( u + v );
		
		edgeIndices[ 0 ] = 2;
		edgeIndices[ 1 ] = 0;
		edgeIndices[ 2 ] = 1;
		
		swapIfNecessary( t , edgeIndices , 0 , 1 );
		swapIfNecessary( t , edgeIndices , 1 , 2 );
		swapIfNecessary( t , edgeIndices , 0 , 1 );
		
		this.u = u0 + t[ 0 ] * u;
		this.v = v0 + t[ 0 ] * v;
	}
}
