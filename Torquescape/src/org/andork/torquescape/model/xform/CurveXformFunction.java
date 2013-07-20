package org.andork.torquescape.model.xform;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.TransformComputer3f;
import org.andork.math3d.curve.ICurveWithNormals3f;
import org.andork.vecmath.VecmathUtils;

public class CurveXformFunction implements IXformFunction
{
	private ICurveWithNormals3f	curve;
	
	private Point3f				p		= new Point3f( );
	private Vector3f			t		= new Vector3f( );
	private Vector3f			n		= new Vector3f( );
	
	private TransformComputer3f	tc		= new TransformComputer3f( );
	
	private Transform3D			xform	= new Transform3D( );
	
	public CurveXformFunction( ICurveWithNormals3f curve )
	{
		this.curve = curve;
	}
	
	@Override
	public float[ ] eval( float param , float[ ] out )
	{
		
		curve.getPoint( param , p );
		curve.getTangent( param , t );
		curve.getBinormal( param , n );
		
		tc.orient( VecmathUtils.ZEROF , VecmathUtils.UNIT_ZF , VecmathUtils.UNIT_XF , p , t , n , xform );
		
		xform.get( out );
		
		return out;
	}
}
