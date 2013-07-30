package org.andork.torquescape.model.old.xform;

import static org.andork.vecmath.FloatArrayVecmath.mmul;
import static org.andork.vecmath.FloatArrayVecmath.rotZ;
import static org.andork.vecmath.FloatArrayVecmath.setIdentity;

import org.andork.torquescape.model.old.param.IParamFunction;

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
