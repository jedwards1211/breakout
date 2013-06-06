package org.andork.manifold.model;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Player
{
	final TriangleBasis	basis						= new TriangleBasis( );
	
	final Vector3d		temp						= new Vector3d( );
	
	final Point3d		location					= new Point3d( );
	final Vector3d		basisForward				= new Vector3d( );
	final Vector3d		basisUp						= new Vector3d( );
	final Vector3d		cameraUp					= new Vector3d( );
	final Vector3d		modelForward				= new Vector3d( );
	final Vector3d		modelUp						= new Vector3d( );
	
	double				velocity					= 0;
	double				forwardAcceleration			= 5;
	double				maxForwardVelocity			= 50;
	double				brakeDeceleration			= 30;
	double				reverseAcceleration			= 5;
	double				maxReverseVelocity			= 5;
	double				naturalDeceleration			= 15;
	
	double				angularVelocity				= 0;
	double				maxAngularVelocity			= Math.PI / 2;
	double				angularAcceleration			= Math.PI * 8;
	double				naturalAngularDeceleration	= Math.PI * 8;
	
	public void setBasis( Triangle triangle )
	{
		basis.set( triangle );
	}
	
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
	
	public void getLocation( Point3d location )
	{
		location.set( this.location );
	}
	
	public void getBasisForward( Vector3d forward )
	{
		forward.set( this.basisForward );
	}
	
	public void getModelForward( Vector3d forward )
	{
		forward.set( this.modelForward );
	}
	
	public void getModelUp( Vector3d up )
	{
		up.set( this.modelUp );
	}
	
	public void getCameraUp( Vector3d cameraUp )
	{
		cameraUp.set( this.cameraUp );
	}
	
	public void getBasisUp( Vector3d targetUp )
	{
		targetUp.set( this.basisUp );
	}
	
	public double getVelocity( )
	{
		return velocity;
	}
	
	public void setVelocity( double velocity )
	{
		this.velocity = velocity;
	}
	
	public double getAngularVelocity( )
	{
		return angularVelocity;
	}
	
	public void setAngularVelocity( double angularVelocity )
	{
		this.angularVelocity = angularVelocity;
	}
	
	public void setBasisForward( Vector3d forward )
	{
		this.basisForward.set( forward );
	}
	
	public void setModelForward( Vector3d forward )
	{
		this.modelForward.set( forward );
	}
	
	public void setModelUp( Vector3d up )
	{
		this.modelUp.set( up );
	}
	
	public void setCameraUp( Vector3d cameraUp )
	{
		this.cameraUp.set( cameraUp );
	}
	
	public void setBasisUp( Vector3d targetUp )
	{
		this.basisUp.set( targetUp );
	}
	
	public void setLocation( Point3d location )
	{
		this.location.set( location );
	}
}
