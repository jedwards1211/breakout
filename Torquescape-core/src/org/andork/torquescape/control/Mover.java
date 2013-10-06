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

public class Mover
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
			float[ ] bf = vehicle.basisForward;
			
			mvmulAffine( vehicle.basis.getXYZToEFGDirect( ) , bf );
			double ePrime = Math.cos( angleChange ) * bf[ 0 ] - Math.sin( angleChange ) * bf[ 1 ];
			double fPrime = Math.sin( angleChange ) * bf[ 0 ] + Math.cos( angleChange ) * bf[ 1 ];
			set( bf , ( float ) ePrime , ( float ) fPrime , 0 );
			mvmulAffine( vehicle.basis.getEFGToXYZDirect( ) , bf );
			normalize3( bf );
		}
		float remainingDist = Math.abs( vehicle.velocity * timestep );
		
		while( remainingDist > 0 )
		{
			set( p1 , vehicle.location );
			set( v1 , vehicle.basisForward );
			
			if( vehicle.velocity < 0 )
			{
				negate3( v1 );
			}
			
			mpmul( vehicle.basis.getXYZToUVNDirect( ) , p1 );
			mvmulAffine( vehicle.basis.getXYZToUVNDirect( ) , v1 );
			p1[ 2 ] = v1[ 2 ] = 0;
			
			intersector.intersect( p1[ 0 ] , p1[ 1 ] , v1[ 0 ] , v1[ 1 ] );
			if( Double.isNaN( intersector.t[ 0 ] ) || Double.isInfinite( intersector.t[ 0 ] ) || intersector.t[ 0 ] <= 0 )
			{
				break;
			}
			
			if( intersector.t[ 0 ] > remainingDist )
			{
				p1[ 0 ] += remainingDist * v1[ 0 ];
				p1[ 1 ] += remainingDist * v1[ 1 ];
				p1[ 2 ] += remainingDist * v1[ 2 ];
				
				vehicle.basis.interpolateNormals( ( float ) p1[ 0 ] , ( float ) p1[ 1 ] , vehicle.basisUp );
				normalize3( vehicle.basisUp );
				mpmul( vehicle.basis.getUVNToXYZDirect( ) , p1 , vehicle.location );
				remainingDist = 0;
			}
			else
			{
				remainingDist -= intersector.t[ 0 ];
				Edge edge = vehicle.basis.createEdge( intersector.edgeIndices[ 0 ] );
				
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
				
				if( nextTriangle != null )
				{
					set( p1 , vehicle.location );
					set( v1 , vehicle.basisForward );
					
					float advance = Math.signum( vehicle.velocity ) * intersector.t[ 0 ];
					p1[ 0 ] += advance * v1[ 0 ];
					p1[ 1 ] += advance * v1[ 1 ];
					p1[ 2 ] += advance * v1[ 2 ];
					
					mvmulAffine( vehicle.basis.getEFGToXYZDirect( ) , 0 , 0 , 1 , v2 );
					
					vehicle.basis.set( vehicle.currentZone.getVertBuffer( ) , nexti0 , nexti1 , nexti2 );
					
					mvmulAffine( vehicle.basis.getEFGToXYZDirect( ) , 0 , 0 , 1 , v3 );
					
					tc.orientInPlace( v2 , v3 , orient );
					mvmulAffine( orient , v1 );
					
					mpmul( vehicle.basis.getXYZToUVNDirect( ) , p1 );
					mvmulAffine( vehicle.basis.getXYZToUVNDirect( ) , v1 );
					
					vehicle.basis.interpolateNormals( ( float ) p1[ 0 ] , ( float ) p1[ 1 ] , v2 );
					
					p1[ 1 ] = 0;
					p1[ 2 ] = 0;
					v1[ 2 ] = 0;
					
					mpmul( vehicle.basis.getUVNToXYZDirect( ) , p1 );
					mvmulAffine( vehicle.basis.getUVNToXYZDirect( ) , v1 );
					
					normalize3( v1 );
					normalize3( v2 );
					normalize3( v3 );
					
					set( vehicle.location , p1 );
					set( vehicle.basisForward , v1 );
					set( vehicle.basisUp , v2 );
				}
				else
				{
					vehicle.velocity = 0;
				}
			}
		}
		
		set( v2 , vehicle.modelUp );
		set( v5 , vehicle.basisUp );
		set( v6 , vehicle.basisForward );
		
		if( epsilonEquals( v2 , v5 , 0.1f ) )
		{
			tc.orientInPlace( v2 , v5 , orient );
			set( vehicle.modelUp , v5 );
			set( vehicle.modelForward , v6 );
		}
		else
		{
			set( v3 , vehicle.modelForward );
			
			cross( v2 , v3 , v1 );
			normalize3( v1 );
			cross( v5 , v6 , v4 );
			normalize3( v4 );
			
			cross( v1 , v4 , v1 );
			
			double rotationRate = Math.PI * 2;
			double planeRotationAmount = rotationRate * timestep;
			double inPlaneRotationAmount = planeRotationAmount;
			
			double targetPlaneRotation = Math.asin( length3( v1 ) );
			
			if( targetPlaneRotation > 0 )
			{
				rotate( v2 , v1 , targetPlaneRotation );
				rotate( v3 , v1 , targetPlaneRotation );
			}
			
			cross( v2 , v5 , v4 );
			double targetInPlaneRotation = Math.asin( length3( v4 ) );
			
			set( v2 , vehicle.modelUp );
			set( v3 , vehicle.modelForward );
			
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
				tc.orientInPlace( v2 , v5 , orient );
				set( vehicle.modelUp , v5 );
				set( vehicle.modelForward , v6 );
			}
			else
			{
				if( inPlaneRotationAmount > 0 )
				{
					rotate( v2 , v4 , inPlaneRotationAmount );
					rotate( v3 , v4 , inPlaneRotationAmount );
				}
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
