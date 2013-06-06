package org.andork.math3d.curve;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public interface ICalcuatingCurve3f extends ICurve3f
{
	public Point3f getPoint( float param , float x , float y , float tangent , Point3f out );

	public Vector3f getOffset( float param , float x , float y , float tangent , Vector3f out );
}
