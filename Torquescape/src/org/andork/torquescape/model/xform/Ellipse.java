package org.andork.torquescape.model.xform;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.TransformComputer3f;
import org.andork.vecmath.VecmathUtils;

public class Ellipse implements IXformFunction
{
	private Point3f		origin	= new Point3f( );
	private Vector3f	axis	= new Vector3f( );
	private Vector3f	span1	= new Vector3f( );
	private Vector3f	span2	= new Vector3f( );
	
	Point3f				p		= new Point3f( );
	Vector3f			zv		= new Vector3f( );
	Vector3f			xv		= new Vector3f( );
	Transform3D			x1		= new Transform3D( );
	Transform3D			x2		= new Transform3D( );
	TransformComputer3f	tc		= new TransformComputer3f( );
	
	public Ellipse( Point3f origin , Vector3f axis , Vector3f span1 , Vector3f span2 )
	{
		this.origin.set( origin );
		this.axis.set( axis );
		this.span1.set( span1 );
		this.span2.set( span2 );
	}
	
	@Override
	public float[ ] eval( float param , float[ ] out )
	{
		float sin = ( float ) Math.sin( param );
		float cos = ( float ) Math.cos( param );
		
		p.set( span1.length( ) * cos , 0 , span2.length( ) * sin );
		zv.set( span1.length( ) * -sin , 0 , span2.length( ) * cos );
		zv.normalize( );
		xv.set( zv.z , 0 , -zv.x );
		
		tc.orient( VecmathUtils.ZEROF , VecmathUtils.UNIT_ZF , VecmathUtils.UNIT_XF , p , zv , xv , x1 );
		tc.orient( VecmathUtils.ZEROF , VecmathUtils.UNIT_XF , VecmathUtils.UNIT_YF , origin , span1 , span2 , x2 );
		x2.mul( x1 );
		
		x2.get( out );
		
		return out;
	}
}
