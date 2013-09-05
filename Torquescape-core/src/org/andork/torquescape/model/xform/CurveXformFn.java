package org.andork.torquescape.model.xform;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.math.curve.ICurveWithNormals3f;
import org.andork.math.curve.TransformComputer3f;
import org.andork.vecmath.VecmathUtils;

public class CurveXformFn implements IXformFn
{
	private ICurveWithNormals3f	curve;
	
	private Point3f				p		= new Point3f( );
	private Vector3f			t		= new Vector3f( );
	private Vector3f			n		= new Vector3f( );
	
	private TransformComputer3f	tc		= new TransformComputer3f( );
	
	private Matrix4f			xform	= new Matrix4f( );
	
	public CurveXformFn( ICurveWithNormals3f curve )
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
		
		VecmathUtils.toArray( xform , out );
		
		return out;
	}
}
