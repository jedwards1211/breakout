package org.andork.torquescape.model.param;

public class CosParamFn implements IParamFn
{
	private float	hiParam;
	private float	loParam;
	private float	hiValue;
	private float	loValue;
	
	public CosParamFn( float hiParam , float loParam , float hiValue , float loValue )
	{
		super( );
		this.hiParam = hiParam;
		this.loParam = loParam;
		this.hiValue = hiValue;
		this.loValue = loValue;
	}

	@Override
	public float eval( float param )
	{
		param = ( param - loParam ) * ( float ) Math.PI / ( hiParam - loParam );
		return loValue + ( ( float ) Math.cos( param ) + 1 ) * ( hiValue - loValue ) / 2;
	}
}
