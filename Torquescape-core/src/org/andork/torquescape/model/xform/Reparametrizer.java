package org.andork.torquescape.model.xform;

import org.andork.torquescape.model.param.IParamFn;

public class Reparametrizer implements IXformFn
{
	public Reparametrizer( IXformFn wrapped , IParamFn paramFunction )
	{
		super( );
		this.wrapped = wrapped;
		this.paramFunction = paramFunction;
	}
	
	private IXformFn	wrapped;
	private IParamFn	paramFunction;
	
	@Override
	public float[ ] eval( float param , float[ ] out )
	{
		return wrapped.eval( paramFunction.eval( param ) , out );
	}
}
