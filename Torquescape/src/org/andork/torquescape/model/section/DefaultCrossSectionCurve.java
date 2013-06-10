package org.andork.torquescape.model.section;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.vecmath.VecmathUtils;

public class DefaultCrossSectionCurve implements ICrossSectionCurve
{
	private final Point3f[ ]	points;
	private final Vector3f[ ]	prevNormals;
	private final Vector3f[ ]	nextNormals;
	
	public DefaultCrossSectionCurve( int pointCount )
	{
		points = VecmathUtils.allocPoint3fArray( pointCount );
		prevNormals = VecmathUtils.allocVector3fArray( pointCount );
		nextNormals = VecmathUtils.allocVector3fArray( pointCount );
	}
	
	public void setPoint( int index , Point3f p )
	{
		points[ index ].set( p );
	}
	
	public void setPrevSegmentNormal( int index , Vector3f v )
	{
		prevNormals[ index ].set( v );
	}
	
	public void setNextSegmentNormal( int index , Vector3f v )
	{
		nextNormals[ index ].set( v );
	}
	
	@Override
	public int getPointCount( )
	{
		return points.length;
	}
	
	@Override
	public Point3f getPoint( int index , Point3f out )
	{
		out.set( points[ index ] );
		return out;
	}
	
	@Override
	public Vector3f getPrevSegmentNormal( int index , Vector3f out )
	{
		out.set( prevNormals[ index ] );
		return out;
	}
	
	@Override
	public Vector3f getNextSegmentNormal( int index , Vector3f out )
	{
		out.set( nextNormals[ index ] );
		return out;
	}
	
	public Point3f getPoint( int index )
	{
		return points[ index ];
	}
	
	public Vector3f getPrevSegmentNormal( int index )
	{
		return prevNormals[ index ];
	}
	
	public Vector3f getNextSegmentNormal( int index )
	{
		return nextNormals[ index ];
	}
}
