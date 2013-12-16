package org.andork.torquescape.model.param;

public class IdentityParamFn implements IParamFn
{
	@Override
	public float eval( float param )
	{
		return param;
	}
}
