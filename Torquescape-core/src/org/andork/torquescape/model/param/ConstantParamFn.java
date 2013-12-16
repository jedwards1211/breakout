package org.andork.torquescape.model.param;


public class ConstantParamFn implements IParamFn
{
	private float constant;
	
	public ConstantParamFn( float constant )
	{
		super( );
		this.constant = constant;
	}

	@Override
	public float eval( float param )
	{
		return constant;
	}
}
