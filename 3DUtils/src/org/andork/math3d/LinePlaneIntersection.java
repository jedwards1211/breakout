
package org.andork.math3d;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * This is the utility class for finding the intersection of a line with a plane.
 * 
 * LinePlaneIntersection has instance variables for inputs ({@link #po}, {@link #pn}, {@link #pu}, {@link #pv}, {@link #lo}, and {@link #lt}); outputs (
 * {@link #result}, {@link #t}, {@link #u}, and {@link #v}) , and temporaries.
 * 
 * @author andy.edwards
 */
public class LinePlaneIntersection
{
	/**
	 * The plane origin.
	 */
	public final Point3d	po				= new Point3d( );
	/**
	 * The plane normal.
	 */
	public final Vector3d	pn				= new Vector3d( );
	/**
	 * The plane u-vector (parallel to plane). Should be linearly independent from {@link #pv}.
	 */
	public final Vector3d	pu				= new Vector3d( );
	/**
	 * The plane v-vector (parallel to plane). Should be linearly independent from {@link #pu}.
	 */
	public final Vector3d	pv				= new Vector3d( );
	
	/**
	 * The line origin.
	 */
	public final Point3d	lo				= new Point3d( );
	/**
	 * The line direction.
	 */
	public final Vector3d	lt				= new Vector3d( );
	
	/**
	 * The resulting intersection point (set by {@link #findIntersection()}). Only valid if {@link #resultType} is {@link ResultType#POINT}.
	 */
	public final Point3d	result			= new Point3d( );
	
	/**
	 * The geometric situation found (set by {@link #findIntersection()}).
	 */
	public ResultType		resultType;
	
	final Vector3d			w0				= new Vector3d( );
	final Vector3d			w				= new Vector3d( );
	
	/**
	 * The line parameter. If a point intersection is found, t will be set such that <code>lo + lt * t = result</code> (approximately).
	 */
	public double			t;
	/**
	 * The plane u parameter. If a point intersection is found, u will be set such that <code> po + pu * u + pv * v = result</code> (approximately).
	 */
	public double			u;
	/**
	 * The plane v parameter. If a point intersection is found, v will be set such that <code> po + pu * u + pv * v = result</code> (approximately).
	 */
	public double			v;
	
	public double			ZERO_TOLERANCE	= 1e-6;
	
	/**
	 * Enumeration of possible geometric situations that can occur.
	 */
	public static enum ResultType
	{
		INVALID ,
		/**
		 * Indicates the line intersects the plane at a single point.
		 */
		POINT ,
		/**
		 * Indicates the line lies in the plane.
		 */
		IN_PLANE ,
		/**
		 * Indicates the line is parallel to the plane but lies outside of it.
		 */
		PARALLEL_DISJOINT
	}
	
	/**
	 * Sets the plane instance variables {@link #po}, {@link #pn}, {@link #pu}, and {@link #pv}.
	 * 
	 * @param origin
	 *            The plane origin.
	 * @param uvec
	 *            The plane u-vector (parallel to the plane).
	 * @param vvec
	 *            The plane v-vector (parallel to the plane).
	 * 
	 * @throws IllegalArgumentException
	 *             if uvec and vvec are linearly dependent.
	 */
	public void setUpPlane( Point3d origin , Vector3d uvec , Vector3d vvec )
	{
		po.set( origin );
		pu.set( uvec );
		pv.set( vvec );
		pn.cross( pu , pv );
		if( pn.x == 0 && pn.y == 0 && pn.z == 0 )
		{
			throw new IllegalArgumentException( "uvec and vvec must be linearly independent" );
		}
	}
	
	/**
	 * Sets the plane instance variables {@link #po}, {@link #pn}, {@link #pu}, and {@link #pv}.
	 * 
	 * @param p1
	 *            First point in the plane.
	 * @param p2
	 *            Second point in the plane.
	 * @param p3
	 *            Third point in the plane.
	 * 
	 * @throws IllegalArgumentException
	 *             if p1, p2, and p3 are colinear.
	 */
	public void setUpPlane( Point3d p1 , Point3d p2 , Point3d p3 )
	{
		po.set( p1 );
		pu.sub( p2 , po );
		pv.sub( p3 , po );
		pn.cross( pu , pv );
		if( pn.x == 0 && pn.y == 0 && pn.z == 0 )
		{
			throw new IllegalArgumentException( "p1, p2, and p3 must not be colinear" );
		}
	}
	
	/**
	 * Sets the line instance variables {@link #lo} and {@link #lt}.
	 * 
	 * @param origin
	 *            The line origin.
	 * @param direction
	 *            The line direction.
	 * 
	 * @throws IllegalArgumentException
	 *             if the direction is zero.
	 */
	public void setUpLine( Point3d origin , Vector3d direction )
	{
		if( direction.x == 0 && direction.y == 0 && direction.z == 0 )
		{
			throw new IllegalArgumentException( "direction must be nonzero" );
		}
		lo.set( origin );
		lt.set( direction );
	}
	
	/**
	 * Sets the line instance variables {@link #lo} and {@link #lt}.
	 * 
	 * @param p1
	 *            First point in the line.
	 * @param p2
	 *            Second point in the line.
	 * 
	 * @throws IllegalArgumentException
	 *             If p1 equals p2.
	 */
	public void setUpLine( Point3d p1 , Point3d p2 )
	{
		lo.set( p1 );
		lt.sub( p2 , p1 );
		if( lt.x == 0 && lt.y == 0 && lt.z == 0 )
		{
			throw new IllegalArgumentException( "p1 must not equal p2" );
		}
	}
	
	/**
	 * Determines what type of geometric situation is present (see {@link ResultType}) and sets {@link #resultType}. If the line and plane intersect in a single
	 * point ({@link ResultType#POINT}), computes the intersection point ({@link #result}), line parameter ({@link #t}) and plane parameters ({@link #u},
	 * {@link #v}).
	 */
	public void findIntersection( )
	{
		resultType = ResultType.INVALID;
		
		w0.sub( po , lo );
		
		double a = checkedDotProduct( w0 , pn );
		double b = checkedDotProduct( lt , pn );
		
		if( b == 0 )
		{
			resultType = a == 0 ? ResultType.IN_PLANE : ResultType.PARALLEL_DISJOINT;
			return;
		}
		
		resultType = ResultType.POINT;
		
		t = a / b;
		result.scaleAdd( t , lt , lo );
		w.sub( result , po );
		
		double uu , uv , vv , wu , wv , D;
		uu = pu.dot( pu );
		uv = pu.dot( pv );
		vv = pv.dot( pv );
		wu = w.dot( pu );
		wv = w.dot( pv );
		D = uv * uv - uu * vv;
		
		u = ( uv * wv - vv * wu ) / D;
		v = ( uv * wu - uu * wv ) / D;
	}
	
	/**
	 * @return <code>true</code> if {@link #resultType} == {@link ResultType#POINT}.
	 */
	public boolean isPointIntersection( )
	{
		return resultType == ResultType.POINT;
	}
	
	/**
	 * @return <code>true</code> if {@link #resultType} == {@link ResultType#IN_PLANE}.
	 */
	public boolean isInPlane( )
	{
		return resultType == ResultType.IN_PLANE;
	}
	
	/**
	 * @return <code>true</code> if the intersection point lies within the triangle at {@link #po} spanned by {@link #pu} and {@link #pv} (if you used
	 *         {@link #setUpPlane(Point3d, Point3d, Point3d)}, that is the triangle between the three points you specified). That is, returns
	 *         <code>u >= 0 && u <= 1 && v >= 0 && v <= 1 && ( u + v ) <= 1</code>.
	 */
	public boolean isInTriangle( )
	{
		return u >= 0 && u <= 1 && v >= 0 && v <= 1 && ( u + v ) <= 1;
	}
	
	/**
	 * @return <code>true</code> if the intersection point lies on the line segment at {@link #lo} spanned by {@link #lt} (if you used
	 *         {@link #setUpLine(Point3d, Point3d)}, that is the line between the two points you specified). That is, returns <code> t >= 0 && t <= 1</code>.
	 */
	public boolean isOnLine( )
	{
		return t >= 0 && t <= 1;
	}
	
	/**
	 * @return <code>true</code> if the intersection point lies on the ray starting at {@link #lo} going in the {@link #lt} direction. That is, returns
	 *         <code> t >= 0</code>.
	 */
	public boolean isOnRay( )
	{
		return t >= 0;
	}
	
	/**
	 * Computes the distance between {@link #result} and <code>po + u * pu + v * pv</code>. Ideally it should be zero, but there may be minor floating point
	 * errors.
	 */
	public double TUVinconsistency( )
	{
		double px = po.x + u * pu.x + v * pv.x;
		double py = po.y + u * pu.y + v * pv.y;
		double pz = po.z + u * pu.z + v * pv.z;
		
		double dx = result.x - px;
		double dy = result.y - py;
		double dz = result.z - pz;
		
		return Math.sqrt( dx * dx + dy * dy + dz * dz );
	}
	
	public double errorTest( Point3d temp , int sigfigs )
	{
		int precision = 1;
		while( sigfigs-- > 0 )
		{
			precision *= 10;
		}
		
		while( true )
		{
			temp.x = random( precision );
			temp.y = random( precision );
			temp.z = random( precision );
			
			double at = random( precision );
			double au = random( precision );
			double av = random( precision );
			
			lt.x = random( precision );
			lt.y = random( precision );
			lt.z = random( precision );
			
			pu.x = random( precision );
			pu.y = random( precision );
			pu.z = random( precision );
			
			pv.x = random( precision );
			pv.y = random( precision );
			pv.z = random( precision );
			
			pn.cross( pu , pv );
			if( pn.x == 0 && pn.y == 0 && pn.z == 0 )
			{
				continue;
			}
			
			lo.scaleAdd( -at , lt , temp );
			
			po.scaleAdd( -au , pu , temp );
			po.scaleAdd( -av , pv , po );
			
			findIntersection( );
			if( !isPointIntersection( ) )
			{
				continue;
			}
			
			return temp.distance( result );
		}
	}
	
	double random( int precision )
	{
		return Math.floor( Math.random( ) * precision ) / precision;
	}
	
	double checkedDotProduct( Vector3d a , Vector3d b )
	{
		double threshhold = ZERO_TOLERANCE * Math.min( a.lengthSquared( ) , b.lengthSquared( ) );
		double dp = a.dot( b );
		return Math.abs( dp ) < threshhold ? 0 : dp;
	}
	
	@Override
	public String toString( )
	{
		StringBuffer buffer = new StringBuffer( );
		buffer.append( getClass( ).getSimpleName( ) ).append( "\n" );
		buffer.append( "\tLine: " ).append( lo ).append( " + t * " ).append( lt ).append( '\n' );
		buffer.append( "\tPlane: " ).append( po ).append( " + u * " ).append( pu ).append( " + v * " ).append( pv ).append( '\n' );
		buffer.append( "\tresultType: " ).append( resultType ).append( '\n' );
		buffer.append( "\tresult: " ).append( result ).append( ", t: " ).append( t ).append( ", u: " ).append( u ).append( ", v: " ).append( v ).append( "\n]" );
		return buffer.toString( );
	}
}
