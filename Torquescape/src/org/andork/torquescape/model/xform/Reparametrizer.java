package org.andork.torquescape.model.xform;

import javax.media.j3d.Transform3D;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.torquescape.model.param.IParamFunction;

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
	public Transform3D eval( float param , J3DTempsPool pool , Transform3D out )
	{
		return wrapped.eval( paramFunction.eval( param ) , pool , out );
	}
}
