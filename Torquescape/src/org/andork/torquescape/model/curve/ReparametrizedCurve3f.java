package org.andork.torquescape.model.curve;

import javax.media.j3d.Transform3D;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.math3d.curve.ICurve3f;
import org.andork.torquescape.model.param.IParamFunction;

public class ReparametrizedCurve3f implements ICurve3f
{
	public ReparametrizedCurve3f( ICurve3f wrapped , IParamFunction paramFunction )
	{
		super( );
		this.wrapped = wrapped;
		this.paramFunction = paramFunction;
	}

	private ICurve3f	wrapped;
	private IParamFunction	paramFunction;
	
	@Override
	public Transform3D eval( float param , J3DTempsPool pool , Transform3D out )
	{
		return wrapped.eval( paramFunction.eval( param ) , pool , out );
	}
}
