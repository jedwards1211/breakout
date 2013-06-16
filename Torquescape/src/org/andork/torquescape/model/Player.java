package org.andork.torquescape.model;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class Player
{
	final TriangleBasis	basis						= new TriangleBasis( );
	
	final Vector3f		temp						= new Vector3f( );
	
	final Point3f		location					= new Point3f( );
	final Vector3f		basisForward				= new Vector3f( );
	final Vector3f		basisUp						= new Vector3f( );
	final Vector3f		cameraUp					= new Vector3f( );
	final Vector3f		modelForward				= new Vector3f( );
	final Vector3f		modelUp						= new Vector3f( );
	
	float				velocity					= 0;
	float				forwardAcceleration			= 5;
	float				maxForwardVelocity			= 50;
	float				brakeDeceleration			= 30;
	float				reverseAcceleration			= 5;
	float				maxReverseVelocity			= 5;
	float				naturalDeceleration			= 15;
	
	float				angularVelocity				= 0;
	float				maxAngularVelocity			= ( float ) Math.PI / 2;
	float				angularAcceleration			= ( float ) Math.PI * 8;
	float				naturalAngularDeceleration	= ( float ) Math.PI * 8;
	
	public void setBasis( Triangle triangle )
	{
		basis.set( triangle );
	}
	
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
	
	public void getLocation( Point3f location )
	{
		location.set( this.location );
	}
	
	public void getBasisForward( Vector3f forward )
	{
		forward.set( this.basisForward );
	}
	
	public void getModelForward( Vector3f forward )
	{
		forward.set( this.modelForward );
	}
	
	public void getModelUp( Vector3f up )
	{
		up.set( this.modelUp );
	}
	
	public void getCameraUp( Vector3f cameraUp )
	{
		cameraUp.set( this.cameraUp );
	}
	
	public void getBasisUp( Vector3f targetUp )
	{
		targetUp.set( this.basisUp );
	}
	
	public float getVelocity( )
	{
		return velocity;
	}
	
	public void setVelocity( float velocity )
	{
		this.velocity = velocity;
	}
	
	public float getAngularVelocity( )
	{
		return angularVelocity;
	}
	
	public void setAngularVelocity( float angularVelocity )
	{
		this.angularVelocity = angularVelocity;
	}
	
	public void setBasisForward( Vector3f forward )
	{
		this.basisForward.set( forward );
	}
	
	public void setModelForward( Vector3f forward )
	{
		this.modelForward.set( forward );
	}
	
	public void setModelUp( Vector3f up )
	{
		this.modelUp.set( up );
	}
	
	public void setCameraUp( Vector3f cameraUp )
	{
		this.cameraUp.set( cameraUp );
	}
	
	public void setBasisUp( Vector3f targetUp )
	{
		this.basisUp.set( targetUp );
	}
	
	public void setLocation( Point3f location )
	{
		this.location.set( location );
	}
}
