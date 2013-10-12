package org.andork.torquescape.model;

import java.util.Arrays;

import javax.vecmath.Point3f;

public class Edge
{
	private final float[ ]	points;
	private final int		hashCode;
	
	public Edge( float x0 , float y0 , float z0 , float x1 , float y1 , float z1 )
	{
		points = new float[ ] { x0 , y0 , z0 , x1 , y1 , z1 };
		hashCode = Arrays.hashCode( points );
	}
	
	public Edge( Point3f p0 , Point3f p1 )
	{
		this( p0.x , p0.y , p0.z , p1.x , p1.y , p1.z );
	}
	
	public Edge canonical( )
	{
		if( CanonicalPointOrder.compare( points[ 0 ] , points[ 1 ] , points[ 2 ] , points[ 3 ] , points[ 4 ] , points[ 5 ] ) > 0 )
		{
			return new Edge( points[ 3 ] , points[ 4 ] , points[ 5 ] , points[ 0 ] , points[ 1 ] , points[ 2 ] );
		}
		return this;
	}
	
	public int hashCode( )
	{
		return hashCode;
	}
	
	public boolean equals( Object o )
	{
		if( o instanceof Edge )
		{
			return Arrays.equals( points , ( ( Edge ) o ).points );
		}
		return false;
	}
	
	public void getPoint( int index , float[ ] out )
	{
		out[ 0 ] = points[ index * 3 ];
		out[ 1 ] = points[ index * 3 + 1 ];
		out[ 2 ] = points[ index * 3 + 2 ];
	}
	
	public String toString( )
	{
		return Arrays.toString( points );
	}
}
