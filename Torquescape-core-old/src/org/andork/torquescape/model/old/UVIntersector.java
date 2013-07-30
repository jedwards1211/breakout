
package org.andork.torquescape.model.old;


public class UVIntersector
{
	final float[ ]	t			= new float[ 3 ];
	final int[ ]	edgeIndices	= new int[ 3 ];
	float			u;
	float			v;
	
	int compare( float t0 , float t1 )
	{
		if( Float.isNaN( t0 ) || Float.isInfinite( t0 ) )
		{
			return Float.isNaN( t1 ) || Float.isInfinite( t1 ) ? 0 : 1;
		}
		else
		{
			if( t0 < 0 )
			{
				return t1 < 0 ? Float.compare( t1 , t0 ) : 1;
			}
			else
			{
				return t1 < 0 ? -1 : Float.compare( t0 , t1 );
			}
		}
	}
	
	void swapIfNecessary( float[ ] t , int[ ] sides , int i , int j )
	{
		if( i > j != compare( t[ i ] , t[ j ] ) > 0 )
		{
			float swap = t[ i ];
			t[ i ] = t[ j ];
			t[ j ] = swap;
			
			int swapi = sides[ i ];
			sides[ i ] = sides[ j ];
			sides[ j ] = swapi;
		}
	}
	
	public void intersect( float u0 , float v0 , float u , float v )
	{
		t[ 0 ] = u >= 0 ? Float.NaN : -u0 / u;
		t[ 1 ] = v >= 0 ? Float.NaN : -v0 / v;
		t[ 2 ] = u + v <= 0 ? Float.NaN : ( 1 - u0 - v0 ) / ( u + v );
		
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
