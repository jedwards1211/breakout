package org.andork.torquescape.model.old.xform;

import org.andork.torquescape.model.old.param.IParamFunction;

public class Reparametrizer implements IXformFunction
{
	public Reparametrizer( IXformFunction wrapped , IParamFunction paramFunction )
	{
		super( );
		this.wrapped = wrapped;
		this.paramFunction = paramFunction;
	}
	
	private IXformFunction	wrapped;
	private IParamFunction	paramFunction;
	
	@Override
	public float[ ] eval( float param , float[ ] out )
	{
		return wrapped.eval( paramFunction.eval( param ) , out );
	}
}
