package org.andork.math3d.curve;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public interface ICurveWithNormals3f
{
	
	public abstract Vector3f getNormalY( float param , Vector3f result );

	public abstract Vector3f getNormalX( float param , Vector3f result );

	public abstract Vector3f getTangent( float param , Vector3f result );

	public abstract Point3f getPoint( float param , Point3f result );
	
}
