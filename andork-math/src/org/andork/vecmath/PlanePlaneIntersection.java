/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/

package org.andork.vecmath;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Performs plane intersections. Right now, it is used to project line segments onto triangles and line segments onto other line segments.
 * 
 * @author andy.edwards
 */
public class PlanePlaneIntersection
{
	public static void main( String[ ] args )
	{
		PlanePlaneIntersection ppx = new PlanePlaneIntersection( );
		// ppx.setUpPlane( 0 , new Point3d( 0 , 0 , 0 ) , new Point3d( 0 , 1 , 1 ) , new Point3d( 1 , 0 , 1 ) );
		// ppx.setUpProjectedLine( new Point3d( 0.5 , 0.5 , 2 ) , new Point3d( 2 , 0 , 5 ) , new Vector3d( 0 , 0 , -1 ) );
		
		ppx.setUpPlane( 0 , new Point3d( -1 , -1 , -1 ) , new Point3d( -.6 , -.6 , 2 ) , new Point3d( 0 , -.75 , 0 ) );
		ppx.setUpProjectedLine( new Point3d( -10 , -10 , 100 ) , new Point3d( -.7 , -.7 , 100 ) , new Vector3d( 0 , 0 , 1 ) );
		
		ppx.doSegmentTriangleProjection( );
		
		System.out.println( ppx );
		
		ppx.setUpLineLineProjection( new Point3d( 0 , 0 , 0 ) , new Point3d( 1 , 0 , 1 ) , new Point3d( 0 , 0 , 1 ) , new Point3d( 1 , 0 , 1 ) , new Vector3d( 1 , 0 , -1 ) );
		ppx.doSegmentSegmentProjection( );
		System.out.println( ppx );
	}
	
	public final Point3d	po		= new Point3d( );
	public final Vector3d	pu		= new Vector3d( );
	public final Vector3d	pv		= new Vector3d( );
	public final Vector3d	pn		= new Vector3d( );
	
	public final Point3d	qo		= new Point3d( );
	public final Vector3d	qu		= new Vector3d( );
	public final Vector3d	qv		= new Vector3d( );
	public final Vector3d	qn		= new Vector3d( );
	
	/**
	 * Weights for vectors (pu, pv, qu, qv) at first intersection point. Will be set such that
	 * <code>x0 = po + params0[0] * pu + params0[1] * pv = qo + params0[2] * qu + params0[3] * qv</code> (if there is a valid intersection point).
	 */
	public double[ ]		params0	= new double[ 4 ];
	/**
	 * Weights for vectors (pu, pv, qu, qv) at second intersection point. Will be set such that
	 * <code>x1 = po + params1[0] * pu + params1[1] * pv = qo + params1[2] * qu + params1[3] * qv</code> (if there is a valid second intersection point).
	 */
	public double[ ]		params1	= new double[ 4 ];
	
	/**
	 * First intersection point. Only valid if {@link #resultType} is <code>POINT, LINE_SEGMENT, or LINE</code>.
	 */
	public final Point3d	x0		= new Point3d( );
	/**
	 * Second intersection point. Only valid if {@link #resultType} is <code>LINE_SEGMENT or LINE</code>.
	 */
	public final Point3d	x1		= new Point3d( );
	
	/**
	 * Type of intersection found.
	 */
	public ResultType		resultType;
	
	public static enum ResultType
	{
		ERROR , NONE ,
		/**
		 * Indicates the intersection is a single point.
		 */
		POINT ,
		/**
		 * Indicates the intersection is a finite line segment.
		 */
		LINE_SEGMENT ,
		/**
		 * Indicates the intersection is an infinite line.
		 */
		LINE ,
		/**
		 * Indicates the intersection is a triangle.
		 */
		TRIANGLE ,
		/**
		 * Indicates the intersection is an infinite plane.
		 */
		PLANE;
	}
	
	final LinePlaneIntersection	lpx	= new LinePlaneIntersection( );
	
	/**
	 * Sets one of the planes to be parallel to a triangle of points. The given points must not be colinear.
	 * 
	 * @param planeNumber
	 *            0 for plane a, 1 for plane b.
	 */
	public void setUpPlane( int planeNumber , Point3d p0 , Point3d p1 , Point3d p2 )
	{
		if( planeNumber == 0 )
		{
			this.po.set( p0 );
			this.pu.sub( p1 , p0 );
			this.pv.sub( p2 , p0 );
			pn.cross( pu , pv );
			
			if( pn.x == 0 && pn.y == 0 && pn.z == 0 )
			{
				throw new IllegalArgumentException( "p0, p1, and p2 must not be coplanar" );
			}
		}
		else if( planeNumber == 1 )
		{
			qo.set( p0 );
			qu.sub( p1 , p0 );
			qv.sub( p2 , p0 );
			qn.cross( qu , qv );
			
			if( qn.x == 0 && qn.y == 0 && qn.z == 0 )
			{
				throw new IllegalArgumentException( "p0, p1, and p2 must not be coplanar" );
			}
		}
	}
	
	/**
	 * Sets one of the planes to be parallel to a triangle of points. The given points must not be colinear.
	 * 
	 * @param planeNumber
	 *            0 for plane a, 1 for plane b.
	 * 
	 * @throws IllegalArgumentException
	 *             if pu and pv are linearly dependent.
	 */
	public void setUpPlane( int planeNumber , Point3d p0 , Vector3d pu , Vector3d pv )
	{
		if( planeNumber == 0 )
		{
			this.po.set( p0 );
			this.pu.set( pu );
			this.pv.set( pv );
			pn.cross( pu , pv );
			
			if( pn.x == 0 && pn.y == 0 && pn.z == 0 )
			{
				throw new IllegalArgumentException( "pu and pv must be linearly independent" );
			}
		}
		else if( planeNumber == 1 )
		{
			qo.set( p0 );
			qu.set( pu );
			qv.set( pv );
			qn.cross( qu , qv );
			
			if( qn.x == 0 && qn.y == 0 && qn.z == 0 )
			{
				throw new IllegalArgumentException( "pu and pv must be linearly independent" );
			}
		}
	}
	
	/**
	 * Sets {@link #po} to l0, {@link #pu} to the vector from l0 to l1, and {@link #pv} to projDir.
	 * 
	 * @param l0
	 *            First line point.
	 * @param l1
	 *            Second line point.
	 * @param projDir
	 *            Direction of projection.
	 * 
	 * @throws IllegalArgumentException
	 *             if l0 equals l1 or projDir is parallel to the line through l0 and l1.
	 */
	public void setUpProjectedLine( Point3d l0 , Point3d l1 , Vector3d projDir )
	{
		po.set( l0 );
		pu.sub( l1 , l0 );
		if( pu.x == 0 && pu.y == 0 && pu.z == 0 )
		{
			throw new IllegalArgumentException( "l0 must not equal l1" );
		}
		pv.set( projDir );
		pn.cross( pu , pv );
		if( pn.x == 0 && pn.y == 0 && pn.z == 0 )
		{
			throw new IllegalArgumentException( "line must not be parallel to projDir" );
		}
	}
	
	/**
	 * Sets up a line onto plane projection. In this context, po is the line origin, pu is the line direction, pv is the direction of projection, qo is the
	 * first triangle point, and qu, qv are the vectors from there to the second and third triangle points.
	 * 
	 * @throws IllegalArgumentException
	 *             if tri0, tri1, and tri2 are colinear, or if projDir is parallel to the line.
	 */
	public void setUpLinePlaneProjection( Point3d line0 , Point3d line1 , Point3d tri0 , Point3d tri1 , Point3d tri2 , Vector3d projDir )
	{
		setUpPlane( 1 , tri0 , tri1 , tri2 );
		setUpProjectedLine( line0 , line1 , projDir );
	}
	
	/**
	 * Sets up a line onto line projection. In this context, po is the origin of the first line, pu is the direction of the first line, pv is the direction of
	 * projection, qo is the origin of the second line, and qu is the direction of the second line.
	 * 
	 * @throws {@link IllegalArgumentException} l0 equals l1 or if projDir is parallel to the line.
	 */
	public void setUpLineLineProjection( Point3d l0 , Point3d l1 , Point3d m0 , Point3d m1 , Vector3d projDir )
	{
		po.set( l0 );
		pu.sub( l1 , l0 );
		if( pu.x == 0 && pu.y == 0 && pu.z == 0 )
		{
			throw new IllegalArgumentException( "l0 must not equal l1" );
		}
		
		pv.set( projDir );
		pn.cross( pu , pv );
		if( pn.x == 0 && pn.y == 0 && pn.z == 0 )
		{
			throw new IllegalArgumentException( "projDir must not be parallel to the line through l0 and l1" );
		}
		
		qo.set( m0 );
		qu.sub( m1 , m0 );
	}
	
	/**
	 * Sets up a triangle / plane intersection. In this context, {@link #po} is t0, {@link #pu} is the vector from t0 to t1, {@link #pv} is the vector from t0
	 * to t2, and {@link #qo}, {@link #qu}, and {@link #qv} are set as given.
	 * 
	 * @param t0
	 *            Triangle point 0.
	 * @param t1
	 *            Triangle point 1.
	 * @param t2
	 *            Triangle point 2.
	 * @param qo
	 *            Plane origin.
	 * @param qu
	 *            Plane u-vector (parallel to plane).
	 * @param qv
	 *            Plane v-vector (parallel to plane).
	 * 
	 * @throws IllegalArgumentException
	 *             if t0, t1, and t2 are colinear, or if qu and qv are linearly dependent.
	 */
	public void setUpTrianglePlaneIntersection( Point3d t0 , Point3d t1 , Point3d t2 , Point3d qo , Vector3d qu , Vector3d qv )
	{
		setUpPlane( 0 , t0 , t1 , t2 );
		setUpPlane( 1 , qo , qu , qv );
	}
	
	/**
	 * Finds the intersection of the triangle and plane in the instance variables (set up using
	 * {@link #setUpTrianglePlaneIntersection(Point3d, Point3d, Point3d, Point3d, Vector3d, Vector3d)}).
	 * 
	 * May set {@link #resultType}, {@link #x0}, {@link #x1}, {@link #params0}, and {@link #params1} with the result values.
	 */
	public void doTrianglePlaneIntersection( )
	{
		resultType = ResultType.NONE;
		
		lpx.setUpPlane( qo , qu , qv );
		
		lpx.setUpLine( po , pu );
		lpx.findIntersection( );
		if( lpx.isPointIntersection( ) && lpx.isOnLine( ) )
		{
			addSegmentTriangleProjResult( lpx.t , 0 , lpx.u , lpx.v , lpx.result );
			if( resultType == ResultType.LINE_SEGMENT )
			{
				return;
			}
		}
		
		lpx.setUpLine( po , pv );
		lpx.findIntersection( );
		if( lpx.isPointIntersection( ) && lpx.isOnLine( ) )
		{
			addSegmentTriangleProjResult( 0 , lpx.t , lpx.u , lpx.v , lpx.result );
			if( resultType == ResultType.LINE_SEGMENT )
			{
				return;
			}
		}
		
		lpx.lo.add( po , pu );
		lpx.lt.sub( pv , pu );
		lpx.findIntersection( );
		if( lpx.isPointIntersection( ) && lpx.isOnLine( ) )
		{
			addSegmentTriangleProjResult( 1 - lpx.t , lpx.t , lpx.u , lpx.v , lpx.result );
			if( resultType == ResultType.LINE_SEGMENT )
			{
				return;
			}
		}
	}
	
	/**
	 * Projects the line segment onto the triangle in the instance variables (set up with
	 * {@link #setUpLinePlaneProjection(Point3d, Point3d, Point3d, Point3d, Point3d, Vector3d)}).
	 * 
	 * May set {@link #resultType}, {@link #x0}, {@link #x1}, {@link #params0}, and {@link #params1} with the result values.
	 */
	public void doSegmentTriangleProjection( )
	{
		resultType = ResultType.NONE;
		
		int hits = 0;
		
		lpx.setUpPlane( qo , qu , qv );
		
		// project first line endpoint onto triangle
		lpx.setUpLine( po , pv );
		lpx.findIntersection( );
		if( lpx.isInTriangle( ) )
		{
			addSegmentTriangleProjResult( 0 , lpx.t , lpx.u , lpx.v , lpx.result );
			if( ++hits == 2 )
			{
				return;
			}
		}
		
		// project second line endpoint onto triangle
		lpx.lo.add( po , pu );
		lpx.findIntersection( );
		if( lpx.isInTriangle( ) )
		{
			addSegmentTriangleProjResult( 1 , lpx.t , lpx.u , lpx.v , lpx.result );
			if( ++hits == 2 )
			{
				return;
			}
		}
		
		// lpx.setUpPlane( q0.x , q0.y , q0.z , q1.x , q1.y , q1.z , q2.x , q2.y , q2.z );
		lpx.setUpPlane( po , pu , pv );
		
		// intersect first triangle segment with projection strip
		lpx.setUpLine( qo , qu );
		lpx.findIntersection( );
		if( lpx.isOnLine( ) && lpx.u >= 0 && lpx.u <= 1 )
		{
			addSegmentTriangleProjResult( lpx.u , lpx.v , lpx.t , 0 , lpx.result );
			if( ++hits == 2 )
			{
				return;
			}
		}
		
		// intersect second triangle segment with projection strip
		lpx.lo.add( qo , qu );
		lpx.lt.sub( qv , qu );
		lpx.findIntersection( );
		if( lpx.isOnLine( ) && lpx.u >= 0 && lpx.u <= 1 )
		{
			addSegmentTriangleProjResult( lpx.u , lpx.v , 1 - lpx.t , lpx.t , lpx.result );
			if( ++hits == 2 )
			{
				return;
			}
		}
		
		// intersect third triangle segment with projection strip
		lpx.setUpLine( qo , qv );
		lpx.findIntersection( );
		if( lpx.isOnLine( ) && lpx.u >= 0 && lpx.u <= 1 )
		{
			addSegmentTriangleProjResult( lpx.u , lpx.v , 0 , lpx.t , lpx.result );
			if( ++hits == 2 )
			{
				return;
			}
		}
	}
	
	private void addSegmentTriangleProjResult( double up , double vp , double uq , double vq , Point3d x )
	{
		if( resultType == ResultType.NONE )
		{
			resultType = ResultType.POINT;
			params0[ 0 ] = up;
			params0[ 1 ] = vp;
			params0[ 2 ] = uq;
			params0[ 3 ] = vq;
			x0.set( x );
		}
		else if( resultType == ResultType.POINT )
		{
			if( !x.equals( x0 ) )
			{
				if( uq < params0[ 2 ] )
				{
					resultType = ResultType.LINE_SEGMENT;
					System.arraycopy( params0 , 0 , params1 , 0 , 4 );
					x1.set( x0 );
					params0[ 0 ] = up;
					params0[ 1 ] = vp;
					params0[ 2 ] = uq;
					params0[ 3 ] = vq;
					x0.set( x );
				}
				else if( uq > params0[ 2 ] )
				{
					resultType = ResultType.LINE_SEGMENT;
					params1[ 0 ] = up;
					params1[ 1 ] = vp;
					params1[ 2 ] = uq;
					params1[ 3 ] = vq;
					x1.set( x );
				}
			}
		}
	}
	
	/**
	 * Projects the first line segment onto the second line segment in the instance variables (set up with
	 * {@link #setUpLineLineProjection(Point3d, Point3d, Point3d, Point3d, Vector3d)}).
	 * 
	 * May set {@link #resultType}, {@link #x0}, {@link #x1}, {@link #params0}, and {@link #params1} with the result values.
	 */
	public void doSegmentSegmentProjection( )
	{
		resultType = ResultType.NONE;
		
		lpx.setUpLine( qo , qu );
		lpx.setUpPlane( po , pu , pv );
		lpx.findIntersection( );
		if( lpx.isPointIntersection( ) && lpx.isOnLine( ) && lpx.u >= 0 && lpx.u <= 1 )
		{
			addSegmentSegmentProjResult( lpx.u , lpx.v , lpx.t , lpx.result );
		}
		else if( lpx.isInPlane( ) )
		{
			int hits = 0;
			
			qv.add( qo , pn );
			lpx.setUpPlane( qo , qu , qv );
			lpx.setUpLine( po , pv );
			lpx.findIntersection( );
			
			if( lpx.isPointIntersection( ) && lpx.u >= 0 && lpx.u <= 1 )
			{
				addSegmentSegmentProjResult( 0 , lpx.t , lpx.u , lpx.result );
				if( ++hits == 2 )
				{
					return;
				}
			}
			
			lpx.lo.add( po , pu );
			lpx.findIntersection( );
			
			if( lpx.isPointIntersection( ) && lpx.u >= 0 && lpx.u <= 1 )
			{
				addSegmentSegmentProjResult( 1 , lpx.t , lpx.u , lpx.result );
				if( ++hits == 2 )
				{
					return;
				}
			}
			
			lpx.setUpPlane( po , pu , pn );
			lpx.lo.set( qo );
			lpx.lt.negate( pv );
			
			lpx.findIntersection( );
			
			if( lpx.isPointIntersection( ) && lpx.u >= 0 && lpx.u <= 1 )
			{
				addSegmentSegmentProjResult( lpx.u , lpx.t , 0 , qo );
				if( ++hits == 2 )
				{
					return;
				}
			}
			
			lpx.lo.add( qo , qu );
			
			lpx.findIntersection( );
			
			if( lpx.isPointIntersection( ) && lpx.u >= 0 && lpx.u <= 1 )
			{
				addSegmentSegmentProjResult( lpx.u , lpx.t , 1 , lpx.lo );
				if( ++hits == 2 )
				{
					return;
				}
			}
		}
	}
	
	private void addSegmentSegmentProjResult( double up , double vp , double uq , Point3d x )
	{
		if( resultType == ResultType.NONE )
		{
			resultType = ResultType.POINT;
			params0[ 0 ] = up;
			params0[ 1 ] = vp;
			params0[ 2 ] = uq;
			params0[ 3 ] = 0;
			x0.set( x );
		}
		else if( resultType == ResultType.POINT )
		{
			if( !x.equals( x0 ) )
			{
				if( up < params0[ 0 ] )
				{
					resultType = ResultType.LINE_SEGMENT;
					System.arraycopy( params0 , 0 , params1 , 0 , 4 );
					x1.set( x0 );
					params0[ 0 ] = up;
					params0[ 1 ] = vp;
					params0[ 2 ] = uq;
					params0[ 3 ] = 0;
					x0.set( x );
				}
				else if( up > params0[ 0 ] )
				{
					resultType = ResultType.LINE_SEGMENT;
					params1[ 0 ] = up;
					params1[ 1 ] = vp;
					params1[ 2 ] = uq;
					params1[ 3 ] = 0;
					x1.set( x );
				}
			}
		}
	}
	
	/**
	 * @return <code>true</code> if <code>resultType == ResultType.POINT</code>.
	 */
	public boolean isPointIntersection( )
	{
		return resultType == ResultType.POINT;
	}
	
	/**
	 * @return <code>true</code> if <code>resultType == ResultType.LINE_SEGMENT</code>.
	 */
	public boolean isLineSegmentIntersection( )
	{
		return resultType == ResultType.LINE_SEGMENT;
	}
	
	@Override
	public String toString( )
	{
		StringBuffer buffer = new StringBuffer( );
		buffer.append( "PlanePlaneIntersection[\n" );
		buffer.append( "\tPlane p: " + po + ", " + pu + ", " + pv + "\n" );
		buffer.append( "\tPlane q: " + qo + ", " + qu + ", " + qv + "\n" );
		buffer.append( "\tresultType: " + resultType + '\n' );
		buffer.append( "\tx0: " + x0 + ", up: " + params0[ 0 ] + ", vp: " + params0[ 1 ] + ", uq: " + params0[ 2 ] + ", vq: " + params0[ 3 ] + "\n" );
		buffer.append( "\tx1: " + x1 + ", up: " + params1[ 0 ] + ", vp: " + params1[ 1 ] + ", uq: " + params1[ 2 ] + ", vq: " + params1[ 3 ] + "\n" );
		return buffer.toString( );
	}
}
