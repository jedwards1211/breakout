package org.andork.torquescape.model.old.xform;

import static org.andork.vecmath.FloatArrayVecmath.setIdentity;

import org.andork.torquescape.model.old.param.IParamFunction;

public class Bloater implements IXformFunction
{
	private IParamFunction	xScaleFunction;
	private IParamFunction	yScaleFunction;
	private IParamFunction	zScaleFunction;
	
	public Bloater( IParamFunction scaleFunction )
	{
		this( scaleFunction , scaleFunction , scaleFunction );
	}
	
	public Bloater( IParamFunction xScaleFunction , IParamFunction yScaleFunction , IParamFunction zScaleFunction )
	{
		super( );
		this.xScaleFunction = xScaleFunction;
		this.yScaleFunction = yScaleFunction;
		this.zScaleFunction = zScaleFunction;
	}
	
	@Override
	public float[ ] eval( float param , float[ ] outXform )
	{
		setIdentity( outXform );
		outXform[ 0 ] = xScaleFunction.eval( param );
		outXform[ 5 ] = yScaleFunction.eval( param );
		outXform[ 10 ] = zScaleFunction.eval( param );
		return outXform;
	}
}
