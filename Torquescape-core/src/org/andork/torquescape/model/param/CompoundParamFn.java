package org.andork.torquescape.model.param;


public class CompoundParamFn implements IParamFn
{
	private IParamFn[ ]	functions;
	
	public CompoundParamFn( IParamFn ... functions )
	{
		this.functions = functions;
	}
	
	@Override
	public float eval( float param )
	{
		float value = param;
		for( int i = functions.length - 1 ; i >= 0 ; i-- )
		{
			value = functions[ i ].eval( value );
		}
		return value;
	}
}
