package org.andork.torquescape.control;

import static org.andork.vecmath.FloatArrayVecmath.mpmul;
import static org.andork.vecmath.FloatArrayVecmath.*;
import static org.andork.vecmath.FloatArrayVecmath.negate3;
import static org.andork.vecmath.FloatArrayVecmath.normalize3;
import static org.andork.vecmath.FloatArrayVecmath.set;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import org.andork.torquescape.model.Edge;
import org.andork.vecmath.FloatTransformComputer;

public class VehicleMover
{
	float[ ]					v1			= new float[ 3 ];
	float[ ]					v2			= new float[ 3 ];
	float[ ]					v3			= new float[ 3 ];
	float[ ]					v4			= new float[ 3 ];
	float[ ]					v5			= new float[ 3 ];
	float[ ]					v6			= new float[ 3 ];
	
	float[ ]					p1			= new float[ 3 ];
	
	FloatTransformComputer		tc			= new FloatTransformComputer( );
	
	float[ ]					orient		= newIdentityMatrix( );
	
	private final UVIntersector	intersector	= new UVIntersector( );
	
	private int indexOf( float[ ] p , int pi , ByteBuffer vertBuffer , CharBuffer indexBuffer , int triangleIndex )
	{
		for( int i = 0 ; i < 3 ; i++ )
		{
			int index = indexBuffer.get( triangleIndex + i );
			float x = vertBuffer.getFloat( index );
			float y = vertBuffer.getFloat( index + 4 );
			float z = vertBuffer.getFloat( index + 8 );
			
			if( p[ pi ] == x && p[ pi + 1 ] == y && p[ pi + 2 ] == z )
			{
				return triangleIndex + i;
			}
		}
		return -1;
	}
	
	public void move( Vehicle vehicle , float timestep )
	{
		double angleChange = vehicle.angularVelocity * timestep;
		if( angleChange != 0 )
		{
			// turn the vehicle. note that this algorithm treats the vehicle as if it turns instantaneously
			// before moving forward, rather than turning continuously as it moves forward. This is works ok
			// if the timestep is small.
			float[ ] bf = vehicle.basisForward;
			
			// turning is accomplished by rotating the forward vector in the EFG coordinate system of the basis.
			
			mvmulAffine( vehicle.basis.getXYZToEFGDirect( ) , bf );
			double ePrime = Math.cos( angleChange ) * bf[ 0 ] - Math.sin( angleChange ) * bf[ 1 ];
			double fPrime = Math.sin( angleChange ) * bf[ 0 ] + Math.cos( angleChange ) * bf[ 1 ];
			set( bf , ( float ) ePrime , ( float ) fPrime , 0 );
			mvmulAffine( vehicle.basis.getEFGToXYZDirect( ) , bf );
			normalize3( bf );
		}
		
		// now move the vehicle forward. This process is as follows:
		// 1. Determine the total distance the vehicle must move in this timestep.
		// 2. Find the next triangle edge the vehicle will cross if it moves forward in a straight line.
		// 3. If the crossing point is beyond the remaining distance to move, move the vehicle forward the remaining distance
		// and finish.
		// 4. Otherwise, move the vehicle to the crossing point, subtract the distance removed from the remaining distance,
		// find the triangle on the other side of the edge, convert the vehicle's basis from the old triangle to the new triangle,
		// and go back to step 2.
		
		float remainingDist = Math.abs( vehicle.velocity * timestep );
		
		while( remainingDist > 0 )
		{
			set( p1 , vehicle.location );
			set( v1 , vehicle.basisForward );
			
			if( vehicle.velocity < 0 )
			{
				negate3( v1 );
			}
			
			// convert the vehicle's location and direction to the basis' UVN coordinate system. It's easiest to find where
			// the vehicle will cross an edge of the triangle in the UVN coordinate system.
			
			// p1 = UVN location
			// v1 = UVN direction
			
			mpmul( vehicle.basis.getXYZToUVNDirect( ) , p1 );
			mvmulAffine( vehicle.basis.getXYZToUVNDirect( ) , v1 );
			p1[ 2 ] = v1[ 2 ] = 0;
			
			// determine where the vehicle will cross an edge if it goes in a straight line and how far away the crossing point is.
			
			// intersector.t[ 0 ] is the distance to the crossing point (in multiples of v1 / vehicle direction)
			// so p1 + intersector.t[ 0 ] * v1 == crossing point in UVN,
			// vehicle location + intersector.t[ 0 ] * vehicle direction == crossing point in XYZ
			
			intersector.intersect( p1[ 0 ] , p1[ 1 ] , v1[ 0 ] , v1[ 1 ] );
			if( Double.isNaN( intersector.t[ 0 ] ) || Double.isInfinite( intersector.t[ 0 ] ) || intersector.t[ 0 ] <= 0 )
			{
				break;
			}
			
			// is the crossing point beyond the remaining distance?
			if( intersector.t[ 0 ] > remainingDist )
			{
				// move the vehicle forward the remaining distance.
				p1[ 0 ] += remainingDist * v1[ 0 ];
				p1[ 1 ] += remainingDist * v1[ 1 ];
				p1[ 2 ] += remainingDist * v1[ 2 ];
				
				// figure out the "up" direction at the new location. This is not the normal of the triangle plane;
				// it's the normal used for rendering, which may vary over the surface of the triangle, making it appear
				// curved. If the vehicle orientation and camera position were based upon the plane normal, they would
				// jerk around suddenly when the vehicle crosses edges that appear smooth. By basing them upon the
				// rendering normal, they make it appear as though the vehicle is moving along a smooth surface.
				vehicle.basis.interpolateNormals( ( float ) p1[ 0 ] , ( float ) p1[ 1 ] , vehicle.basisUp );
				normalize3( vehicle.basisUp );
				
				// the new location is still in UVN! convert it back to XYZ
				mpmul( vehicle.basis.getUVNToXYZDirect( ) , p1 , vehicle.location );
				remainingDist = 0;
			}
			else
			{
				// subtract the distance to the crossing point from the total left to move.
				remainingDist -= intersector.t[ 0 ];
				
				// find the edge that was crossed.
				Edge edge = vehicle.basis.createEdge( intersector.edgeIndices[ 0 ] );
				
				// find the triangle on the other side
				// and determine how to order its points such that the first two match the crossed edge endpoints, in order
				Integer nextTriangle = null;
				Integer nexti0 = null, nexti1 = null, nexti2 = null;
				for( Character other : vehicle.currentZone.edgeToTriMap.get( edge.canonical( ) ) )
				{
					if( other != vehicle.indexInZone )
					{
						nextTriangle = ( int ) other;
						
						edge.getPoint( 0 , v1 );
						nexti0 = indexOf( v1 , 0 , vehicle.currentZone.getVertBuffer( ) , vehicle.currentZone.getIndexBuffer( ) , other );
						edge.getPoint( 1 , v1 );
						nexti1 = indexOf( v1 , 0 , vehicle.currentZone.getVertBuffer( ) , vehicle.currentZone.getIndexBuffer( ) , other );
						
						nexti2 = nextTriangle;
						while( nexti2 == nexti0 || nexti2 == nexti1 )
						{
							nexti2++ ;
						}
					}
				}
				
				// of course, there may not be anything on the other side, and the vehicle has reached a dead end!
				if( nextTriangle != null )
				{
					set( p1 , vehicle.location );
					set( v1 , vehicle.basisForward );
					
					// find the crossing point in XYZ
					float advance = Math.signum( vehicle.velocity ) * intersector.t[ 0 ];
					p1[ 0 ] += advance * v1[ 0 ];
					p1[ 1 ] += advance * v1[ 1 ];
					p1[ 2 ] += advance * v1[ 2 ];
					
					// compute the normal of the old triangle plane
					mvmulAffine( vehicle.basis.getEFGToXYZDirect( ) , 0 , 0 , 1 , v2 );
					
					// change the vehicle's basis to the new triangle and point ordering
					vehicle.basis.set( vehicle.currentZone.getVertBuffer( ) , nexti0 , nexti1 , nexti2 );
					
					// compute the normal of the new triangle plane
					mvmulAffine( vehicle.basis.getEFGToXYZDirect( ) , 0 , 0 , 1 , v3 );
					
					// find the transformation that rotates the old normal directly to the new normal
					tc.orientInPlace( v2 , v3 , orient );
					// rotate the vehicle direction thusly
					mvmulAffine( orient , v1 );
					
					// convert the vehicle location and direction to the new triangle UVN coordinates,
					// with which we can find the rendering normal at the new location and remove any
					// offset perpendicular to the new triangle plane due to floating point error.
					mpmul( vehicle.basis.getXYZToUVNDirect( ) , p1 );
					mvmulAffine( vehicle.basis.getXYZToUVNDirect( ) , v1 );
					
					// compute the rendering normal at the new location.
					vehicle.basis.interpolateNormals( ( float ) p1[ 0 ] , ( float ) p1[ 1 ] , v2 );
					
					// remove accidental offset from the edge
					p1[ 1 ] = 0;
					p1[ 2 ] = 0;
					v1[ 2 ] = 0;
					
					// convert the corrected location/direction back to XYZ
					mpmul( vehicle.basis.getUVNToXYZDirect( ) , p1 );
					mvmulAffine( vehicle.basis.getUVNToXYZDirect( ) , v1 );
					
					// keep the vehicle direction and "up" vectors normalized
					normalize3( v1 );
					normalize3( v2 );
					
					set( vehicle.location , p1 );
					set( vehicle.basisForward , v1 );
					set( vehicle.basisUp , v2 );
				}
				else
				{
					// we've hit a dead end; for now, bring the vehicle to a sudden stop.
					vehicle.velocity = 0;
					remainingDist = 0;
				}
			}
		}
		
		// if the vehicle has crossed an edge, it may not be facing "up" in the new basis.
		// rotate it to the new "up" direction gradually, rather than instantaneously (it may not finish
		// rotating up in this timestep).
		
		set( v2 , vehicle.modelUp );
		set( v5 , vehicle.basisUp );
		set( v6 , vehicle.basisForward );
		
		if( epsilonEquals( v2 , v5 , 0.1f ) )
		{
			// close enough; rotate the vehicle to the exact up direction
			tc.orientInPlace( v2 , v5 , orient );
			set( vehicle.modelUp , v5 );
			set( vehicle.modelForward , v6 );
		}
		else
		{
			// this rotation is tricky. Eventually I need to use quaternion slerp to do it, but this is the more complicated
			// method I figured out before I had any idea about slerp, and it's probably not 100% correct either.
			// Basically:
			// 1. Construct the plane parallel to its current forward and up directions (let's call this a profile plane)
			// 2. Construct the profile plane it needs to rotate to
			// 3. Determine where the current forward and up directions would move to if the current profile plane was rotated
			// directly to the target profile plane around the planes' intersection;
			// 4. Determine how much the forward/up need to be rotated within this rotated profile plane (is this making sense???)
			// to reach the target
			// 5. rotate the forward/up a fraction of this amount within the current profile plane
			// 6. rotate the forward/up a fraction of the angle between the current/target profile planes
			// Whew!
			
			// v2 = current up
			// v3 = current forward
			// v5 = target up
			// v6 = target forward
			
			set( v3 , vehicle.modelForward );
			
			// v1 = normal of current profile plane
			
			cross( v2 , v3 , v1 );
			normalize3( v1 );
			
			// v4 = normal of target profile plane
			
			cross( v5 , v6 , v4 );
			normalize3( v4 );
			
			cross( v1 , v4 , v1 );
			// now v1 = axis to rotate current to target profile plane
			
			double rotationRate = Math.PI * 2;
			double planeRotationAmount = rotationRate * timestep;
			double inPlaneRotationAmount = planeRotationAmount;
			
			// how much do we need to rotate the profile plane?
			double targetPlaneRotation = Math.asin( length3( v1 ) );
			
			if( targetPlaneRotation > 0 )
			{
				// find up/forward if the current profile plane is rotated all the way to the target profile plane
				rotate( v2 , v1 , targetPlaneRotation );
				rotate( v3 , v1 , targetPlaneRotation );
			}
			
			// determine how much we need to rotate up/forward within the profile plane
			cross( v2 , v5 , v4 );
			double targetInPlaneRotation = Math.asin( length3( v4 ) );
			
			// restore vehicle's current up/forward to v2/v3
			set( v2 , vehicle.modelUp );
			set( v3 , vehicle.modelForward );
			
			// make ratio of the plane rotation/in-plane rotation rates match the ratio of the
			// total amount of plane rotation/in plane rotation needed
			if( targetPlaneRotation > 0 && targetPlaneRotation > targetInPlaneRotation )
			{
				inPlaneRotationAmount = planeRotationAmount * targetInPlaneRotation / targetPlaneRotation;
			}
			else if( targetInPlaneRotation > 0 && targetInPlaneRotation > targetPlaneRotation )
			{
				planeRotationAmount = inPlaneRotationAmount * targetPlaneRotation / targetInPlaneRotation;
			}
			
			if( targetPlaneRotation < planeRotationAmount && targetInPlaneRotation < inPlaneRotationAmount )
			{
				// the target is within range; fire!!
				// set the vehicle up/forward to the exact target up/forward; we're done
				tc.orientInPlace( v2 , v5 , orient );
				set( vehicle.modelUp , v5 );
				set( vehicle.modelForward , v6 );
			}
			else
			{
				// perform in-plane rotation
				if( inPlaneRotationAmount > 0 )
				{
					rotate( v2 , v4 , inPlaneRotationAmount );
					rotate( v3 , v4 , inPlaneRotationAmount );
				}
				// perform profile plane rotation
				if( planeRotationAmount > 0 )
				{
					rotate( v2 , v1 , planeRotationAmount );
					rotate( v3 , v1 , planeRotationAmount );
				}
				set( vehicle.modelUp , v2 );
				set( vehicle.modelForward , v3 );
			}
		}
	}
	
	private void rotate( float[ ] v , float[ ] axis , double rotationAmount )
	{
		setRotation( orient , axis , ( float ) rotationAmount );
		mvmulAffine( orient , v );
	}
}