package org.andork.torquescape.control;

import org.andork.torquescape.model.Zone;

public class Vehicle
{
	public Zone					currentZone;
	public int					indexInZone;
	
	public final TriangleBasis	basis						= new TriangleBasis( );
	
	public final float[ ]		temp						= new float[ 3 ];
	
	public final float[ ]		location					= new float[ 3 ];
	public final float[ ]		basisForward				= new float[ 3 ];
	public final float[ ]		basisUp						= new float[ 3 ];
	public final float[ ]		modelForward				= new float[ 3 ];
	public final float[ ]		modelUp						= new float[ 3 ];
	
	public float				velocity					= 0;
	public float				forwardAcceleration			= 5;
	public float				maxForwardVelocity			= 50;
	public float				brakeDeceleration			= 30;
	public float				reverseAcceleration			= 5;
	public float				maxReverseVelocity			= 5;
	public float				naturalDeceleration			= 15;
	
	public float				angularVelocity				= 0;
	public float				maxAngularVelocity			= ( float ) Math.PI / 2;
	public float				angularAcceleration			= ( float ) Math.PI * 8;
	public float				naturalAngularDeceleration	= ( float ) Math.PI * 8;
	
	public void updateVelocity( float timestep , float forwardTime , float reverseTime , float leftTime , float rightTime )
	{
		velocity += forwardAcceleration * forwardTime;
		float reverseAmount;
		
		if( velocity > 0 )
		{
			reverseAmount = brakeDeceleration * reverseTime;
			if( reverseAmount > velocity )
			{
				reverseAmount -= velocity;
				velocity = 0;
				reverseAmount = reverseAcceleration * reverseAmount / brakeDeceleration;
			}
			else
			{
				velocity -= reverseAmount;
				reverseAmount = 0;
			}
		}
		else
		{
			reverseAmount = reverseAcceleration * reverseTime;
		}
		if( velocity <= 0 )
		{
			velocity -= reverseAmount;
		}
		
		float naturalDecelAmount = ( timestep - forwardTime - reverseTime ) * naturalDeceleration;
		if( naturalDecelAmount > Math.abs( velocity ) )
		{
			velocity = 0;
		}
		else
		{
			velocity -= Math.signum( velocity ) * naturalDecelAmount;
		}
		
		velocity = Math.max( -maxReverseVelocity , Math.min( maxForwardVelocity , velocity ) );
		
		angularVelocity += angularAcceleration * ( leftTime - rightTime );
		float angularDecelAmount = naturalAngularDeceleration * ( timestep - leftTime - rightTime );
		if( angularDecelAmount > Math.abs( angularVelocity ) )
		{
			angularVelocity = 0;
		}
		else
		{
			angularVelocity -= Math.signum( angularVelocity ) * angularDecelAmount;
		}
		angularVelocity = Math.signum( angularVelocity ) * Math.min( maxAngularVelocity , Math.abs( angularVelocity ) );
	}
}
