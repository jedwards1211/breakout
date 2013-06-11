package org.andork.math3d.curve;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public interface ICurve3f
{
	
	public abstract Vector3f getNormalY( float depth , Vector3f result );

	public abstract Vector3f getNormalX( float depth , Vector3f result );

	public abstract Vector3f getTangent( float depth , Vector3f result );

	public abstract Point3f getPoint( float depth , Point3f result );
	
}
