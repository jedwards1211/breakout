package org.andork.torquescape.model.xform;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.j3d.math.TransformComputer3f;
import org.andork.vecmath.VecmathUtils;

public class Ellipse implements IXformFunction
{
	private Point3f		origin	= new Point3f( );
	private Vector3f	axis	= new Vector3f( );
	private Vector3f	span1	= new Vector3f( );
	private Vector3f	span2	= new Vector3f( );
	
	public Ellipse( Point3f origin , Vector3f axis , Vector3f span1 , Vector3f span2 )
	{
		this.origin.set( origin );
		this.axis.set( axis );
		this.span1.set( span1 );
		this.span2.set( span2 );
	}
	
	@Override
	public Transform3D eval( float param , J3DTempsPool pool , Transform3D out )
	{
		TransformComputer3f tc = pool.getTransformComputer3f( );
		
		float sin = ( float ) Math.sin( param );
		float cos = ( float ) Math.cos( param );
		
		Point3f p = pool.getPoint3f( );
		p.set( span1.length( ) * cos , 0 , span2.length( ) * sin );
		Vector3f zv = pool.getVector3f( );
		zv.set( span1.length( ) * -sin , 0 , span2.length( ) * cos );
		zv.normalize( );
		Vector3f xv = pool.getVector3f( );
		xv.set( zv.z , 0 , -zv.x );
		
		Transform3D x1 = pool.getTransform3D( );
		tc.orient( VecmathUtils.ZEROF , VecmathUtils.UNIT_ZF , VecmathUtils.UNIT_XF , p , zv , xv , x1 );
		tc.orient( VecmathUtils.ZEROF , VecmathUtils.UNIT_XF , VecmathUtils.UNIT_YF , origin , span1 , span2 , out );
		out.mul( x1 );
		
		pool.release( x1 );
		pool.release( tc );
		pool.release( p );
		pool.release( xv );
		pool.release( zv );
		
		return out;
	}
}
