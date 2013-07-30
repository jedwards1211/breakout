package org.andork.torquescape.control;

public class ControlState
{
	public long		lastUpdate;
	public float	acceleration;
	public float	braking;
	public float	leftTurning;
	public float	rightTurning;
	
	public boolean	upPressed;
	public boolean	downPressed;
	public boolean	leftPressed;
	public boolean	rightPressed;
	
	public void update( long time )
	{
		long lastUpdate = this.lastUpdate;
		double timestep = ( time - lastUpdate ) / 1e9;
		this.lastUpdate = time;
		
		if( lastUpdate != 0 )
		{
			if( upPressed )
			{
				acceleration += timestep;
			}
			if( downPressed )
			{
				braking += timestep;
			}
			if( leftPressed )
			{
				leftTurning += timestep;
			}
			if( rightPressed )
			{
				rightTurning += timestep;
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