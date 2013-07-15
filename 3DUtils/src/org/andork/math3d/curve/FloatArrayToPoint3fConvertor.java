package org.andork.math3d.curve;

import javax.vecmath.Point3f;

public class FloatArrayToPoint3fConvertor implements PointTypeConvertor<float[ ], Point3f>
{
	@Override
	public void convert( float[ ] in , Point3f out )
	{
		out.x = in[ 0 ];
		out.y = in[ 1 ];
		out.z = in[ 2 ];
	}
}
