package org.andork.torquescape.model.xform;

import javax.media.j3d.Transform3D;

import org.andork.j3d.math.J3DTempsPool;

public class CompoundXformFunction implements IXformFunction
{
	private IXformFunction[] curves;
	
	public CompoundXformFunction(IXformFunction... curves) {
		this.curves = curves;
	}
	
	@Override
	public Transform3D eval( float param , J3DTempsPool pool , Transform3D out )
	{
		out.setIdentity( );
		
		Transform3D x1 = pool.getTransform3D( );
		
		for (IXformFunction curve : curves) {
			curve.eval( param , pool , x1);
			out.mul( x1 );
		}
		
		return out;
	}
	
}
