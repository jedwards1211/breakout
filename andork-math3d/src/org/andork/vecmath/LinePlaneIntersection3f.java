package org.andork.vecmath;

import static org.andork.vecmath.Vecmath.cross;
import static org.andork.vecmath.Vecmath.distance3;
import static org.andork.vecmath.Vecmath.dot3;
import static org.andork.vecmath.Vecmath.scaleAdd3;
import static org.andork.vecmath.Vecmath.setf;
import static org.andork.vecmath.Vecmath.sub3;

/**
 * This is the utility class for finding the intersection of a line with a plane.
 * 
 * LinePlaneIntersection has instance variables for inputs ({@link #po}, {@link #pn}, {@link #pu}, {@link #pv}, {@link #lo}, and {@link #lt}); outputs (
 * {@link #result}, {@link #t}, {@link #u}, and {@link #v}) , and temporaries.
 * 
 * @author andy.edwards
 */
public class LinePlaneIntersection3f
{
	/**
	 * The plane origin.
	 */
	public final float[ ]	po				= new float[ 3 ];
	/**
	 * The plane normal.
	 */
	public final float[ ]	pn				= new float[ 3 ];
	/**
	 * The plane u-vector (parallel to plane). Should be linearly independent from {@link #pv}.
	 */
	public final float[ ]	pu				= new float[ 3 ];
	/**
	 * The plane v-vector (parallel to plane). Should be linearly independent from {@link #pu}.
	 */
	public final float[ ]	pv				= new float[ 3 ];
	
	/**
	 * The line origin.
	 */
	public final float[ ]	lo				= new float[ 3 ];
	/**
	 * The line direction.
	 */
	public final float[ ]	lt				= new float[ 3 ];
	
	/**
	 * The resulting intersection point (set by {@link #findIntersection()}). Only valid if {@link #resultType} is {@link ResultType#POINT}.
	 */
	public final float[ ]	result			= new float[ 3 ];
	
	/**
	 * The geometric situation found (set by {@link #findIntersection()}).
	 */
	public ResultType		resultType;
	
	final float[ ]			w0				= new float[ 3 ];
	final float[ ]			w				= new float[ 3 ];
	
	/**
	 * The line parameter. If a point intersection is found, t will be set such that <code>lo + lt * t = result</code> (approximately).
	 */
	public float			t;
	/**
	 * The plane u parameter. If a point intersection is found, u will be set such that <code> po + pu * u + pv * v = result</code> (approximately).
	 */
	public float			u;
	/**
	 * The plane v parameter. If a point intersection is found, v will be set such that <code> po + pu * u + pv * v = result</code> (approximately).
	 */
	public float			v;
	
	public float			ZERO_TOLERANCE	= 1e-6f;
	
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
	public void planeFromUV( float[ ] origin , float[ ] uvec , float[ ] vvec )
	{
		setf( po , origin );
		setf( pu , uvec );
		setf( pv , vvec );
		cross( pu , pv , pn );
		if( pn[ 0 ] == 0 && pn[ 1 ] == 0 && pn[ 2 ] == 0 )
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
	public void planeFromPoints( float[ ] p1 , float[ ] p2 , float[ ] p3 )
	{
		setf( po , p1 );
		sub3( p2 , po , pu );
		sub3( p3 , po , pv );
		cross( pu , pv , pn );
		if( pn[ 0 ] == 0 && pn[ 1 ] == 0 && pn[ 2 ] == 0 )
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
	public void lineFromRay( float[ ] origin , float[ ] direction )
	{
		if( direction[ 0 ] == 0 && direction[ 1 ] == 0 && direction[ 2 ] == 0 )
		{
			throw new IllegalArgumentException( "direction must be nonzero" );
		}
		setf( lo , origin );
		setf( lt , direction );
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
	public void lineFromPoints( float[ ] p1 , float[ ] p2 )
	{
		setf( lo , p1 );
		sub3( p2 , p1 , lt );
		if( lt[ 0 ] == 0 && lt[ 1 ] == 0 && lt[ 2 ] == 0 )
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
		
		sub3( po , lo , w0 );
		
		float a = checkedDotProduct( w0 , pn );
		float b = checkedDotProduct( lt , pn );
		
		if( b == 0 )
		{
			resultType = a == 0 ? ResultType.IN_PLANE : ResultType.PARALLEL_DISJOINT;
			return;
		}
		
		resultType = ResultType.POINT;
		
		t = a / b;
		scaleAdd3( t , lt , lo , result );
		
		sub3( result , po , w );
		
		float uu, uv, vv, wu, wv, D;
		uu = dot3( pu , pu );
		uv = dot3( pu , pv );
		vv = dot3( pv , pv );
		wu = dot3( w , pu );
		wv = dot3( w , pv );
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
	 *         {@link #planeFromPoints(float[], float[], float[])}, that is the triangle between the three points you specified). That is, returns
	 *         <code>u >= 0 && u <= 1 && v >= 0 && v <= 1 && ( u + v ) <= 1</code>.
	 */
	public boolean isInTriangle( )
	{
		return u >= 0 && u <= 1 && v >= 0 && v <= 1 && ( u + v ) <= 1;
	}
	
	/**
	 * @return <code>true</code> if the intersection point lies on the line segment at {@link #lo} spanned by {@link #lt} (if you used
	 *         {@link #setUpLine(float[], float[])}, that is the line between the two points you specified). That is, returns <code> t >= 0 && t <= 1</code>.
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
	public float TUVinconsistency( )
	{
		float px = po[ 0 ] + u * pu[ 0 ] + v * pv[ 0 ];
		float py = po[ 1 ] + u * pu[ 1 ] + v * pv[ 1 ];
		float pz = po[ 2 ] + u * pu[ 2 ] + v * pv[ 2 ];
		
		float dx = result[ 0 ] - px;
		float dy = result[ 1 ] - py;
		float dz = result[ 2 ] - pz;
		
		return ( float ) Math.sqrt( dx * dx + dy * dy + dz * dz );
	}
	
	public float errorTest( float[ ] temp , int sigfigs )
	{
		int precision = 1;
		while( sigfigs-- > 0 )
		{
			precision *= 10;
		}
		
		while( true )
		{
			temp[ 0 ] = random( precision );
			temp[ 1 ] = random( precision );
			temp[ 2 ] = random( precision );
			
			float at = random( precision );
			float au = random( precision );
			float av = random( precision );
			
			lt[ 0 ] = random( precision );
			lt[ 1 ] = random( precision );
			lt[ 2 ] = random( precision );
			
			pu[ 0 ] = random( precision );
			pu[ 1 ] = random( precision );
			pu[ 2 ] = random( precision );
			
			pv[ 0 ] = random( precision );
			pv[ 1 ] = random( precision );
			pv[ 2 ] = random( precision );
			
			cross( pu , pv , pn );
			if( pn[ 0 ] == 0 && pn[ 1 ] == 0 && pn[ 2 ] == 0 )
			{
				continue;
			}
			
			scaleAdd3( -at , lt , temp , lo );
			
			scaleAdd3( -au , pu , temp , po );
			scaleAdd3( -av , pv , po , po );
			
			findIntersection( );
			if( !isPointIntersection( ) )
			{
				continue;
			}
			
			return distance3( temp , result );
		}
	}
	
	float random( int precision )
	{
		return ( float ) Math.floor( Math.random( ) * precision ) / precision;
	}
	
	float checkedDotProduct( float[ ] a , float[ ] b )
	{
		float threshhold = ZERO_TOLERANCE * Math.min( dot3( a , a ) , dot3( b , b ) );
		float dp = dot3( a , b );
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
