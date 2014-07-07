package org.andork.jogl;

import org.andork.jogl.neu.JoglDrawContext;
import org.andork.math3d.Vecmath;

public class OrthoProjectionCalculator implements ProjectionCalculator
{
	public float	zNear;
	public float	zFar;
	
	public float	hSpan;
	public float	vSpan;
	
	public OrthoProjectionCalculator( float hSpan , float zNear , float zFar )
	{
		this.hSpan = hSpan;
		this.zNear = zNear;
		this.zFar = zFar;
	}
	
	@Override
	public void calculate( JoglDrawContext dc , float[ ] pOut )
	{
		float width = dc.getWidth( );
		float height = dc.getHeight( );
		float left, right, bottom, top;
		if( vSpan / hSpan > height / width )
		{
			top = vSpan / 2;
			bottom = -top;
			right = top * width / height;
			left = -right;
		}
		else
		{
			right = hSpan / 2;
			left = -right;
			top = right * height / width;
			bottom = -top;
		}
		Vecmath.ortho( pOut , left , right , bottom , top , zNear , zFar );
	}
}
