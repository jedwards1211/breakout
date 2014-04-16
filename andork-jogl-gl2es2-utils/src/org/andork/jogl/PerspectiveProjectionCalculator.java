package org.andork.jogl;

import static org.andork.math3d.Vecmath.perspective;

public class PerspectiveProjectionCalculator implements ProjectionCalculator
{
	public float	fovAngle;
	public float	zNear;
	public float	zFar;
	
	public PerspectiveProjectionCalculator( float fov , float zNear , float zFar )
	{
		super( );
		this.fovAngle = fov;
		this.zNear = zNear;
		this.zFar = zFar;
	}
	
	@Override
	public void calculate( float width , float height , float[ ] pOut )
	{
		perspective( pOut , fovAngle , ( float ) width / height , zNear , zFar );
	}
}
