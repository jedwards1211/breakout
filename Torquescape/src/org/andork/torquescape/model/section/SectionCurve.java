package org.andork.torquescape.model.section;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.vecmath.VecmathUtils;

public class SectionCurve
{
	public final Vector3f	outsideDirection	= new Vector3f( );
	public final Point3f[ ]	points;
	public final boolean[ ]	smoothFlags;
	
	public SectionCurve( int pointCount )
	{
		points = VecmathUtils.allocPoint3fArray( pointCount );
		smoothFlags = new boolean[ pointCount ];
	}
}
