package org.andork.torquescape.model.xform;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.math.curve.ICurveWithNormals3f;
import org.andork.vecmath.FloatOrientComputer;
import static org.andork.vecmath.VecmathUtils.*;

public class CurveXformFn implements IXformFn
{
	private ICurveWithNormals3f	curve;
	
	private Point3f				p	= new Point3f( );
	private Vector3f			t	= new Vector3f( );
	private Vector3f			n	= new Vector3f( );
	
	private float[ ]			pa	= new float[ 3 ];
	private float[ ]			ta	= new float[ 3 ];
	private float[ ]			na	= new float[ 3 ];
	
	private FloatOrientComputer	oc	= new FloatOrientComputer( );
	
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
		
		set( pa , p );
		set( ta , t );
		set( na , n );
		
		oc.orient( 0 , 0 , 0 , 0 , 0 , 1 , 1 , 0 , 0 ,
				pa , ta , na , out );
		
		return out;
	}
}
