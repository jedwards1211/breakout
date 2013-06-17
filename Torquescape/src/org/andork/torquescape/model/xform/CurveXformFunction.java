package org.andork.torquescape.model.xform;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.j3d.math.TransformComputer3f;
import org.andork.math3d.curve.ICurveWithNormals3f;
import org.andork.vecmath.VecmathUtils;

public class CurveXformFunction implements IXformFunction
{
	private ICurveWithNormals3f	curve;
	
	public CurveXformFunction( ICurveWithNormals3f curve )
	{
		this.curve = curve;
	}

	@Override
	public Transform3D eval( float param , J3DTempsPool pool , Transform3D out )
	{
		Point3f p = pool.getPoint3f( );
		Vector3f t = pool.getVector3f( );
		Vector3f n = pool.getVector3f( );
		TransformComputer3f tc = pool.getTransformComputer3f( );
		
		curve.getPoint( param , p );
		curve.getTangent( param , t );
		curve.getNormalX( param , n );
		
		tc.orient( VecmathUtils.ZEROF , VecmathUtils.UNIT_ZF , VecmathUtils.UNIT_XF , p , t , n , out );
		
		pool.release( p );
		pool.release( t );
		pool.release( n );
		pool.release( tc );
		
		return out;
	}
}
