package org.andork.torquescape.model;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.torquescape.model.render.TriangleRenderingInfo;
import org.andork.vecmath.VecmathUtils;

public class Triangle
{
	/**
	 * Made public for speed of access.  DO NOT MODIFY ITS COORDINATES!
	 */
	public final Point3f			p0;
	/**
	 * Made public for speed of access.  DO NOT MODIFY ITS COORDINATES!
	 */
	public final Point3f			p1;
	/**
	 * Made public for speed of access.  DO NOT MODIFY ITS COORDINATES!
	 */
	public final Point3f			p2;
	
	public Vector3f					n0;
	public Vector3f					n1;
	public Vector3f					n2;
	
	public TriangleRenderingInfo	ri;
	
	public Triangle( Point3f p0 , Point3f p1 , Point3f p2 )
	{
		this( true , p0 , p1 , p2 );
	}
	
	public Triangle( Point3f p0 , Point3f p1 , Point3f p2 , TriangleRenderingInfo ri )
	{
		this( true , p0 , p1 , p2 , ri );
	}
	
	public Triangle( Point3f p0 , Point3f p1 , Point3f p2 , Vector3f n0 , Vector3f n1 , Vector3f n2 )
	{
		this( true , p0 , p1 , p2 , n0 , n1 , n2 );
	}
	
	Triangle( boolean checkValidity , Point3f p0 , Point3f p1 , Point3f p2 )
	{
		if( checkValidity )
		{
			checkValid( p0 , p1 , p2 );
		}
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
	}
	
	Triangle( boolean checkValidity , Point3f p0 , Point3f p1 , Point3f p2 , TriangleRenderingInfo ri )
	{
		this( checkValidity , p0 , p1 , p2 );
		this.ri = ri;
	}
	
	Triangle( boolean checkValidity , Point3f p0 , Point3f p1 , Point3f p2 , Vector3f n0 , Vector3f n1 , Vector3f n2 , TriangleRenderingInfo ri )
	{
		this( checkValidity , p0 , p1 , p2 , ri );
		this.n0 = n0;
		this.n1 = n1;
		this.n2 = n2;
	}
	
	Triangle( boolean checkValidity , Point3f p0 , Point3f p1 , Point3f p2 , Vector3f n0 , Vector3f n1 , Vector3f n2 )
	{
		this( checkValidity , p0 , p1 , p2 );
		this.n0 = n0;
		this.n1 = n1;
		this.n2 = n2;
	}
	
	public static void checkValid( Point3f p0 , Point3f p1 , Point3f p2 )
	{
		VecmathUtils.checkReal( p0 );
		VecmathUtils.checkReal( p1 );
		VecmathUtils.checkReal( p2 );
		
		if( p0.equals( p1 ) || p0.equals( p2 ) || p1.equals( p2 ) )
		{
			throw new IllegalArgumentException( "points must all be distinct" );
		}
		
		float a1 = p1.x - p0.x;
		float a2 = p1.y - p0.y;
		float a3 = p1.z - p0.z;
		
		float b1 = p2.x - p0.x;
		float b2 = p2.y - p0.y;
		float b3 = p2.z - p0.z;
		
		float c1 = a2 * b3 - a3 * b2;
		float c2 = a3 * b1 - a1 * b3;
		float c3 = a1 * b2 - a2 * b1;
		
		if( c1 == 0 && c2 == 0 && c3 == 0 )
		{
			throw new IllegalArgumentException( "points must not be colinear" );
		}
	}
	
	public Triangle canonical( )
	{
		int i0 = 0;
		int i1 = 1;
		int i2 = 2;
		if( CanonicalPoint3fOrder.INSTANCE.compare( p0 , p1 ) > 0 )
		{
			i0 = 1;
			i1 = 0;
		}
		if( CanonicalPoint3fOrder.INSTANCE.compare( getPoint( i1 ) , p2 ) > 0 )
		{
			int swap = i1;
			i1 = i2;
			i2 = swap;
		}
		if( CanonicalPoint3fOrder.INSTANCE.compare( getPoint( i0 ) , getPoint( i1 ) ) > 0 )
		{
			int swap = i0;
			i0 = i1;
			i1 = swap;
		}
		return new Triangle( false , getPoint( i0 ) , getPoint( i1 ) , getPoint( i2 ) , getNormal( i0 ) , getNormal( i1 ) , getNormal( i2 ) );
	}
	
	public Triangle reverse( )
	{
		return new Triangle( false , p2 , p1 , p0 , n2 , n1 , n0 );
	}
	
	public Triangle rotate( int amount )
	{
		return new Triangle( false , getPoint( amount % 3 ) , getPoint( ( amount + 1 ) % 3 ) , getPoint( ( amount + 2 ) % 3 ) , getNormal( amount % 3 ) , getNormal( ( amount + 1 ) % 3 ) , getNormal( ( amount + 2 ) % 3 ) );
	}
	
	public Triangle reorder( Point3f newP0 , Point3f newP1 )
	{
		if( newP0.equals( newP1 ) )
		{
			throw new IllegalArgumentException( "newP0 and newP1 must be different" );
		}
		
		Point3f newP2 = null;
		Vector3f newN0 = null;
		Vector3f newN1 = null;
		Vector3f newN2 = null;
		boolean p0found = false;
		boolean p1found = false;
		
		for( int i = 0 ; i < 3 ; i++ )
		{
			Point3f p = getPoint( i );
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
	
	public Point3f getPoint( int index )
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
	
	public int indexOf( Point3f p )
	{
		if( p0.equals( p ) )
		{
			return 0;
		}
		if( p1.equals( p ) )
		{
			return 1;
		}
		if( p2.equals( p ) )
		{
			return 2;
		}
		return -1;
	}
	
	public Vector3f getNormal( int index )
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
	
	public void setNormal( int index , Vector3f normal )
	{
		switch( index )
		{
			case 0:
				n0 = normal;
				break;
			case 1:
				n1 = normal;
				break;
			case 2:
				n2 = normal;
				break;
			default:
				throw new IllegalArgumentException( "indes must be between 0 and 2 inclusive" );
		}
	}
	
	public Point3f getOtherPoint( Point3f point1 , Point3f point2 , Point3f result )
	{
		if( !p0.equals( point1 ) && !p0.equals( point2 ) )
		{
			result.set( p0 );
		}
		if( !p1.equals( point1 ) && !p1.equals( point2 ) )
		{
			result.set( p1 );
		}
		result.set( p2 );
		
		return result;
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
	
	public TriangleRenderingInfo getRenderingInfo( )
	{
		return ri;
	}
	
	public void setRenderingInfo( TriangleRenderingInfo ri )
	{
		this.ri = ri;
	}
	
	public void getDefaultNormal( Vector3f out )
	{
		float bx = p1.x - p0.x;
		float by = p1.y - p0.y;
		float bz = p1.z - p0.z;
		
		float cx = p2.x - p0.x;
		float cy = p2.y - p0.y;
		float cz = p2.z - p0.z;
		
		float ax = by * cz - bz * cy;
		float ay = bz * cx - bx * cz;
		float az = bx * cy - by * cx;
		
		float scale = ( float ) ( 1.0 / Math.sqrt( ax * ax + ay * ay + az * az ) );
		
		out.x = ( float ) ( ax * scale );
		out.y = ( float ) ( ay * scale );
		out.z = ( float ) ( az * scale );
	}
	
	public static void calcFrontFaceDirection( Point3f p0 , Point3f p1 , Point3f p2 , J3DTempsPool pool , Vector3f out )
	{
		Vector3f v1 = pool.getVector3f( );
		
		v1.set( p1.x - p0.x , p1.y - p0.y , p1.z - p0.z );
		out.set( p2.x - p0.x , p2.y - p0.y , p2.z - p0.z );
		out.cross( v1 , out );
		
		pool.release( v1 );
	}
}
