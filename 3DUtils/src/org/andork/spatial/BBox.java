package org.andork.spatial;

import javax.vecmath.*;

public class BBox
{
	public static void main( String[ ] args )
	{
		BBox b1 = new BBox( new Point3d( Double.NEGATIVE_INFINITY , Double.NEGATIVE_INFINITY , Double.NEGATIVE_INFINITY ) , new Point3d( 0 , 0 , 0 ) );
		BBox b2 = new BBox( new Point3d( -5 , -5 , -5 ) , new Point3d( -2 , -2 , -2 ) );
		
		System.out.println( b1.contains( b2 ) );
		
		System.out.println( 5 - Double.NEGATIVE_INFINITY );
		
		b1.setCenterRadius( new Point3d( ) , 5 );
		b2.setInfinite( );
		System.out.println( b2.contains( b1 ) );
	}
	
	private final Point3d	lower	= new Point3d( );
	private final Point3d	upper	= new Point3d( );
	
	public BBox( )
	{
		setVoid( );
	}
	
	// Constructor added to take floats.
	public BBox( float[ ] pLow , float[ ] pHigh )
	{
		if( pLow.length != pHigh.length )
			throw new IllegalArgumentException( );
		
		lower.x = Math.min( pLow[ 0 ] , pHigh[ 0 ] );
		lower.y = Math.min( pLow[ 1 ] , pHigh[ 1 ] );
		lower.z = Math.min( pLow[ 2 ] , pHigh[ 2 ] );
		upper.x = Math.max( pLow[ 0 ] , pHigh[ 0 ] );
		upper.y = Math.max( pLow[ 1 ] , pHigh[ 1 ] );
		upper.z = Math.max( pLow[ 2 ] , pHigh[ 2 ] );
	}
	
	public BBox( double[ ] pLow , double[ ] pHigh )
	{
		if( pLow.length != pHigh.length )
			throw new IllegalArgumentException( );
		
		lower.y = Math.min( pLow[ 1 ] , pHigh[ 1 ] );
		lower.z = Math.min( pLow[ 2 ] , pHigh[ 2 ] );
		upper.x = Math.max( pLow[ 0 ] , pHigh[ 0 ] );
		upper.y = Math.max( pLow[ 1 ] , pHigh[ 1 ] );
		upper.z = Math.max( pLow[ 2 ] , pHigh[ 2 ] );
	}
	
	public BBox( Point3d c1 , Point3d c2 )
	{
		lower.x = Math.min( c1.x , c2.x );
		lower.y = Math.min( c1.y , c2.y );
		lower.z = Math.min( c1.z , c2.z );
		upper.x = Math.max( c1.x , c2.x );
		upper.y = Math.max( c1.y , c2.y );
		upper.z = Math.max( c1.z , c2.z );
	}
	
	public BBox( BBox r )
	{
		lower.set( r.lower );
		upper.set( r.upper );
	}
	
	public boolean equals( Object o )
	{
		if( o instanceof BBox )
		{
			BBox r = ( BBox ) o;
			return lower.equals( r.lower ) && upper.equals( r.upper );
		}
		return false;
	}
	
	//
	// Cloneable interface
	//
	
	public BBox clone( )
	{
		return new BBox( this );
	}
	
	public void setInfinite( )
	{
		lower.set( Double.NEGATIVE_INFINITY , Double.NEGATIVE_INFINITY , Double.NEGATIVE_INFINITY );
		upper.set( Double.POSITIVE_INFINITY , Double.POSITIVE_INFINITY , Double.POSITIVE_INFINITY );
	}
	
	public void setVoid( )
	{
		lower.set( Double.NaN , Double.NaN , Double.NaN );
		upper.set( Double.NaN , Double.NaN , Double.NaN );
	}
	
	public boolean isVoid( )
	{
		return Double.isNaN( lower.x ) || Double.isNaN( lower.y ) || Double.isNaN( lower.z ) || Double.isNaN( upper.x ) || Double.isNaN( upper.y ) || Double.isNaN( upper.z );
	}
	
	public Point3d getCenter( Point3d buffer )
	{
		buffer.interpolate( lower , upper , 0.5 );
		return buffer;
	}
	
	public int getDimension( )
	{
		return 3;
	}
	
	public double getVolume( )
	{
		return ( upper.x - lower.x ) * ( upper.y - lower.y ) * ( upper.z - lower.z );
	}
	
	public boolean intersects( BBox r )
	{
		return lower.x <= r.upper.x && lower.y <= r.upper.y && lower.z <= r.upper.z && upper.x >= r.lower.x && upper.y >= r.lower.y && upper.z >= r.lower.z;
	}
	
	public boolean contains( BBox r )
	{
		return lower.x <= r.lower.x && upper.x >= r.upper.x && lower.y <= r.lower.y && upper.y >= r.upper.y && lower.z <= r.lower.z && upper.z >= r.upper.z;
	}
	
	public boolean contains( Point3d p )
	{
		return p.x >= lower.x && p.x <= upper.x && p.y >= lower.y && p.y <= upper.y && p.z >= lower.z && p.z <= upper.z;
	}
	
	public void setLower( Point3d newLoc )
	{
		lower.x = Math.min( upper.x , newLoc.x );
		lower.y = Math.min( upper.y , newLoc.y );
		lower.z = Math.min( upper.z , newLoc.z );
		upper.x = Math.max( upper.x , newLoc.x );
		upper.y = Math.max( upper.y , newLoc.y );
		upper.z = Math.max( upper.z , newLoc.z );
	}
	
	public void setUpper( Point3d newLoc )
	{
		upper.x = Math.max( lower.x , newLoc.x );
		upper.y = Math.max( lower.y , newLoc.y );
		upper.z = Math.max( lower.z , newLoc.z );
		lower.x = Math.min( lower.x , newLoc.x );
		lower.y = Math.min( lower.y , newLoc.y );
		lower.z = Math.min( lower.z , newLoc.z );
	}
	
	public void setLower( int index , double value )
	{
		switch( index )
		{
			case 0:
				lower.x = value;
				if( lower.x > upper.x )
				{
					lower.x = upper.x;
					upper.x = value;
				}
				break;
			case 1:
				lower.y = value;
				if( lower.y > upper.y )
				{
					lower.y = upper.y;
					upper.y = value;
				}
				break;
			case 2:
				lower.z = value;
				if( lower.z > upper.z )
				{
					lower.z = upper.z;
					upper.z = value;
				}
				break;
			default:
				throw new IndexOutOfBoundsException( "index must be between 0-2 inclusive" );
		}
	}
	
	public void setUpper( int index , double value )
	{
		switch( index )
		{
			case 0:
				upper.x = value;
				if( upper.x < lower.x )
				{
					upper.x = lower.x;
					lower.x = value;
				}
				break;
			case 1:
				upper.y = value;
				if( upper.y < lower.y )
				{
					upper.y = lower.y;
					lower.y = value;
				}
				break;
			case 2:
				upper.z = value;
				if( upper.z < lower.z )
				{
					upper.z = lower.z;
					lower.z = value;
				}
				break;
			default:
				throw new IndexOutOfBoundsException( "index must be between 0-2 inclusive" );
		}
	}
	
	public void set( Point3d c1 , Point3d c2 )
	{
		lower.x = Math.min( c1.x , c2.x );
		lower.y = Math.min( c1.y , c2.y );
		lower.z = Math.min( c1.z , c2.z );
		upper.x = Math.max( c1.x , c2.x );
		upper.y = Math.max( c1.y , c2.y );
		upper.z = Math.max( c1.z , c2.z );
	}
	
	public void set( BBox r )
	{
		lower.set( r.lower );
		upper.set( r.upper );
	}
	
	public void setCenterRadius( Point3d center , double radius )
	{
		lower.set( center.x - radius , center.y - radius , center.z - radius );
		upper.set( center.x + radius , center.y + radius , center.z + radius );
	}
	
	public void enlargeFixed( double enlargement )
	{
		lower.x -= enlargement;
		lower.y -= enlargement;
		lower.z -= enlargement;
		upper.x += enlargement;
		upper.y += enlargement;
		upper.z += enlargement;
	}
	
	public void enlargePrecent( double percentage )
	{
		double enlargement = ( upper.x - lower.x ) * ( percentage - 1.0 ) * 0.5;
		lower.x -= enlargement;
		upper.x += enlargement;
		
		enlargement = ( upper.y - lower.y ) * ( percentage - 1.0 ) * 0.5;
		lower.y -= enlargement;
		upper.y += enlargement;
		
		enlargement = ( upper.z - lower.z ) * ( percentage - 1.0 ) * 0.5;
		lower.z -= enlargement;
		upper.z += enlargement;
	}
	
	public double min2( double a , double b )
	{
		return a < b ? a : Double.isNaN( b ) ? a : b;
	}
	
	public double max2( double a , double b )
	{
		return a > b ? a : Double.isNaN( b ) ? a : b;
	}
	
	public void union( BBox b2 )
	{
		union( this , b2 );
	}
	
	public void union( BBox b1 , BBox b2 )
	{
		lower.x = min2( b2.lower.x , b1.lower.x );
		lower.y = min2( b2.lower.y , b1.lower.y );
		lower.z = min2( b2.lower.z , b1.lower.z );
		upper.x = max2( b2.upper.x , b1.upper.x );
		upper.y = max2( b2.upper.y , b1.upper.y );
		upper.z = max2( b2.upper.z , b1.upper.z );
	}
	
	public void intersect( BBox b2 )
	{
		intersect( this , b2 );
	}
	
	public void intersect( BBox b1 , BBox b2 )
	{
		if( b1.intersects( b2 ) )
		{
			setVoid( );
		}
		else
		{
			lower.x = Math.max( b2.lower.x , b1.lower.x );
			lower.y = Math.max( b2.lower.y , b1.lower.y );
			lower.z = Math.max( b2.lower.z , b1.lower.z );
			upper.x = Math.min( b2.upper.x , b1.upper.x );
			upper.y = Math.min( b2.upper.y , b1.upper.y );
			upper.z = Math.min( b2.upper.z , b1.upper.z );
		}
	}
	
	public double getLow( int index )
	{
		switch( index )
		{
			case 0:
				return lower.x;
			case 1:
				return lower.y;
			case 2:
				return lower.z;
			default:
				throw new IndexOutOfBoundsException( "index must be between 0-2 inclusive" );
		}
	}
	
	public double getHigh( int index )
	{
		switch( index )
		{
			case 0:
				return upper.x;
			case 1:
				return upper.y;
			case 2:
				return upper.z;
			default:
				throw new IndexOutOfBoundsException( "index must be between 0-2 inclusive" );
		}
	}
	
	public Point3d getLower( Point3d buffer )
	{
		buffer.set( lower );
		return buffer;
	}
	
	public Point3d getUpper( Point3d buffer )
	{
		buffer.set( upper );
		return buffer;
	}
	
	public String toString( )
	{
		return "BoundingBox[" + lower + " - " + upper + "]";
	}
}
