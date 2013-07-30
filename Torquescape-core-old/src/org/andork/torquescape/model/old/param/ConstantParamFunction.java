package org.andork.torquescape.model.old.param;


public class ConstantParamFunction implements IParamFunction
{
	private float constant;
	
	public ConstantParamFunction( float constant )
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
