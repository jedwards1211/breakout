
package org.andork.torquescape.model.old;

import javax.vecmath.Point3f;

public class Edge
{
	public final Point3f	p0;
	public final Point3f	p1;

	public Edge( Point3f p0 , Point3f p1 )
	{
		if( p0.equals( p1 ) )
		{
			throw new IllegalArgumentException( "p0 and p1 must be different" );
		}
		this.p0 = new Point3f( p0 );
		this.p1 = new Point3f( p1 );
	}
	
	public Edge( Edge other )
	{
		this( other.p0 , other.p1 );
	}
	
	public Edge canonical( )
	{
		if( CanonicalPoint3fOrder.INSTANCE.compare( p0 , p1 ) > 0 )
		{
			return new Edge( p1 , p0 );
		}
		else
		{
			return new Edge( p0 , p1 );
		}
	}
	
	public Edge reverse( )
	{
		return new Edge( p1 , p0 );
	}
	
	public boolean equals( Edge o )
	{
		return p0.equals( o.p0 ) && p1.equals( o.p1 );
	}
	
	public boolean equals( Object o )
	{
		if( o instanceof Edge )
		{
			return equals( ( Edge ) o );
		}
		return false;
	}
	
	public int hashCode( )
	{
		int result = p0.hashCode( );
		result = ( 31 * result ) ^ p1.hashCode( );
		return result;
	}
}
