package org.andork.torquescape.model.param;

public class IdentityParamFunction implements IParamFunction
{
	@Override
	public float eval( float param )
	{
		return param;
	}
}
