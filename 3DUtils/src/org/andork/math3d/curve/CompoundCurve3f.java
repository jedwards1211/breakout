package org.andork.math3d.curve;

import javax.media.j3d.Transform3D;

import org.andork.j3d.math.J3DTempsPool;

public class CompoundCurve3f implements ICurve3f
{
	private ICurve3f[] curves;
	
	public CompoundCurve3f(ICurve3f... curves) {
		this.curves = curves;
	}
	
	@Override
	public Transform3D eval( float param , J3DTempsPool pool , Transform3D out )
	{
		out.setIdentity( );
		
		Transform3D x1 = pool.getTransform3D( );
		
		for (ICurve3f curve : curves) {
			curve.eval( param , pool , x1);
			out.mul( x1 );
		}
		
		return out;
	}
	
}
