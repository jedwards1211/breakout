package org.andork.torquescape.model.xform;

import static org.andork.math3d.Vecmath.mmul;
import static org.andork.math3d.Vecmath.rotZ;
import static org.andork.math3d.Vecmath.setIdentity;

import org.andork.torquescape.model.param.IParamFn;

public class Helicizer implements IXformFn
{
	IParamFn	radiusFunction;
	IParamFn	angleFunction;
	
	float[ ]		x1	= new float[ 16 ];
	
	public Helicizer( IParamFn radiusFunction , IParamFn angleFunction )
	{
		super( );
		this.radiusFunction = radiusFunction;
		this.angleFunction = angleFunction;
	}
	
	@Override
	public float[ ] eval( float param , float[ ] out )
	{
		setIdentity( x1 );
		x1[ 12 ] = radiusFunction.eval( param );
		
		setIdentity( out );
		rotZ( out , angleFunction.eval( param ) );
		mmul( out , x1 , out );
		
		return out;
	}
	
}
