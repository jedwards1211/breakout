package org.andork.torquescape.model;

import java.util.Arrays;

import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.vecmath.VecmathUtils;

public class Triangle
{
	final Point3d	p0;
	final Point3d	p1;
	final Point3d	p2;
	
	final Vector3f	n0;
	final Vector3f	n1;
	final Vector3f	n2;
	
	public Triangle( Point3d p0 , Point3d p1 , Point3d p2 , Vector3f n0 , Vector3f n1 , Vector3f n2 )
	{
		this( true , p0 , p1 , p2 , n0 , n1 , n2 );
	}
	
	public Triangle( Point3d[ ] points , Vector3f[ ] normals )
	{
		this( true , points , normals );
	}
	
	Triangle( boolean checkValidity , Point3d p0 , Point3d p1 , Point3d p2 , Vector3f n0 , Vector3f n1 , Vector3f n2 )
	{
		if( checkValidity )
		{
			checkValid( p0 , p1 , p2 );
		}
		
		this.p0 = new Point3d( p0 );
		this.p1 = new Point3d( p1 );
		this.p2 = new Point3d( p2 );
		this.n0 = new Vector3f( n0 );
		this.n1 = new Vector3f( n1 );
		this.n2 = new Vector3f( n2 );
	}
	
	Triangle( boolean checkValidity , Point3d[ ] points , Vector3f[ ] normals )
	{
		this( checkValidity , points[ 0 ] , points[ 1 ] , points[ 2 ] , normals[ 0 ] , normals[ 1 ] , normals[ 2 ] );
	}
	
	public Triangle( Point3f p0 , Point3f p1 , Point3f p2 , Vector3f n0 , Vector3f n1 , Vector3f n2 )
	{
		this( new Point3d( p0 ) , new Point3d( p1 ) , new Point3d( p2 ) , n0 , n1 , n2 );
	}
	
	public static void checkValid( Point3d p0 , Point3d p1 , Point3d p2 )
	{
		VecmathUtils.checkReal( p0 );
		VecmathUtils.checkReal( p1 );
		VecmathUtils.checkReal( p2 );
		
		if( p0.equals( p1 ) || p0.equals( p2 ) || p1.equals( p2 ) )
		{
			throw new IllegalArgumentException( "points must all be distinct" );
		}
		
		double a1 = p1.x - p0.x;
		double a2 = p1.y - p0.y;
		double a3 = p1.z - p0.z;
		
		double b1 = p2.x - p0.x;
		double b2 = p2.y - p0.y;
		double b3 = p2.z - p0.z;
		
		double c1 = a2 * b3 - a3 * b2;
		double c2 = a3 * b1 - a1 * b3;
		double c3 = a1 * b2 - a2 * b1;
		
		if( c1 == 0 && c2 == 0 && c3 == 0 )
		{
			throw new IllegalArgumentException( "points must not be colinear" );
		}
	}
	
	public Triangle canonical( )
	{
		Point3d[ ] points = getPoints( );
		Arrays.sort( points , CanonicalPoint3dOrder.INSTANCE );
		return reorder( points[ 0 ] , points[ 1 ] );
	}
	
	public Triangle reverse( )
	{
		return new Triangle( false , p2 , p1 , p0 , n2 , n1 , n0 );
	}
	
	public Triangle rotate( int amount )
	{
		return new Triangle( false , getPoint( amount % 3 ) , getPoint( ( amount + 1 ) % 3 ) , getPoint( ( amount + 2 ) % 3 ) , getNormal( amount % 3 ) , getNormal( ( amount + 1 ) % 3 ) , getNormal( ( amount + 2 ) % 3 ) );
	}
	
	public Triangle reverseRotate( int amount )
	{
		return new Triangle( false , getPoint( ( amount + 2 ) % 3 ) , getPoint( ( amount + 1 ) % 3 ) , getPoint( amount % 3 ) , getNormal( ( amount + 2 ) % 3 ) , getNormal( ( amount + 1 ) % 3 ) , getNormal( amount % 3 ) );
	}
	
	public Triangle reorder( Point3d newP0 , Point3d newP1 )
	{
		if( newP0.equals( newP1 ) )
		{
			throw new IllegalArgumentException( "newP0 and newP1 must be different" );
		}
		
		Point3d newP2 = null;
		Vector3f newN0 = null;
		Vector3f newN1 = null;
		Vector3f newN2 = null;
		boolean p0found = false;
		boolean p1found = false;
		
		for( int i = 0 ; i < 3 ; i++ )
		{
			Point3d p = getPoint( i );
			if( p.equals( newP0 ) )
			{
				newN0 = getNormal( i );
				p0found = true;
			}
			else if( p.equals( newP1 ) )
			{
				newN1 = getNormal( i );
				p1found = true;
			}
			else
			{
				newP2 = p;
				newN2 = getNormal( i );
			}
		}
		
		if( !p0found )
		{
			throw new IllegalArgumentException( "newP0 must be one of this triangle's points" );
		}
		if( !p1found )
		{
			throw new IllegalArgumentException( "newP1 must be one of this triangle's points" );
		}
		
		return new Triangle( false , newP0 , newP1 , newP2 , newN0 , newN1 , newN2 );
	}
	
	Point3d getPoint( int index )
	{
		switch( index )
		{
			case 0:
				return p0;
			case 1:
				return p1;
			case 2:
				return p2;
			default:
				throw new IllegalArgumentException( "index must be between 0 and 2 inclusive" );
		}
	}
	
	Vector3f getNormal( int index )
	{
		switch( index )
		{
			case 0:
				return n0;
			case 1:
				return n1;
			case 2:
				return n2;
			default:
				throw new IllegalArgumentException( "index must be between 0 and 2 inclusive" );
		}
	}
	
	public void getPoints( Point3d[ ] result )
	{
		result[ 0 ].set( p0 );
		result[ 1 ].set( p1 );
		result[ 2 ].set( p2 );
	}
	
	public void getNormals( Vector3f[ ] result )
	{
		result[ 0 ].set( n0 );
		result[ 1 ].set( n1 );
		result[ 2 ].set( n2 );
	}
	
	public Point3d[ ] getPoints( )
	{
		return new Point3d[ ] { new Point3d( p0 ) , new Point3d( p1 ) , new Point3d( p2 ) };
	}
	
	public Vector3f[ ] getNormals( )
	{
		return new Vector3f[ ] { new Vector3f( n0 ) , new Vector3f( n1 ) , new Vector3f( n2 ) };
	}
	
	public Edge getEdge( int index )
	{
		switch( index )
		{
			case 0:
				return new Edge( p0 , p1 );
			case 1:
				return new Edge( p1 , p2 );
			case 2:
				return new Edge( p2 , p0 );
			default:
				throw new IllegalArgumentException( "index must be between 0 and 2 inclusive" );
		}
	}
	
	public Edge[ ] getEdges( )
	{
		return new Edge[ ] { new Edge( p0 , p1 ) , new Edge( p1 , p2 ) , new Edge( p2 , p0 ) };
	}
	
	public boolean equals( Triangle o )
	{
		return p0.equals( o.p0 ) && p1.equals( o.p1 ) && p2.equals( o.p2 );
	}
	
	public boolean equals( Object o )
	{
		if( o instanceof Triangle )
		{
			return equals( ( Triangle ) o );
		}
		return false;
	}
	
	public int hashCode( )
	{
		int result = p0.hashCode( );
		result = ( result * 31 ) ^ p1.hashCode( );
		result = ( result * 29 ) ^ p2.hashCode( );
		return result;
	}
	
}
