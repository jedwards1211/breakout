package org.andork.torquescape.control;

import org.andork.torquescape.model.Zone;

public class Vehicle
{
	public Zone					currentZone;
	public int					indexInZone;
	
	public final TriangleBasis	basis						= new TriangleBasis( );
	
	public final double[ ]		temp						= new double[ 3 ];
	
	public final double[ ]		location					= new double[ 3 ];
	public final double[ ]		basisForward				= new double[ 3 ];
	public final double[ ]		basisUp						= new double[ 3 ];
	public final double[ ]		modelForward				= new double[ 3 ];
	public final double[ ]		modelUp						= new double[ 3 ];
	
	public double				velocity					= 0;
	public double				forwardAcceleration			= 5;
	public double				maxForwardVelocity			= 50;
	public double				brakeDeceleration			= 30;
	public double				reverseAcceleration			= 5;
	public double				maxReverseVelocity			= 5;
	public double				naturalDeceleration			= 15;
	
	public double				angularVelocity				= 0;
	public double				maxAngularVelocity			= ( double ) Math.PI / 2;
	public double				angularAcceleration			= ( double ) Math.PI * 8;
	public double				naturalAngularDeceleration	= ( double ) Math.PI * 8;
	
	public void updateVelocity( double timestep , double forwardTime , double reverseTime , double leftTime , double rightTime )
	{
		velocity += forwardAcceleration * forwardTime;
		double reverseAmount;
		
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
		
		double naturalDecelAmount = ( timestep - forwardTime - reverseTime ) * naturalDeceleration;
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
		double angularDecelAmount = naturalAngularDeceleration * ( timestep - leftTime - rightTime );
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
