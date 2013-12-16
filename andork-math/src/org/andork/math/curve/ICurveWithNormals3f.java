package org.andork.math.curve;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public interface ICurveWithNormals3f
{
	
	public abstract Vector3f getNormal( float param , Vector3f result );

	public abstract Vector3f getBinormal( float param , Vector3f result );

	public abstract Vector3f getTangent( float param , Vector3f result );

	public abstract Point3f getPoint( float param , Point3f result );
	
}
