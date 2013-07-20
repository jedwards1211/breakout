package org.andork.torquescape.model.xform;

import static org.andork.vecmath.RawFloatVecmath.mmul;
import static org.andork.vecmath.RawFloatVecmath.rotZ;
import static org.andork.vecmath.RawFloatVecmath.setIdentity;

import org.andork.torquescape.model.param.IParamFunction;

public class Helicizer implements IXformFunction
{
	IParamFunction	radiusFunction;
	IParamFunction	angleFunction;
	
	float[ ]		x1	= new float[ 16 ];
	
	public Helicizer( IParamFunction radiusFunction , IParamFunction angleFunction )
	{
		super( );
		this.radiusFunction = radiusFunction;
		this.angleFunction = angleFunction;
	}
	
	@Override
	public float[ ] eval( float param , float[ ] out )
	{
		setIdentity( x1 );
		x1[ 3 ] = radiusFunction.eval( param );
		
		setIdentity( out );
		rotZ( out , angleFunction.eval( param ) );
		mmul( out , x1 , out );
		
		return out;
	}
	
}
