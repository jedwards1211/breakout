package org.andork.torquescape.model.section;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public interface ICrossSectionCurve
{
	public int getPointCount();
	
	public Point3f getPoint(int index, Point3f out);
	
	public Vector3f getPrevSegmentNormal(int index, Vector3f out);
	
	public Vector3f getNextSegmentNormal(int index, Vector3f out);
}
