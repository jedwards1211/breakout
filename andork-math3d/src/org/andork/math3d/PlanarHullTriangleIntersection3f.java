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
package org.andork.math3d;

import static org.andork.math3d.Vecmath.*;

import java.util.Arrays;

public class PlanarHullTriangleIntersection3f
{
	private final float[ ][ ]	triangleVertexDists;
	private final float[ ]		hullVertexDists;
	private final float[ ]		triangleNormal	= new float[ 3 ];
	private final float[ ]		xLineOrigin		= new float[ 3 ];
	private final float[ ]		xLineDirection	= new float[ 3 ];
	private final float[ ]		temp			= new float[ 3 ];
	private final float[ ][ ]	p				= new float[ 3 ][ ];
	private final boolean[ ]	pInside			= new boolean[ 3 ];
	private final float[ ]		st				= new float[ 3 ];
	private final float[ ]		sh				= new float[ 3 ];
	private final float[ ]		pt				= new float[ 3 ];
	private final float[ ]		ph				= new float[ 3 ];
	
	public PlanarHullTriangleIntersection3f( int numSides , int numVertices )
	{
		triangleVertexDists = new float[ numSides ][ 3 ];
		hullVertexDists = new float[ numVertices ];
	}
	
	public boolean findNearestIntersectionPoint(
			float[ ] p0 ,
			float[ ] p1 ,
			float[ ] p2 ,
			PlanarHull3f hull ,
			float[ ] rayOrigin ,
			float[ ] rayDirection ,
			float[ ] normal ,
			float[ ] outNearestPoint ,
			float[ ] outDistanceAndLateralSq )
	{
		p[ 0 ] = p0;
		p[ 1 ] = p1;
		p[ 2 ] = p2;
		
		Arrays.fill( outDistanceAndLateralSq , Float.NaN );
		Arrays.fill( pInside , true );
		
		// find the signed distances from the triangle vertices to the hull planes,
		// and determine if any of the triangle vertices are inside the hull along the way
		for( int side = 0 ; side < triangleVertexDists.length ; side++ )
		{
			float[ ] dists = triangleVertexDists[ side ];
			for( int d = 0 ; d < 3 ; d++ )
			{
				dists[ d ] = dot3( p[ d ] , hull.normals[ side ] ) + hull.planeDists[ side ];
				pInside[ d ] &= dists[ d ] > 0;
			}
			
			// if all triangle vertices lie on the outside of a hull plane, no intersection
			if( dists[ 0 ] <= 0 && dists[ 1 ] <= 0 && dists[ 2 ] <= 0 )
			{
				return false;
			}
		}
		
		// if any of the triangle vertices are inside the hull, return the one that is nearest to the center ray
		for( int d = 0 ; d < 3 ; d++ )
		{
			if( pInside[ d ] )
			{
				pickNearerPoint( p[ d ] , rayOrigin , rayDirection , normal , outNearestPoint , outDistanceAndLateralSq );
			}
		}
		if( !Float.isNaN( outDistanceAndLateralSq[ 0 ] ) )
		{
			return true;
		};
		
		// calculate the triangle plane equation
		threePointNormal( p0 , p1 , p2 , triangleNormal );
		float trianglePlaneDist = -dot3( triangleNormal , p0 );
		
		boolean allPositive = false;
		boolean allNegative = false;
		
		// find the signed distances from the hull vertices to the triangle plane
		for( int vertex = 0 ; vertex < hullVertexDists.length ; vertex++ )
		{
			hullVertexDists[ vertex ] = dot3( hull.vertices[ vertex ] , triangleNormal ) + trianglePlaneDist;
			allPositive &= hullVertexDists[ vertex ] >= 0;
			allNegative &= hullVertexDists[ vertex ] <= 0;
		}
		
		// if all hull vertices are on one side of the triangle plane, no intersection
		if( allPositive || allNegative )
		{
			return false;
		}
		
		// there must be an intersection somewhere, now we have to find intersection points between
		// the triangle and the hull triangles
		
		for( int hullTriangle = 0 ; hullTriangle < hull.triangleIndices.length ; hullTriangle++ )
		{
			int[ ] hullIndices = hull.triangleIndices[ hullTriangle ];
			
			int side = hull.triangleSides[ hullTriangle ];
			float[ ] dists = triangleVertexDists[ side ];
			
			for( int d = 0 ; d < 3 ; d++ )
			{
				st[ d ] = Math.signum( dists[ d ] );
				sh[ d ] = Math.signum( hullVertexDists[ hullIndices[ d ] ] );
			}
			
			// if all the triangle vertices are on one side of the hull plane, no intersection here
			if( st[ 0 ] == st[ 1 ] && st[ 0 ] == st[ 2 ] )
			{
				continue;
			}
			
			// if all the hull triangle vertices are on one side of the triangle plane, no intersection here
			if( sh[ 0 ] == sh[ 1 ] && sh[ 0 ] == sh[ 2 ] )
			{
				continue;
			}
			cross( hull.normals[ side ] , triangleNormal , xLineDirection );
			
			// compute the origin of the intersection line
			// (find an dimension in which the direction is nonzero, set the origin to zero in that dimension, and solve)
			for( int d = 0 ; d < 3 ; d++ )
			{
				if( xLineDirection[ d ] != 0 )
				{
					calcXLineOrigin( hull , trianglePlaneDist , side , d );
					break;
				}
			}
			
			// project the triangle and hull triangle points onto the intersection line
			for( int d = 0 ; d < 3 ; d++ )
			{
				pt[ d ] = subDot3( p[ d ] , xLineDirection , xLineDirection );
				ph[ d ] = subDot3( hull.vertices[ hullIndices[ d ] ] , xLineDirection , xLineDirection );
			}
			
			// find the intervals where the triangle and hull triangle intersect the intersection line
			float tmin = Float.MAX_VALUE;
			float tmax = -Float.MAX_VALUE;
			float hmin = Float.MAX_VALUE;
			float hmax = -Float.MAX_VALUE;
			for( int d0 = 0 ; d0 < 3 ; d0++ )
			{
				int d1 = ( d0 + 1 ) % 3;
				// does this edge of the triangle cross the hull plane?
				if( st[ d0 ] != st[ d1 ] )
				{
					float t = pt[ d0 ] + ( pt[ d1 ] - pt[ d0 ] ) * dists[ d0 ] / ( dists[ d0 ] - dists[ d1 ] );
					tmin = Math.min( tmin , t );
					tmax = Math.max( tmax , t );
				}
				// does this edge of the hull triangle cross the triangle plane?
				if( sh[ d0 ] != sh[ d1 ] )
				{
					float h = ph[ d0 ] + ( ph[ d1 ] - ph[ d0 ] ) * hullVertexDists[ hullIndices[ d0 ] ] /
							( hullVertexDists[ hullIndices[ d0 ] ] - hullVertexDists[ hullIndices[ d1 ] ] );
					hmin = Math.min( hmin , h );
					hmax = Math.max( hmax , h );
				}
			}
			
			// do the triangles intersect?
			if( hmin > tmax || tmin > hmax )
			{
				continue;
			}
			
			// compute the intersection points and determine if they're nearer than anything we've seen so far
			scaleAdd3( Math.max( tmin , hmin ) , xLineDirection , xLineOrigin , temp );
			pickNearerPoint( temp , rayOrigin , rayDirection , normal , outNearestPoint , outDistanceAndLateralSq );
			scaleAdd3( Math.min( tmax , hmax ) , xLineDirection , xLineOrigin , temp );
			pickNearerPoint( temp , rayOrigin , rayDirection , normal , outNearestPoint , outDistanceAndLateralSq );
		}
		
		return !Float.isNaN( outDistanceAndLateralSq[ 0 ] );
	}
	
	private void pickNearerPoint( float[ ] newPoint , float[ ] rayOrigin , float[ ] rayDirection , float[ ] normal ,
			float[ ] inOutNearestPoint , float[ ] inOutDistanceSqAndLateralSq )
	{
		float newDistance = subDot3( newPoint , rayOrigin , normal );
		float newDistanceSq = newDistance * newDistance;
		float ratio = newDistance / dot3( rayDirection , normal );
		float dx = rayOrigin[ 0 ] + rayDirection[ 0 ] * ratio - newPoint[ 0 ];
		float dy = rayOrigin[ 1 ] + rayDirection[ 1 ] * ratio - newPoint[ 1 ];
		float dz = rayOrigin[ 2 ] + rayDirection[ 2 ] * ratio - newPoint[ 2 ];
		float newLateralSq = dx * dx + dy * dy + dz * dz;
		if( Float.isNaN( inOutDistanceSqAndLateralSq[ 0 ] ) || newLateralSq * inOutDistanceSqAndLateralSq[ 0 ] <
				inOutDistanceSqAndLateralSq[ 1 ] * newDistanceSq )
		{
			setf( inOutNearestPoint , newPoint );
			inOutDistanceSqAndLateralSq[ 0 ] = newDistanceSq;
			inOutDistanceSqAndLateralSq[ 1 ] = newLateralSq;
		}
	}
	
	private void calcXLineOrigin( PlanarHull3f hull , float trianglePlaneDist , int side , int d0 )
	{
		int d1 = ( d0 + 1 ) % 3;
		int d2 = ( d0 + 2 ) % 3;
		float a = hull.normals[ side ][ d1 ];
		float b = hull.normals[ side ][ d2 ];
		float c = triangleNormal[ d1 ];
		float d = triangleNormal[ d2 ];
		float rdet = 1 / ( a * d - b * c );
		xLineOrigin[ d0 ] = 0;
		xLineOrigin[ d1 ] = rdet * ( d * hull.planeDists[ side ] - b * trianglePlaneDist );
		xLineOrigin[ d2 ] = rdet * ( -c * hull.planeDists[ side ] + a * trianglePlaneDist );
	}
}
