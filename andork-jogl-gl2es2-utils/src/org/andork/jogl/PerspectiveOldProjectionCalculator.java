package org.andork.jogl;

import static org.andork.math3d.Vecmath.perspective;

public class PerspectiveOldProjectionCalculator implements OldProjectionCalculator
{
	public float	fovAngle;
	public float	zNear;
	public float	zFar;
	
	public PerspectiveOldProjectionCalculator( float fov , float zNear , float zFar )
	{
		super( );
		this.fovAngle = fov;
		this.zNear = zNear;
		this.zFar = zFar;
	}
	
	@Override
	public void calculate( int width , int height , float[ ] pOut )
	{
		perspective( pOut , fovAngle , ( float ) width / height , zNear , zFar );
	}
}
