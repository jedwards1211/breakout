package org.andork.j3d.newcamera;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class CameraPosition
{
	public Quat4f	orientation	= new Quat4f( 0 , 0 , 0 , 1 );
	public Vector3f	position	= new Vector3f( );
	
	public Quat4f	q1			= new Quat4f( );
	
	public void toMatrix( Matrix4f out )
	{
		out.set( orientation );
		out.setTranslation( position );
	}
	
	public void pan( float radians )
	{
		q1.set( new AxisAngle4f( 0 , 1 , 0 , radians ) );
		orientation.mul( q1 , orientation );
	}
	
	public void tilt( float radians )
	{
		q1.set( new AxisAngle4f( 1 , 0 , 0 , radians ) );
		orientation.mul( q1 );
	}
	
	public void move( float right , float up , float back )
	{
		q1.conjugate( orientation );
		
		Quat4f qright = new Quat4f( 1 , 0 , 0 , 0 );
		Quat4f qup = new Quat4f( 0 , 1 , 0 , 0 );
		Quat4f qback = new Quat4f( 0 , 0 , 1 , 0 );
		
		qright.mul( q1 );
		qright.mul( orientation , qright );
		qup.mul( q1 );
		qup.mul( orientation , qup );
		qback.mul( q1 );
		qback.mul( orientation , qback );
		
		position.x += qright.x * right + qup.x * up + qback.x * back;
		position.y += qright.y * right + qup.y * up + qback.y * back;
		position.z += qright.z * right + qup.z * up + qback.z * back;
	}
	
	public void setLocation( float x , float y , float z )
	{
		position.set( x , y , z );
	}
}
