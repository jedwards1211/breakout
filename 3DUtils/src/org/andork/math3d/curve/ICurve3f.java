package org.andork.math3d.curve;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public interface ICurve3f
{
	public float getLowerBound();
	
	public float getUpperBound();
	
	public Point3f getPoint(float param, Point3f out);
	
	public Vector3f getTangent(float param, Vector3f out);
	
	public Vector3f getNormalX(float param, Vector3f out);

	public Vector3f getNormalY(float param, Vector3f out); 
}
