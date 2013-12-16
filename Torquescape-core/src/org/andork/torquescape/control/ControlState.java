package org.andork.torquescape.control;


public class ControlState
{
	public long		lastUpdate;
	public float	acceleration;
	public float	braking;
	public float	leftTurning;
	public float	rightTurning;
	
	public float	forwardBackward;
	public float	rightLeft;
	
	public void update( long time )
	{
		long lastUpdate = this.lastUpdate;
		double timestep = ( time - lastUpdate ) / 1e9;
		this.lastUpdate = time;
		
		if( lastUpdate != 0 )
		{
			if( forwardBackward > 0 )
			{
				acceleration += forwardBackward * timestep;
			}
			else
			{
				braking -= forwardBackward * timestep;
			}
			if( rightLeft < 0 )
			{
				leftTurning -= rightLeft * timestep;
			}
			else
			{
				rightTurning += rightLeft * timestep;
			}
		}
	}
	
	public void clear( )
	{
		acceleration = 0;
		braking = 0;
		leftTurning = 0;
		rightTurning = 0;
	}
}