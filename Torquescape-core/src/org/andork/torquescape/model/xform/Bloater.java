package org.andork.torquescape.model.xform;

import static org.andork.vecmath.Vecmath.setIdentity;

import org.andork.torquescape.model.param.IParamFn;

public class Bloater implements IXformFn
{
	private IParamFn	xScaleFunction;
	private IParamFn	yScaleFunction;
	private IParamFn	zScaleFunction;
	
	public Bloater( IParamFn scaleFunction )
	{
		this( scaleFunction , scaleFunction , scaleFunction );
	}
	
	public Bloater( IParamFn xScaleFunction , IParamFn yScaleFunction , IParamFn zScaleFunction )
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
